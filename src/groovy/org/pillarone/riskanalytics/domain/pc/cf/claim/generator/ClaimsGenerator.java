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
import org.pillarone.riskanalytics.domain.pc.cf.dependency.DependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.math.copula.ICorrelationMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGenerator extends Component implements IPerilMarker, ICorrelationMarker{

    private PeriodScope periodScope;
    private PeriodStore periodStore;
    private boolean globalGenerateNewClaimsInFirstPeriodOnly = true;

    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo
            = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    /**
     * needs to be connected only if the claims generator was selected as target in a copula
     */
    private PacketList<DependenceStream> inProbabilities = new PacketList<DependenceStream>(DependenceStream.class);

    private PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<SingleValuePacket> outClaimNumber = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<IndexPacket> outSeverityIndexApplied = new PacketList<IndexPacket>(IndexPacket.class);

    // attritional, frequency average attritional, ...
    private ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker.class, "");
    private ConstrainedString parmReportingPattern = new ConstrainedString(IReportingPatternMarker.class, "");
    private ConstrainedMultiDimensionalParameter parmSeveritiesIndices = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER));
    private ComboBoxTableMultiDimensionalParameter parmUnderwritingInformation = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Underwriting Information"), IUnderwritingInfoMarker.class);
    private IClaimsGeneratorStrategy parmClaimsModel = ClaimsGeneratorType.getDefault();

    protected void doCalculation() {
        List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();

        List<Factors> factors = IndexUtils.filterFactors(inFactors, parmSeveritiesIndices);
        int number = generateClaimsOfCurrentPeriod(claims, periodCounter, factors);
        developClaimsOfFormerPeriods(claims, periodCounter, factors);

        outClaims.addAll(claims);
        outClaimNumber.add(new SingleValuePacket(number));
        if (this.isSenderWired(outSeverityIndexApplied)) {
            outSeverityIndexApplied.add(IndexUtils.aggregate(factors, periodScope.getCurrentPeriodStartDate()));
        }
    }

    /**
     * @param claims
     * @param periodCounter
     * @return  number of claims
     */
    private int generateClaimsOfCurrentPeriod(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter, List<Factors> factors) {
        if (globalGenerateNewClaimsInFirstPeriodOnly
                && periodScope.isFirstPeriod()
                || !globalGenerateNewClaimsInFirstPeriodOnly) {
            List uwFilterCriteria = parmUnderwritingInformation.getValuesAsObjects();
            // a nominal ultimate is generated, therefore no factors are applied
            List<ClaimRoot> baseClaims = parmClaimsModel.generateClaims(inUnderwritingInfo, uwFilterCriteria, inFactors, periodScope);

            PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern);
            PatternPacket reportingPattern = PatternUtils.filterPattern(inPatterns, parmReportingPattern);

            List<GrossClaimRoot> grossClaimRoots = new ArrayList<GrossClaimRoot>();
            for (ClaimRoot baseClaim : baseClaims) {
                GrossClaimRoot grossClaimRoot = new GrossClaimRoot(baseClaim, payoutPattern, reportingPattern);
                if (!grossClaimRoot.hasTrivialPayout()) {
                    // add claim only to period store if development is required
                    grossClaimRoots.add(grossClaimRoot);
                }
                claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, factors, true));
            }
            periodStore.put(GROSS_CLAIMS, grossClaimRoots);
            return grossClaimRoots.size();
        }
        return 0;
    }

    private void developClaimsOfFormerPeriods(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter, List<Factors> factors) {
        if (!periodScope.isFirstPeriod()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            int latestFormerPeriodWithNewClaims = globalGenerateNewClaimsInFirstPeriodOnly ? 0 : currentPeriod - 1;
            for (int period = 0; period <= latestFormerPeriodWithNewClaims; period++) {
                int periodOffset = currentPeriod - period;
                List<GrossClaimRoot> grossClaimRoots = (List<GrossClaimRoot>) periodStore.get(GROSS_CLAIMS, -periodOffset);
                if (grossClaimRoots != null) {
                    for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
                        claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, factors, false));
                    }
                }
            }
        }
    }

    // period store key
    private static final String GROSS_CLAIMS = "gross claims root";


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

    /** this property is overwritten by the value set in GlobalParameters */
    public boolean isGlobalGenerateNewClaimsInFirstPeriodOnly() {
        return globalGenerateNewClaimsInFirstPeriodOnly;
    }

    public void setGlobalGenerateNewClaimsInFirstPeriodOnly(boolean globalGenerateNewClaimsInFirstPeriodOnly) {
        this.globalGenerateNewClaimsInFirstPeriodOnly = globalGenerateNewClaimsInFirstPeriodOnly;
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

    public ComboBoxTableMultiDimensionalParameter getParmUnderwritingInformation() {
        return parmUnderwritingInformation;
    }

    public void setParmUnderwritingInformation(ComboBoxTableMultiDimensionalParameter parmUnderwritingInformation) {
        this.parmUnderwritingInformation = parmUnderwritingInformation;
    }

    public PacketList<SingleValuePacket> getOutClaimNumber() {
        return outClaimNumber;
    }

    public void setOutClaimNumber(PacketList<SingleValuePacket> outClaimNumber) {
        this.outClaimNumber = outClaimNumber;
    }

    public ConstrainedMultiDimensionalParameter getParmSeveritiesIndices() {
        return parmSeveritiesIndices;
    }

    public void setParmSeveritiesIndices(ConstrainedMultiDimensionalParameter parmSeveritiesIndices) {
        this.parmSeveritiesIndices = parmSeveritiesIndices;
    }

    /** Indices applied to single claims may be different depending on the selected mode as interpolation is done
     *  by occurrence date of claim. */
    public PacketList<IndexPacket> getOutSeverityIndexApplied() {
        return outSeverityIndexApplied;
    }

    public void setOutSeverityIndexApplied(PacketList<IndexPacket> outSeverityIndexApplied) {
        this.outSeverityIndexApplied = outSeverityIndexApplied;
    }
}
