package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategy {
    /**
     * The interface of this very generic, meaning that not all implementations will use all parameters.
     * @param period contracts of this period should be returned, normally this is the current period
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base defines which property of the underwritingInfoPackets should be used for scaling. Depending on the
     *             contracts are parametrized, this parameter is ignored and instead a local strategy parameter is used
     * @param termDeductible deductible shared among several contracts
     * @param termLimit limit shared among several contracts
     * @return fully prepared contracts including absolute values
     */
    List<IReinsuranceContract> getContracts(int period,
                                            List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                            ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit);

    double getTermDeductible();
    double getTermLimit();
}
