package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategy {
    /**
     * The interface of this very generic, meaning that not all implementations will use all parameters.
     *
     *
     *
     * @param periodCounter contracts of this period should be returned, normally this is the current period
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base defines which property of the underwritingInfoPackets should be used for scaling. Depending on the
     *             contracts are parametrized, this parameter is ignored and instead a local strategy parameter is used
     * @param termDeductible deductible shared among several contracts
     * @param termLimit limit shared among several contracts
     * @param claims
     * @param factors are used to possibly index limits and thresholds
     * @return fully prepared contracts including absolute values
     */
    List<IReinsuranceContract> getContracts(IPeriodCounter periodCounter,
                                            List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                            IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit,
                                            List<ClaimCashflowPacket> claims, List<FactorsPacket> factors);

    double getTermDeductible();
    double getTermLimit();
}
