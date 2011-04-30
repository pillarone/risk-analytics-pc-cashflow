package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IPolicyIndexMarker;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IPremiumIndexMarker;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author martin.melchior (at) fhnw (dot) ch, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RiskBands extends Component implements IUnderwritingInfoMarker {

    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfo
            = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

    private ConstrainedString parmPolicyIndex = new ConstrainedString(IPolicyIndexMarker.class, "");
    private ConstrainedString parmPremiumIndex = new ConstrainedString(IPremiumIndexMarker.class, "");
    private TableMultiDimensionalParameter parmUnderwritingInformation = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d, 0d, 0d}), TABLE_COLUMN_TITLES,
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    private IterationScope iterationScope;
    private IterationStore iterationStore;

    public void doCalculation() {
        fillIterationStore();
        List<UnderwritingInfoPacket> underwritingInfos = (List<UnderwritingInfoPacket>) iterationStore.get(UNDERWRITING_INFOS);

        DateTime currentPeriodStartDate = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
        FactorsPacket policyFactors = IndexUtils.filterFactors(inFactors, parmPolicyIndex);
        FactorsPacket premiumFactors = IndexUtils.filterFactors(inFactors, parmPremiumIndex);
        if (policyFactors == null && premiumFactors == null) {
            outUnderwritingInfo.addAll(underwritingInfos);
        }
        else {
            Double policyFactor = factor(currentPeriodStartDate, policyFactors);
            Double premiumFactor = factor(currentPeriodStartDate, premiumFactors);
            for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                UnderwritingInfoPacket modifiedUnderwritingInfo = underwritingInfo.withFactorsApplied(policyFactor, premiumFactor);
                outUnderwritingInfo.add(modifiedUnderwritingInfo);
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
                underwritingInfo.setPremium(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexPremium)));
                underwritingInfo.setMaxSumInsured(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexMaxSumInsured)));
                underwritingInfo.setSumInsured(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexAverageSumInsured)));
                underwritingInfo.setNumberOfPolicies(InputFormatConverter.getDouble(
                        parmUnderwritingInformation.getValueAt(i, columnIndexNumberOfPolicies)));
                underwritingInfo.origin = this;
                underwritingInfos.add(underwritingInfo);
            }
            iterationStore.put(UNDERWRITING_INFOS, underwritingInfos);
        }
    }


    /**
     * @param date
     * @param factors
     * @return the floor factor corresponding to the date or 1 if non is found
     */
    private Double factor(DateTime date, FactorsPacket factors) {
        Double policyFactor = factors != null ? factors.getFactorFloor(date) : 1d;
        policyFactor = policyFactor == null ? 1d : policyFactor;
        return policyFactor;
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

    public ConstrainedString getParmPolicyIndex() {
        return parmPolicyIndex;
    }

    public void setParmPolicyIndex(ConstrainedString parmPolicyIndex) {
        this.parmPolicyIndex = parmPolicyIndex;
    }

    public ConstrainedString getParmPremiumIndex() {
        return parmPremiumIndex;
    }

    public void setParmPremiumIndex(ConstrainedString parmPremiumIndex) {
        this.parmPremiumIndex = parmPremiumIndex;
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
}
