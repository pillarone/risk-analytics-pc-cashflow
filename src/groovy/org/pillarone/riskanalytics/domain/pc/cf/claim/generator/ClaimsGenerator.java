package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.IRiskAllocatorStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGenerator extends Component implements IPerilMarker, ICorrelationMarker {

    private PeriodScope periodScope;
    private PeriodStore periodStore;
    private boolean globalRunOffAfterFirstPeriod  = true;

    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo
            = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);
    private PacketList<SystematicFrequencyPacket> inEventFrequencies = new PacketList<SystematicFrequencyPacket>(SystematicFrequencyPacket.class);

    private PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<SingleValuePacket> outClaimNumber = new PacketList<SingleValuePacket>(SingleValuePacket.class);

    // attritional, frequency average attritional, ...
    private ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker.class, "");
    private ConstrainedString parmReportingPattern = new ConstrainedString(IReportingPatternMarker.class, "");
    private ConstrainedMultiDimensionalParameter parmSeverityIndices = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER));
    private ComboBoxTableMultiDimensionalParameter parmUnderwritingSegments = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Underwriting Information"), IUnderwritingInfoMarker.class);
    private IClaimsGeneratorStrategy parmClaimsModel = ClaimsGeneratorType.getDefault();
    private IRiskAllocatorStrategy parmAssociateExposureInfo = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, new HashMap());

    protected void doCalculation() {
        List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();
        List<Factors> factors = IndexUtils.filterFactors(inFactors, parmSeverityIndices);
        int number = generateClaimsOfCurrentPeriod(claims, periodCounter, factors);
        developClaimsOfFormerPeriods(claims, periodCounter, factors);
        setTechnicalProperties(claims);
        outClaims.addAll(claims);
        outClaimNumber.add(new SingleValuePacket(number));
    }

    /**
     * @param claims
     * @param periodCounter
     * @param factors
     * @return number of generated claims
     */
    private int generateClaimsOfCurrentPeriod(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter, List<Factors> factors) {
        if (generateNewClaims()) {
            List uwFilterCriteria = (List) parmUnderwritingSegments.getValuesAsObjects(0, true);
            // a nominal ultimate is generated, therefore no factors are applied
            List<ClaimRoot> baseClaims = parmClaimsModel.calculateClaims(inUnderwritingInfo, uwFilterCriteria,
                    inEventSeverities, this, periodScope);
            baseClaims = parmClaimsModel.generateClaims(baseClaims, inUnderwritingInfo, null, uwFilterCriteria, inFactors, periodScope, inEventFrequencies, this);
            if (!baseClaims.isEmpty()) {
                baseClaims = parmAssociateExposureInfo.getAllocatedClaims(baseClaims, UnderwritingInfoUtils.filterUnderwritingInfo(inUnderwritingInfo, uwFilterCriteria));

                PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern, IPayoutPatternMarker.class);
                PatternPacket reportingPattern = PatternUtils.filterPattern(inPatterns, parmReportingPattern, IReportingPatternMarker.class);
                PatternUtils.synchronizePatterns(payoutPattern, reportingPattern);

                List<GrossClaimRoot> grossClaimRoots = new ArrayList<GrossClaimRoot>();
                for (ClaimRoot baseClaim : baseClaims) {
                    GrossClaimRoot grossClaimRoot = new GrossClaimRoot(baseClaim, payoutPattern, reportingPattern);
                    if (!grossClaimRoot.hasTrivialPayout()) {
                        // add claim only to period store if development is required
                        grossClaimRoots.add(grossClaimRoot);
                    }
                    claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, factors));
                }
                periodStore.put(GROSS_CLAIMS, grossClaimRoots);
            }
            return baseClaims.size();
        }
        return 0;
    }

    private boolean generateNewClaims() {
        return globalRunOffAfterFirstPeriod
                && periodScope.isFirstPeriod()
                || !globalRunOffAfterFirstPeriod;
    }

    private void developClaimsOfFormerPeriods(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter, List<Factors> factors) {
        if (!periodScope.isFirstPeriod()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            int latestFormerPeriodWithNewClaims = globalRunOffAfterFirstPeriod ? 0 : currentPeriod - 1;
            for (int period = 0; period <= latestFormerPeriodWithNewClaims; period++) {
                int periodOffset = currentPeriod - period;
                List<GrossClaimRoot> grossClaimRoots = (List<GrossClaimRoot>) periodStore.get(GROSS_CLAIMS, -periodOffset);
                if (grossClaimRoots != null) {
                    for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
                        claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, factors));
                    }
                }
            }
        }
    }

    // period store key
    private static final String GROSS_CLAIMS = "gross claims root";

    private void setTechnicalProperties(List<ClaimCashflowPacket> claims) {
        for (ClaimCashflowPacket claim : claims) {
            claim.setMarker(this);
            claim.origin = this;
        }
    }

    public IClaimsGeneratorStrategy getParmClaimsModel() {
        return parmClaimsModel;
    }

    public void setParmClaimsModel(IClaimsGeneratorStrategy parmClaimsModel) {
        this.parmClaimsModel = parmClaimsModel;
    }

    /**
     * claims which source is a covered line
     */
    public PacketList<ClaimCashflowPacket> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<ClaimCashflowPacket> outClaims) {
        this.outClaims = outClaims;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<PatternPacket> getInPatterns() {
        return inPatterns;
    }

    public void setInPatterns(PacketList<PatternPacket> inPatterns) {
        this.inPatterns = inPatterns;
    }

    public ConstrainedString getParmPayoutPattern() {
        return parmPayoutPattern;
    }

    public void setParmPayoutPattern(ConstrainedString parmPayoutPattern) {
        this.parmPayoutPattern = parmPayoutPattern;
    }

    public ConstrainedString getParmReportingPattern() {
        return parmReportingPattern;
    }

    public void setParmReportingPattern(ConstrainedString parmReportingPattern) {
        this.parmReportingPattern = parmReportingPattern;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    /**
     * this property is overwritten by the value set in GlobalParameters
     * @return
     */
    public boolean isGlobalRunOffAfterFirstPeriod() {
        return globalRunOffAfterFirstPeriod;
    }

    public void setGlobalRunOffAfterFirstPeriod (boolean globalRunOffAfterFirstPeriod ) {
        this.globalRunOffAfterFirstPeriod  = globalRunOffAfterFirstPeriod;
    }

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public ComboBoxTableMultiDimensionalParameter getParmUnderwritingSegments() {
        return parmUnderwritingSegments;
    }

    public void setParmUnderwritingSegments(ComboBoxTableMultiDimensionalParameter parmUnderwritingSegments) {
        this.parmUnderwritingSegments = parmUnderwritingSegments;
    }

    public PacketList<SingleValuePacket> getOutClaimNumber() {
        return outClaimNumber;
    }

    public void setOutClaimNumber(PacketList<SingleValuePacket> outClaimNumber) {
        this.outClaimNumber = outClaimNumber;
    }

    public ConstrainedMultiDimensionalParameter getParmSeverityIndices() {
        return parmSeverityIndices;
    }

    public void setParmSeverityIndices(ConstrainedMultiDimensionalParameter parmSeverityIndices) {
        this.parmSeverityIndices = parmSeverityIndices;
    }

    public PacketList<EventDependenceStream> getInEventSeverities() {
        return inEventSeverities;
    }

    public void setInEventSeverities(PacketList<EventDependenceStream> inEventSeverities) {
        this.inEventSeverities = inEventSeverities;
    }

    public PacketList<SystematicFrequencyPacket> getInEventFrequencies() {
        return inEventFrequencies;
    }

    public void setInEventFrequencies(PacketList<SystematicFrequencyPacket> inEventFrequencies) {
        this.inEventFrequencies = inEventFrequencies;
    }

    public IRiskAllocatorStrategy getParmAssociateExposureInfo() {
        return parmAssociateExposureInfo;
    }

    public void setParmAssociateExposureInfo(IRiskAllocatorStrategy parmAssociateExposureInfo) {
        this.parmAssociateExposureInfo = parmAssociateExposureInfo;
    }
}
