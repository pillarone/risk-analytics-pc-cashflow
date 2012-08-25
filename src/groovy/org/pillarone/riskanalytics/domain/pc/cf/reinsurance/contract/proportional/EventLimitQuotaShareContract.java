package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.EventLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventLimitQuotaShareContract extends QuotaShareContract {

    private Map<EventPacket, EventLimitTracking> limitTrackingPerEvent;
    private double eventLimit;

    public EventLimitQuotaShareContract(double quotaShare, ICommission commission, EventLimitStrategy limit) {
        super(quotaShare, commission);
        limitTrackingPerEvent = new HashMap<EventPacket, EventLimitTracking>();
        eventLimit = limit.getEventLimit();
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        double quotaShareUltimate = 0;
        if (!storage.hasReferenceCeded()) {
            quotaShareUltimate = adjustedQuote(grossClaim, BasedOnClaimProperty.ULTIMATE);
        }

        double quotaShareReported = adjustedQuote(grossClaim, BasedOnClaimProperty.REPORTED);
        double quotaSharePaid = adjustedQuote(grossClaim, BasedOnClaimProperty.PAID);
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                quotaShareReported, quotaSharePaid, true);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     * @return sign is negative
     */
    private double adjustedQuote(ClaimCashflowPacket claim, BasedOnClaimProperty basedOnClaimProperty) {
        if (claim.hasEvent()) {
            EventLimitTracking eventLimitTracking = limitTrackingPerEvent.get(claim.getEvent());
            if (eventLimitTracking == null) {
                eventLimitTracking = new EventLimitTracking(eventLimit);
                limitTrackingPerEvent.put(claim.getEvent(), eventLimitTracking);
            }
            return eventLimitTracking.adjustedQuote(quotaShare, claim, basedOnClaimProperty);
        }
        else {
            return -quotaShare;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append(", event limit: ");
        builder.append(eventLimit);
        return builder.toString();
    }

    private class EventLimitTracking {

        DoubleValue remainingUltimateEventLimit;
        DoubleValue remainingReportedEventLimit;
        DoubleValue remainingPaidEventLimit;

        private EventLimitTracking(double eventLimit) {
            remainingUltimateEventLimit = new DoubleValue(eventLimit);
            remainingReportedEventLimit = new DoubleValue(eventLimit);
            remainingPaidEventLimit = new DoubleValue(eventLimit);
        }

        /**
         * @param quote default quote
         * @param claim having negative signs
         * @param claimProperty
         * @return <= quote
         */
        double adjustedQuote(double quote, ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty) {
            double value = claimProperty.incrementalIndexed(claim);
            double cession = quote * value;
            DoubleValue maxCession = remainingEventLimit(claimProperty);
            double cessionReduction = Math.min(-cession, maxCession.value);
            maxCession.minus(cessionReduction);
            return cession == 0 ?  -quote : cessionReduction / cession * quote;
        }

        private DoubleValue remainingEventLimit(BasedOnClaimProperty claimProperty) {
            switch (claimProperty) {
                case ULTIMATE:
                    return remainingUltimateEventLimit;
                case REPORTED:
                    return remainingReportedEventLimit;
                case PAID:
                    return remainingPaidEventLimit;
                default:
                    throw new NotImplementedException("Unknown case " + claimProperty.toString());
            }
        }
    }
}
