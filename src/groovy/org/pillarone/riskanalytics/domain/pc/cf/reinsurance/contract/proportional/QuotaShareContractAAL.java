package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContractAAL extends QuotaShareContract {

    private DoubleValue annualAggregateLimitUltimate = new DoubleValue();
    private DoubleValue annualAggregateLimitPaid = new DoubleValue();
    private DoubleValue annualAggregateLimitReported = new DoubleValue();


    public QuotaShareContractAAL(double quotaShare, double annualAggregateLimit) {
        super(quotaShare);
        annualAggregateLimitUltimate.value = annualAggregateLimit;
        annualAggregateLimitPaid.value = annualAggregateLimit;
        annualAggregateLimitReported.value = annualAggregateLimit;
    }

    public void initBookkeepingFigures(List<ClaimCashflowPacket> grossClaims) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim;
        if (storage.hasReferenceCeded()) {
            cededBaseClaim = storage.getCededClaimRoot(quotaShare, contractMarker);
        }
        else {
            double quotaShareUltimate = adjustedQuote(grossClaim.ultimate(), annualAggregateLimitUltimate);
            cededBaseClaim = storage.getCededClaimRoot(quotaShareUltimate, contractMarker);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedIncremental(), annualAggregateLimitReported);
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidIncremental(), annualAggregateLimitPaid);

        return grossClaim.withBaseClaimAndShare(cededBaseClaim, quotaShareReported, quotaSharePaid, grossClaim.ultimate() != 0);
    }

    /**
     *
     * @param claimProperty
     * @param annualAggregateLimit
     * @return has a negative sign as claimProperty is negati
     */
    private double adjustedQuote(double claimProperty, DoubleValue annualAggregateLimit) {
        if (claimProperty == 0) return 1;
        Double cededClaimProperty = Math.min(claimProperty * -quotaShare, annualAggregateLimit.value);
        annualAggregateLimit.minus(cededClaimProperty);
        return (cededClaimProperty / claimProperty);
    }

    public UnderwritingInfoPacket calculateUnderwritingInfoCeded(UnderwritingInfoPacket grossInfo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CommissionPacket calculateCommission(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> cededUnderwritingInfo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
