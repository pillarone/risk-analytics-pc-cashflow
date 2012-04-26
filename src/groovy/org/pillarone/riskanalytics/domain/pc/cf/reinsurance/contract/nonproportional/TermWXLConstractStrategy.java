package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TermWXLConstractStrategy extends TermXLConstractStrategy implements IReinsuranceContractStrategy {

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.WXLTERM;
    }


    /**
     * This implementation ignores all provided parameters.
     * @param period ignored
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base defines which property of the underwritingInfoPackets should be used for scaling. Depending on the
     *             contracts are parametrized, this parameter is ignored and instead a local strategy parameter is used
     * @param termDeductible deductible shared among several contracts
     * @param termLimit limit shared among several contracts
     * @return fully prepared contracts
     */
    public List<IReinsuranceContract> getContracts(int period, List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ExposureBase base, ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        double cededPremiumFixed = getCededPremiumFixed(underwritingInfoPackets);
        List<Double> reinstatementPremiumFactors = (List<Double>) reinstatementPremiums.getValues().get(0);
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new TermWXLContract(cededPremiumFixed, attachmentPoint,
                limit, aggregateDeductible, aggregateLimit, stabilization, reinstatementPremiumFactors, riPremiumSplit,
                termDeductible, termLimit)));
    }
}
