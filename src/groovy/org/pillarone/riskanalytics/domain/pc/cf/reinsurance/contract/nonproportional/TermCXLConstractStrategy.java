package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TermCXLConstractStrategy extends TermXLConstractStrategy implements IReinsuranceContractStrategy {

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.CXLTERM;
    }

    /**
     * This implementation ignores all provided parameters.
     *
     *
     * @param periodCounter ignored
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base defines which property of the underwritingInfoPackets should be used for scaling. Depending on the
     *             contracts are parametrized, this parameter is ignored and instead a local strategy parameter is used
     * @param termDeductible deductible shared among several contracts
     * @param termLimit limit shared among several contracts
     * @param claims
     * @param factors
     * @return fully prepared contracts
     */
    public List<IReinsuranceContract> getContracts(IPeriodCounter periodCounter, List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ExposureBase base, IPeriodDependingThresholdStore termDeductible,
                                                   IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims,
                                                   List<FactorsPacket> factors) {
        double cededPremiumFixed = getCededPremiumFixed(underwritingInfoPackets);
        List<Double> reinstatementPremiumFactors = (List<Double>) reinstatementPremiums.getValues().get(0);
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new TermCXLContract(cededPremiumFixed, attachmentPoint,
                limit, aggregateDeductible, aggregateLimit, stabilization, reinstatementPremiumFactors, riPremiumSplit,
                termDeductible, termLimit)));
    }
}
