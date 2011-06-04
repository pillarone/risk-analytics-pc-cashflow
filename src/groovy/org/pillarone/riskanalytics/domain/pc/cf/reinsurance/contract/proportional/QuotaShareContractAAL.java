package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.AalLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContractAAL extends QuotaShareContract {

    private DoubleValue annualAggregateLimitUltimate = new DoubleValue();
    private DoubleValue annualAggregateLimitPaid = new DoubleValue();
    private DoubleValue annualAggregateLimitReported = new DoubleValue();


    public QuotaShareContractAAL(double quotaShare, ICommission commission, AalLimitStrategy limit) {
        super(quotaShare, commission);
        double annualAggregateLimit = limit.getAAL();
        annualAggregateLimitUltimate.value = annualAggregateLimit;
        annualAggregateLimitPaid.value = annualAggregateLimit;
        annualAggregateLimitReported.value = annualAggregateLimit;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim;
        if (storage.hasReferenceCeded()) {
            cededBaseClaim = storage.getCededClaimRoot();
        }
        else {
            double quotaShareUltimate = adjustedQuote(grossClaim.ultimate(), annualAggregateLimitUltimate);
            cededBaseClaim = storage.lazyInitCededClaimRoot(quotaShareUltimate);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedIncremental(), annualAggregateLimitReported);
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidIncremental(), annualAggregateLimitPaid);
        ClaimCashflowPacket cededClaim = grossClaim.withBaseClaimAndShare(cededBaseClaim, quotaShareReported,
                quotaSharePaid, grossClaim.ultimate() != 0);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     *
     * @param claimProperty
     * @param annualAggregateLimit
     * @return has a negative sign as claimProperty is negative
     */
    private double adjustedQuote(double claimProperty, DoubleValue annualAggregateLimit) {
        if (claimProperty == 0) return 1;
        Double cededClaimProperty = Math.min(claimProperty * -quotaShare, annualAggregateLimit.value);
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
        return buffer.toString();
    }
}
