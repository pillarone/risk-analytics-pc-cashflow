package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AggregateEventClaimsStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.IBoundaryIndexStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class WCXLContract extends CXLContract {

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     *
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param stabilization
     * @param reinstatementPremiumFactors
     * @param premiumAllocation
     */
    public WCXLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                        double aggregateLimit, IStabilizationStrategy stabilization,
                        List<Double> reinstatementPremiumFactors, IRIPremiumSplitStrategy premiumAllocation,
                        IBoundaryIndexStrategy boundaryIndex, List<FactorsPacket> factors, IPeriodCounter periodCounter) {
        super(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit,
                stabilization, reinstatementPremiumFactors, premiumAllocation, boundaryIndex, factors, periodCounter);
    }

    protected boolean isClaimTypeCovered(ClaimCashflowPacket grossClaim) {
        return super.isClaimTypeCovered(grossClaim) || grossClaim.getBaseClaim().getClaimType().equals(ClaimType.SINGLE);
    }

    protected AggregateEventClaimsStorage getClaimsStorage(ClaimCashflowPacket grossClaim) {
        if (grossClaim.hasEvent()) {
            return cededShareByEvent.get(grossClaim.getEvent());
        }
        else if (grossClaim.getBaseClaim().getClaimType().equals(ClaimType.SINGLE)) {
            return cededShareByEvent.get(grossClaim.getKeyClaim());
        }
        return null;
    }

}
