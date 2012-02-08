package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategy {
    /**
     *
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param termDeductible
     * @param termLimit
     * @return fully prepared contract
     */
    IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets,
                                     ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit);

    double getTermDeductible();
    double getTermLimit();
}
