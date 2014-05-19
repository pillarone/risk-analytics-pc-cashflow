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
public class CXLConstractStrategy extends XLConstractStrategy implements IReinsuranceContractStrategy {

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.CXL;
    }

    /**
     *
     *
     * @param periodCounter is ignored in this context
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base is ignored in this context
     * @param termDeductible is ignored in this context
     * @param termLimit is ignored in this context
     * @param claims
     * @param factors
     * @return a single CXL contract
     */
    public List<IReinsuranceContract> getContracts(IPeriodCounter periodCounter,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible,
                                                   IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims,
                                                   List<FactorsPacket> factors) {
        double cededPremiumFixed = getCededPremiumFixed(underwritingInfoPackets);
        List<Double> reinstatementPremiumFactors = (List<Double>) reinstatementPremiums.getValues().get(0);
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new CXLContract(cededPremiumFixed, attachmentPoint,
            limit, aggregateDeductible, aggregateLimit, stabilization, reinstatementPremiumFactors, riPremiumSplit,
            boundaryIndex, factors, periodCounter)));
    }
}
