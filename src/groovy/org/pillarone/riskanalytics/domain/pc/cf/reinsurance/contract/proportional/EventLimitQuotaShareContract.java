package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.EventLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventLimitQuotaShareContract extends QuotaShareContract {

    private DoubleValue eventLimitUltimate = new DoubleValue();
    private DoubleValue eventLimitPaid = new DoubleValue();
    private DoubleValue eventLimitReported = new DoubleValue();


    public EventLimitQuotaShareContract(double quotaShare, ICommission commission, EventLimitStrategy limit) {
        super(quotaShare, commission);
        double eventLimit = limit.getEventLimit();
        eventLimitUltimate.value = eventLimit;
        eventLimitPaid.value = eventLimit;
        eventLimitReported.value = eventLimit;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        double quotaShareUltimate = 0;
        IClaimRoot cededBaseClaim;
        if (storage.hasReferenceCeded()) {
            cededBaseClaim = storage.getCededClaimRoot();
        }
        else {
            quotaShareUltimate = adjustedQuote(grossClaim.ultimate(), eventLimitUltimate, grossClaim.getBaseClaim().hasEvent());
            cededBaseClaim = storage.lazyInitCededClaimRoot(quotaShareUltimate);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedIncremental(), eventLimitReported, grossClaim.getBaseClaim().hasEvent());
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidIncremental(), eventLimitPaid, grossClaim.getBaseClaim().hasEvent());
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                quotaShareReported, quotaSharePaid);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     *
     * @param claimProperty
     * @param eventLimit
     * @return has a negative sign as claimProperty is negative
     */
    private double adjustedQuote(double claimProperty, DoubleValue eventLimit, boolean isEventClaim) {
        // todo(sku): similar implementation as for CXL required
        return 0;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", event limit ultimate: ");
        buffer.append(eventLimitUltimate);
        buffer.append(", event limit reported: ");
        buffer.append(eventLimitReported);
        buffer.append(", event limit paid: ");
        buffer.append(eventLimitPaid);
        return buffer.toString();
    }
}
