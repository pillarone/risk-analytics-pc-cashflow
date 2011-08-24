package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class WXLContract extends XLContract {

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
    public WXLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                       double aggregateLimit, IStabilizationStrategy stabilization,
                       List<Double> reinstatementPremiumFactors, IRIPremiumSplitStrategy premiumAllocation) {
        super(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit, stabilization,
                reinstatementPremiumFactors, premiumAllocation);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        if (grossClaim.getBaseClaim().getClaimType().equals(ClaimType.SINGLE)) {
            return super.calculateClaimCeded(grossClaim, storage, periodCounter);
        }
        return new ClaimCashflowPacket(grossClaim.getBaseClaim().withScale(0), grossClaim.getKeyClaim(), 0, 0, 0, 0, 0, 0, 0, null,
                grossClaim.getUpdateDate(), grossClaim.getUpdatePeriod());
    }

}
