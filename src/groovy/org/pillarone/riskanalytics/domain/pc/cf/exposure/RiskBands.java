package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.DateFactors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The RiskBands class allows one to specify (in tabular format) a set of risk bands,
 * each (row) of which defines the values (columns):
 * - maximum sum insured (packet property: maxSumInsured)
 * - average sum insured (packet property: sumInsured)
 * - premium (lower limit; packet property: premium)
 * - number of policies (packet property: numberOfPolicies)
 *
 * An instance will emit outUnderwritingInfo packets, one per segment/band defined
 * (i.e. one underwriting packet for each row in the table), with the property names
 * indicated above. In addition, an origin pointing to the Riskband instance emitting the packet,
 * and a self-referential originalUnderwritingInfo property (i.e. pointing to the packet itself).
 *
 * Premium and policy index are applied at the 'written date'. Paid values are therefore not differently affected
 * by an index time series.
 *
 * @author martin.melchior (at) fhnw (dot) ch, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RiskBands extends Component implements IUnderwritingInfoMarker {

    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfo
            = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<SingleValuePacket> outPolicyIndexApplied = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<SingleValuePacket> outPremiumIndexApplied = new PacketList<SingleValuePacket>(SingleValuePacket.class);

    private boolean globalGenerateNewClaimsInFirstPeriodOnly = true;
    private ConstrainedString parmPremiumPattern = new ConstrainedString(IPremiumPatternMarker.class, "");
    private ConstrainedMultiDimensionalParameter parmPolicyIndices = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), PolicyIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(PolicyIndexSelectionTableConstraints.IDENTIFIER));
    private ConstrainedMultiDimensionalParameter parmPremiumIndices = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), PremiumIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(PremiumIndexSelectionTableConstraints.IDENTIFIER));

    private TableMultiDimensionalParameter parmUnderwritingInformation = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d, 0d, 0d}), TABLE_COLUMN_TITLES,
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    private IterationScope iterationScope;
    private IterationStore iterationStore;
    private PeriodStore periodStore;

    public void doCalculation() {
        fillIterationStore();
        List<UnderwritingInfoPacket> underwritingInfos = (List<UnderwritingInfoPacket>) iterationStore.get(UNDERWRITING_INFOS);
        PatternPacket premiumPattern = PatternUtils.filterPattern(inPatterns, parmPremiumPattern, IPremiumPatternMarker.class);
        PeriodScope periodScope = iterationScope.getPeriodScope();

        if (premiumPattern == null || premiumPattern.isTrivial()) {
            newBusinessInCurrentPeriod(underwritingInfos, periodScope);
        }
        else {
            newBusinessInCurrentPeriod(underwritingInfos, premiumPattern, periodScope);
            calculatePremiumPaidForBusinessOfPreviousPeriods(premiumPattern, periodScope);
        }
    }

    private void newBusinessInCurrentPeriod(List<UnderwritingInfoPacket> underwritingInfos, PatternPacket premiumPattern, PeriodScope periodScope) {
        if (globalGenerateNewClaimsInFirstPeriodOnly
                && iterationScope.getPeriodScope().isFirstPeriod()
                || !globalGenerateNewClaimsInFirstPeriodOnly) {
            List<DateFactors> dateFactorsList = premiumPattern.getDateFactorsForCurrentPeriod(periodScope.getPeriodCounter());
            List<Factors> policyFactors = IndexUtils.filterFactors(inFactors, parmPolicyIndices);
            List<Factors> premiumFactors = IndexUtils.filterFactors(inFactors, parmPremiumIndices);
            boolean positiveWrittenAmount = true;
            List<UnderwritingInfoPacket> modifiedUnderwritingInfoPackets = new ArrayList<UnderwritingInfoPacket>();
            for (DateFactors dateFactors : dateFactorsList) {
                if (policyFactors == null && premiumFactors == null) {
                    modifiedUnderwritingInfoPackets.addAll(underwritingInfos);
                }
                else {
                    Double policyFactor = IndexUtils.aggregateFactor(policyFactors, dateFactors.getDate());
                    Double premiumFactor = IndexUtils.aggregateFactor(premiumFactors, dateFactors.getDate()) * policyFactor;
                    for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                        UnderwritingInfoPacket modifiedUnderwritingInfo = underwritingInfo.withFactorsApplied(policyFactor, premiumFactor);
                        modifiedUnderwritingInfo.applyPattern(positiveWrittenAmount, dateFactors.getFactorIncremental());
                        modifiedUnderwritingInfoPackets.add(modifiedUnderwritingInfo);
                    }

                    positiveWrittenAmount = false;
                    if (isSenderWired(outPolicyIndexApplied)) {
                        outPolicyIndexApplied.add(new SingleValuePacket(policyFactor));
                    }
                    if (isSenderWired(outPremiumIndexApplied)) {
                        outPremiumIndexApplied.add(new SingleValuePacket(premiumFactor));
                    }
                }
            }
            if (!premiumPattern.isTrivial()) {
                periodStore.put(UNDERWRITING_INFOS, modifiedUnderwritingInfoPackets);
            }
            outUnderwritingInfo.addAll(modifiedUnderwritingInfoPackets);
        }
    }

    private void newBusinessInCurrentPeriod(List<UnderwritingInfoPacket> underwritingInfos, PeriodScope periodScope) {
        if (globalGenerateNewClaimsInFirstPeriodOnly
                && iterationScope.getPeriodScope().isFirstPeriod()
                || !globalGenerateNewClaimsInFirstPeriodOnly) {
            DateTime currentPeriodStartDate = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
            List<Factors> policyFactors = IndexUtils.filterFactors(inFactors, parmPolicyIndices);
            List<Factors> premiumFactors = IndexUtils.filterFactors(inFactors, parmPremiumIndices);
            if (policyFactors == null && premiumFactors == null) {
                outUnderwritingInfo.addAll(underwritingInfos);
            }
            else {
                Double policyFactor = IndexUtils.aggregateFactor(policyFactors, currentPeriodStartDate);
                Double premiumFactor = IndexUtils.aggregateFactor(premiumFactors, currentPeriodStartDate);
                for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                    UnderwritingInfoPacket modifiedUnderwritingInfo = underwritingInfo.withFactorsApplied(policyFactor, premiumFactor);
                    outUnderwritingInfo.add(modifiedUnderwritingInfo);
                }
                if (isSenderWired(outPolicyIndexApplied)) {
                    outPolicyIndexApplied.add(new SingleValuePacket(policyFactor));
                }
                if (isSenderWired(outPremiumIndexApplied)) {
                    outPremiumIndexApplied.add(new SingleValuePacket(premiumFactor));
                }
            }
        }
    }

    private void calculatePremiumPaidForBusinessOfPreviousPeriods(PatternPacket premiumPattern, PeriodScope periodScope) {
        if (!periodScope.isFirstPeriod() && !premiumPattern.isTrivial()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            int latestFormerPeriodWithNewClaims = globalGenerateNewClaimsInFirstPeriodOnly ? 0 : currentPeriod - 1;
            for (int period = 0; period <= latestFormerPeriodWithNewClaims; period++) {
                int periodOffset = currentPeriod - period;
                List<UnderwritingInfoPacket> underwritingInfos = (List<UnderwritingInfoPacket>) periodStore.get(UNDERWRITING_INFOS, -periodOffset);
                if (underwritingInfos == null || underwritingInfos.size() == 0) continue;
                DateTime inceptionDate = underwritingInfos.get(0).getExposure().getInceptionDate();
                List<DateFactors> dateFactorsList = premiumPattern.getDateFactorsForCurrentPeriod(inceptionDate, periodScope.getPeriodCounter());
                for (DateFactors dateFactors : dateFactorsList) {
                    for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                        UnderwritingInfoPacket modifiedUnderwritingInfo = (UnderwritingInfoPacket) underwritingInfo.clone();
                        modifiedUnderwritingInfo.applyPattern(false, dateFactors.getFactorIncremental());
                        outUnderwritingInfo.add(modifiedUnderwritingInfo);
                    }
                }
            }
        }
    }

    private void fillIterationStore() {
        if (iterationScope.isFirstIteration()) {
            int numberOfSegments = parmUnderwritingInformation.getRowCount();
            int columnIndexMaxSumInsured = parmUnderwritingInformation.getColumnIndex(MAXIMUM_SUM_INSURED);
            int columnIndexAverageSumInsured = parmUnderwritingInformation.getColumnIndex(AVERAGE_SUM_INSURED);
            int columnIndexPremium = parmUnderwritingInformation.getColumnIndex(PREMIUM);
            int columnIndexNumberOfPolicies = parmUnderwritingInformation.getColumnIndex(NUMBER_OF_POLICIES);
            List<UnderwritingInfoPacket> underwritingInfos = new ArrayList<UnderwritingInfoPacket>(numberOfSegments);
            for (int i = 1; i < numberOfSegments; i++) {
                UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket();
                underwritingInfo.setPremiumWritten(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexPremium)));
                underwritingInfo.setPremiumPaid(underwritingInfo.getPremiumWritten());
                underwritingInfo.setMaxSumInsured(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexMaxSumInsured)));
                underwritingInfo.setSumInsured(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexAverageSumInsured)));
                underwritingInfo.setNumberOfPolicies(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexNumberOfPolicies)));
                underwritingInfo.origin = this;
                ExposureInfo exposure = new ExposureInfo(iterationScope.getPeriodScope().getCurrentPeriodStartDate(),
                        iterationScope.getPeriodScope().getPeriodCounter());
                exposure.setSumInsured(underwritingInfo.getSumInsured());
                exposure.setMaxSumInsured(underwritingInfo.getMaxSumInsured());
                underwritingInfo.setExposure(exposure);
                underwritingInfos.add(underwritingInfo);
            }
            iterationStore.put(UNDERWRITING_INFOS, underwritingInfos);
        }
    }

    public static final String UNDERWRITING_INFOS = "underwriting infos";
    public static final String MAXIMUM_SUM_INSURED = "maximum sum insured";
    public static final String AVERAGE_SUM_INSURED = "average sum insured";
    public static final String PREMIUM = "premium";
    public static final String NUMBER_OF_POLICIES = "number of policies";

    public final static List<String> TABLE_COLUMN_TITLES =
            Arrays.asList(MAXIMUM_SUM_INSURED, AVERAGE_SUM_INSURED, PREMIUM, NUMBER_OF_POLICIES);

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfo() {
        return outUnderwritingInfo;
    }

    public void setOutUnderwritingInfo(PacketList<UnderwritingInfoPacket> outUnderwritingInfo) {
        this.outUnderwritingInfo = outUnderwritingInfo;
    }

    public TableMultiDimensionalParameter getParmUnderwritingInformation() {
        return parmUnderwritingInformation;
    }

    public void setParmUnderwritingInformation(TableMultiDimensionalParameter parmUnderwritingInformation) {
        this.parmUnderwritingInformation = parmUnderwritingInformation;
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }

    public IterationStore getIterationStore() {
        return iterationStore;
    }

    public void setIterationStore(IterationStore iterationStore) {
        this.iterationStore = iterationStore;
    }

    public ConstrainedMultiDimensionalParameter getParmPolicyIndices() {
        return parmPolicyIndices;
    }

    public void setParmPolicyIndices(ConstrainedMultiDimensionalParameter parmPolicyIndices) {
        this.parmPolicyIndices = parmPolicyIndices;
    }

    public ConstrainedMultiDimensionalParameter getParmPremiumIndices() {
        return parmPremiumIndices;
    }

    public void setParmPremiumIndices(ConstrainedMultiDimensionalParameter parmPremiumIndices) {
        this.parmPremiumIndices = parmPremiumIndices;
    }

    public PacketList<SingleValuePacket> getOutPolicyIndexApplied() {
        return outPolicyIndexApplied;
    }

    public void setOutPolicyIndexApplied(PacketList<SingleValuePacket> outPolicyIndexApplied) {
        this.outPolicyIndexApplied = outPolicyIndexApplied;
    }

    public PacketList<SingleValuePacket> getOutPremiumIndexApplied() {
        return outPremiumIndexApplied;
    }

    public void setOutPremiumIndexApplied(PacketList<SingleValuePacket> outPremiumIndexApplied) {
        this.outPremiumIndexApplied = outPremiumIndexApplied;
    }

    public PacketList<PatternPacket> getInPatterns() {
        return inPatterns;
    }

    public void setInPatterns(PacketList<PatternPacket> inPatterns) {
        this.inPatterns = inPatterns;
    }

    public ConstrainedString getParmPremiumPattern() {
        return parmPremiumPattern;
    }

    public void setParmPremiumPattern(ConstrainedString parmPremiumPattern) {
        this.parmPremiumPattern = parmPremiumPattern;
    }

    public boolean isGlobalGenerateNewClaimsInFirstPeriodOnly() {
        return globalGenerateNewClaimsInFirstPeriodOnly;
    }

    public void setGlobalGenerateNewClaimsInFirstPeriodOnly(boolean globalGenerateNewClaimsInFirstPeriodOnly) {
        this.globalGenerateNewClaimsInFirstPeriodOnly = globalGenerateNewClaimsInFirstPeriodOnly;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }
}
