package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.AalAadLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AALAADQuotaShareContract extends QuotaShareContract {

    private DoubleValue annualAggregateLimitUltimate = new DoubleValue();
    private DoubleValue annualAggregateLimitPaid = new DoubleValue();
    private DoubleValue annualAggregateLimitReported = new DoubleValue();
    private DoubleValue annualAggregateDeductibleUltimate = new DoubleValue();
    private DoubleValue annualAggregateDeductiblePaid = new DoubleValue();
    private DoubleValue annualAggregateDeductibleReported = new DoubleValue();


    public AALAADQuotaShareContract(double quotaShare, ICommission commission, AalAadLimitStrategy limit) {
        super(quotaShare, commission);
        double annualAggregateLimit = limit.getAAL();
        annualAggregateLimitUltimate.value = annualAggregateLimit;
        annualAggregateLimitPaid.value = annualAggregateLimit;
        annualAggregateLimitReported.value = annualAggregateLimit;
        double annualAggregateDeductible = limit.getAAD();
        annualAggregateDeductibleUltimate.value = annualAggregateDeductible;
        annualAggregateDeductiblePaid.value = annualAggregateDeductible;
        annualAggregateDeductibleReported.value = annualAggregateDeductible;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        double quotaShareUltimate = 0;
        IClaimRoot cededBaseClaim;
        if (storage.hasReferenceCeded()) {
            cededBaseClaim = storage.getCededClaimRoot();
        }
        else {
            quotaShareUltimate = adjustedQuote(grossClaim.ultimate(), annualAggregateLimitUltimate, annualAggregateDeductibleUltimate);
            cededBaseClaim = storage.lazyInitCededClaimRoot(quotaShareUltimate);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedIncrementalIndexed(), annualAggregateLimitReported, annualAggregateDeductibleReported);
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidIncrementalIndexed(), annualAggregateLimitPaid, annualAggregateDeductiblePaid);
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                quotaShareReported, quotaSharePaid, true);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     * @param claimProperty
     * @param annualAggregateLimit
     * @param annualAggregateDeductible
     * @return has a negative sign as claimProperty is negative
     */
    private double adjustedQuote(double claimProperty, DoubleValue annualAggregateLimit, DoubleValue annualAggregateDeductible) {
        if (claimProperty == 0) return 1;
        double claimPropertyAfterAAD = Math.max(claimProperty - annualAggregateDeductible.value, 0);
        double aadReduction = claimProperty - claimPropertyAfterAAD;
        annualAggregateDeductible.minus(aadReduction);
        Double cededClaimProperty = Math.min(claimPropertyAfterAAD * -quotaShare, annualAggregateLimit.value);
        annualAggregateLimit.minus(cededClaimProperty);
        return (cededClaimProperty / claimProperty);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", AAL ultimate: ");
        buffer.append(annualAggregateLimitUltimate);
        buffer.append(", AAL reported: ");
        buffer.append(annualAggregateLimitReported);
        buffer.append(", AAL paid: ");
        buffer.append(annualAggregateLimitPaid);
        buffer.append(", AAD ultimate: ");
        buffer.append(annualAggregateDeductibleUltimate);
        buffer.append(", AAD reported: ");
        buffer.append(annualAggregateDeductibleReported);
        buffer.append(", AAD paid: ");
        buffer.append(annualAggregateDeductiblePaid);
        return buffer.toString();
    }
}
