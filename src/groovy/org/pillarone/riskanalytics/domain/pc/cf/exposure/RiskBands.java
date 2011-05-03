package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

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
 * @author martin.melchior (at) fhnw (dot) ch, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RiskBands extends Component implements IUnderwritingInfoMarker {

    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfo
            = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<SingleValuePacket> outPolicyIndexApplied = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<SingleValuePacket> outPremiumIndexApplied = new PacketList<SingleValuePacket>(SingleValuePacket.class);

    private ConstrainedMultiDimensionalParameter parmPoliciesIndices = new ConstrainedMultiDimensionalParameter(
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

    public void doCalculation() {
        fillIterationStore();
        List<UnderwritingInfoPacket> underwritingInfos = (List<UnderwritingInfoPacket>) iterationStore.get(UNDERWRITING_INFOS);

        DateTime currentPeriodStartDate = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
        List<Factors> policyFactors = IndexUtils.filterFactors(inFactors, parmPoliciesIndices);
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

    public ConstrainedMultiDimensionalParameter getParmPoliciesIndices() {
        return parmPoliciesIndices;
    }

    public void setParmPoliciesIndices(ConstrainedMultiDimensionalParameter parmPoliciesIndices) {
        this.parmPoliciesIndices = parmPoliciesIndices;
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
}
