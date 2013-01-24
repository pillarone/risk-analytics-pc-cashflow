package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.EventLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.ILimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.ILossParticipation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventLimitQuotaShareContract extends QuotaShareContract {

    private Map<IClaimRoot, ClaimCashflowPacket> cumulatedCededClaims;
    private Map<EventPacket, EventLimitTracking> limitTrackingPerEvent;
    private double eventLimit;
    private ILimitStrategy limit;

    public EventLimitQuotaShareContract(double quotaShare, ICommission commission, EventLimitStrategy limit, ILossParticipation lossParticipation) {
        super(quotaShare, commission, lossParticipation);
        cumulatedCededClaims = new HashMap<IClaimRoot, ClaimCashflowPacket>();
        limitTrackingPerEvent = new HashMap<EventPacket, EventLimitTracking>();
        this.limit = limit;
        eventLimit = limit.getEventLimit();
    }

    @Override
    public void initBasedOnAggregateCalculations(List<ClaimCashflowPacket> grossClaim, List<UnderwritingInfoPacket> grossUnderwritingInfo) {
        lossParticipation.initPeriod(grossClaim, grossUnderwritingInfo, limit);
        super.initBasedOnAggregateCalculations(grossClaim, grossUnderwritingInfo);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        ClaimCashflowPacket cededClaim;
        if (lossParticipation.noLossParticipation()) {
            double quotaShareUltimate = 0;
            ClaimCashflowPacket cumulatedCededClaim = cumulatedCededClaims.get(grossClaim.getKeyClaim());
            if (!storage.hasReferenceCeded()) {
                quotaShareUltimate = adjustedQuote(grossClaim, BasedOnClaimProperty.ULTIMATE, cumulatedCededClaim);
            }

            double quotaShareReported = adjustedQuote(grossClaim, BasedOnClaimProperty.REPORTED, cumulatedCededClaim);
            double quotaSharePaid = adjustedQuote(grossClaim, BasedOnClaimProperty.PAID, cumulatedCededClaim);
            cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                    quotaShareReported, quotaSharePaid, true);
            cumulatedCededClaims.put(grossClaim.getKeyClaim(), cededClaim);
        } else {
            cededClaim = lossParticipation.cededClaim(quotaShare, grossClaim, storage, true);
        }
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     * @return sign is negative
     */
    private double adjustedQuote(ClaimCashflowPacket claim, BasedOnClaimProperty basedOnClaimProperty, ClaimCashflowPacket cumulatedCededClaim) {
        if (claim.hasEvent()) {
            EventLimitTracking eventLimitTracking = limitTrackingPerEvent.get(claim.getEvent());
            if (eventLimitTracking == null) {
                eventLimitTracking = new EventLimitTracking(eventLimit);
                limitTrackingPerEvent.put(claim.getEvent(), eventLimitTracking);
            }
            return eventLimitTracking.adjustedQuote(quotaShare, claim, basedOnClaimProperty, cumulatedCededClaim);
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
         * @return <= quote, negative sign
         */
        double adjustedQuote(double quote, ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty, ClaimCashflowPacket cumulatedCededClaim) {
            DoubleValue maxCession = remainingEventLimit(claimProperty);
            double cumulatedCession = quote * claimProperty.cumulatedIndexed(claim);
            double incrementalCession = cumulatedCession;
            if (cumulatedCededClaim != null) {
                incrementalCession = cumulatedCession + claimProperty.cumulatedIndexed(cumulatedCededClaim);
            }
            double cessionReduction = Math.min(-incrementalCession, maxCession.value);
            maxCession.minus(cessionReduction);
            double incrementalCessionNotAdjusted = quote * claimProperty.incrementalIndexed(claim);
            return incrementalCession == 0 ?  0 : cessionReduction / incrementalCessionNotAdjusted * quote;
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
