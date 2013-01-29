package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AggregateEventClaimsStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnifiedAdcLptContract extends AbstractReinsuranceContract implements INonPropReinsuranceContract {

    private double cededShare;
    private double attachmentPoint;
    private double limit;

    /**
     * used to trigger return of a single ceded claim packet
     */
    private IClaimRoot firstClaimKey;

    private ClaimCashflowPacket aggregateIncrementalCededReserveClaim;
    private ClaimCashflowPacket aggregateCumulativeCededReserveClaim;
    private double previousCumulativeUltimate;
    private double previousCumulativeReported;
    private double previousCumulativePaid;
    private double previousIBNR;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     *
     * @param cededShare
     * @param attachmentPoint
     * @param limit
     */
    public UnifiedAdcLptContract(double cededShare, double attachmentPoint, double limit) {
        this.cededShare = cededShare;
        this.attachmentPoint = attachmentPoint;
        this.limit = limit;
    }


    @Override
    public void initBasedOnAggregateCalculations(List<ClaimCashflowPacket> grossClaims, List<UnderwritingInfoPacket> grossUnderwritingInfo) {
        if (grossClaims.size() > 0) {
            firstClaimKey = grossClaims.get(0).getKeyClaim();
            ClaimCashflowPacket aggregateGrossClaim = new ClaimPacketAggregator().aggregate(grossClaims);
            double cumulativeUltimate = cededCumulativeValue(aggregateGrossClaim.developedUltimate());
            double cumulativeReported = cededCumulativeValue(aggregateGrossClaim.getReportedCumulatedIndexed());
            double cumulativePaid = cededCumulativeValue(aggregateGrossClaim.getPaidCumulatedIndexed());
            double reserves = cumulativeUltimate - cumulativePaid;
            double ibnr = cumulativeUltimate - cumulativeReported;
            if (aggregateCumulativeCededReserveClaim == null) {
                DateTime occurrenceDate = grossClaims.get(0).getUpdateDate().withDayOfYear(1);
                IClaimRoot keyClaim = new ClaimRoot(cumulativeUltimate, ClaimType.AGGREGATED_RESERVES, occurrenceDate, occurrenceDate);
                double changeInReserves = reserves; // todo(sku): verify
                double changeInIBNR = cumulativeUltimate - cumulativeReported; // todo(sku): verify
                int updatePeriod = grossClaims.get(0).getUpdatePeriod();
                aggregateCumulativeCededReserveClaim = new ClaimCashflowPacket(keyClaim, keyClaim, cumulativeUltimate,
                        cumulativePaid, cumulativePaid, cumulativeReported, cumulativeReported,
                        reserves, changeInReserves, changeInIBNR, null, occurrenceDate, updatePeriod);
                aggregateIncrementalCededReserveClaim = ClaimUtils.scale(aggregateCumulativeCededReserveClaim, 1);
            } else {
                aggregateIncrementalCededReserveClaim = new ClaimCashflowPacket(
                        aggregateCumulativeCededReserveClaim.getKeyClaim(), aggregateCumulativeCededReserveClaim.getKeyClaim(),
                        cumulativeUltimate, aggregateCumulativeCededReserveClaim.nominalUltimate(),
                        cumulativePaid - previousCumulativePaid, cumulativePaid,
                        cumulativeReported - previousCumulativeReported, cumulativeReported,
                        reserves, aggregateCumulativeCededReserveClaim.reservedIndexed() - reserves,
                        ibnr - previousIBNR, null,
                        aggregateCumulativeCededReserveClaim.getOccurrenceDate(), aggregateCumulativeCededReserveClaim.getUpdatePeriod()
                );
            }
            previousCumulativeUltimate = cumulativeUltimate;
            previousCumulativeReported = cumulativeReported;
            previousCumulativePaid = cumulativePaid;
            previousIBNR = ibnr;
        }
    }

    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        if (grossClaim.getKeyClaim() == firstClaimKey) {
            return aggregateIncrementalCededReserveClaim;
        }
        return null;
    }

    private double cededCumulativeValue(double grossCumulativeValue) {
        return Math.max(0, Math.min(limit, -grossCumulativeValue - attachmentPoint));
    }

    /**
     * Not implemented as not required PMO-2235
     *
     * @param cededUnderwritingInfos
     * @param netUnderwritingInfos
     * @param coveredByReinsurers
     * @param fillNet                if true the second list is filled too
     */
    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos,
                                          double coveredByReinsurers, boolean fillNet) {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ceded share: ");
        builder.append(cededShare);
        builder.append("attachment point: ");
        builder.append(attachmentPoint);
        builder.append(", limit: ");
        builder.append(limit);
        return builder.toString();
    }

}
