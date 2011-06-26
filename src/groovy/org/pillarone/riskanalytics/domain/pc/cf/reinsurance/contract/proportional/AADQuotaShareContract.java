package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.AadLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AADQuotaShareContract extends QuotaShareContract {

    private DoubleValue annualAggregateDeductibleUltimate = new DoubleValue();
    private DoubleValue annualAggregateDeductiblePaid = new DoubleValue();
    private DoubleValue annualAggregateDeductibleReported = new DoubleValue();


    public AADQuotaShareContract(double quotaShare, ICommission commission, AadLimitStrategy limit) {
        super(quotaShare, commission);
        double annualAggregateDeductible = limit.getAAD();
        annualAggregateDeductibleUltimate.value = annualAggregateDeductible;
        annualAggregateDeductiblePaid.value = annualAggregateDeductible;
        annualAggregateDeductibleReported.value = annualAggregateDeductible;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        double quotaShareUltimate = 0;
        IClaimRoot cededBaseClaim;
        if (storage.hasReferenceCeded()) {
            cededBaseClaim = storage.getCededClaimRoot();
        }
        else {
            quotaShareUltimate = adjustedQuote(grossClaim.ultimate(), annualAggregateDeductibleUltimate);
            cededBaseClaim = storage.lazyInitCededClaimRoot(quotaShareUltimate);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedIncremental(), annualAggregateDeductibleReported);
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidIncremental(), annualAggregateDeductiblePaid);
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                quotaShareReported, quotaSharePaid);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     *
     * @param claimProperty
     * @param annualAggregateDeductible is applied on whole gross
     * @return has a negative sign as claimProperty is negative
     */
    private double adjustedQuote(double claimProperty, DoubleValue annualAggregateDeductible) {
        if (claimProperty == 0) return 1;
        double claimPropertyAfterAAD = Math.min(claimProperty + annualAggregateDeductible.value, 0);
        double aadReduction = claimProperty - claimPropertyAfterAAD;
        annualAggregateDeductible.plus(aadReduction);
        return (claimPropertyAfterAAD / claimProperty * -quotaShare);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", AAD ultimate: ");
        buffer.append(annualAggregateDeductibleUltimate);
        buffer.append(", AAD reported: ");
        buffer.append(annualAggregateDeductibleReported);
        buffer.append(", AAD paid: ");
        buffer.append(annualAggregateDeductiblePaid);
        return buffer.toString();
    }
}
