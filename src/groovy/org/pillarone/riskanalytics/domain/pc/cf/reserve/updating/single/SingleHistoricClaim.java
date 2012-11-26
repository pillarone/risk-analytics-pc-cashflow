package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): set originalPeriod correctly!
public class SingleHistoricClaim {

    private String claimID;
    private int contractPeriod;
    /** contains all reported and paid claims except the first and last belonging to the claimID */
    private TreeMap<DateTime, Double> claimUpdates = new TreeMap<DateTime, Double>();

    private DateTime occurrenceDate;  // corresponds to the occurrence date
//    private ClaimDevelopmentPacket originalClaim;

    private double lastPaid;
    private double lastReported;
    private double originalLastReported;
    private DateTime lastUpdateDate;

    public SingleHistoricClaim(String claimID, int contractPeriod, DateTime occurrenceDate) {
        this.claimID = claimID;
        this.contractPeriod = contractPeriod;
        this.occurrenceDate = occurrenceDate;
    }

    /**
     * Only claims with same claimID should be added!
     * @param reportedDate
     * @param cumulativePaid
     * @param cumulativeReported
     */
    public void add(DateTime reportedDate, double cumulativePaid, double cumulativeReported,
                    boolean firstReportOfClaim, IPeriodCounter periodCounter) {
        if (reportedDate.isBefore(occurrenceDate)) occurrenceDate = reportedDate;
        if (lastUpdateDate == null || reportedDate.isAfter(lastUpdateDate)) {
            lastUpdateDate = reportedDate;
            lastPaid = cumulativePaid;
            lastReported = cumulativeReported;
            originalLastReported = lastReported;
        }
        claimUpdates.put(reportedDate, cumulativePaid);
        if (firstReportOfClaim) {
            try {
                int reportedPeriod = periodCounter.belongsToPeriod(reportedDate);
                int occurrencePeriod = periodCounter.belongsToPeriod(occurrenceDate);
                if (occurrencePeriod < reportedPeriod) {
                    claimUpdates.put(occurrenceDate, 0d);
                }
            }
            catch (NotInProjectionHorizon ex) {
                // we can ignore actual claims outside the global start and end date
            }

        }
    }

    public void removeReportedDateWithZeroIncrementalPaid() {
        Double lastCumulativePaid = null;
        List<DateTime> reportDatesWithNoPayment = new ArrayList<DateTime>();
        for (Map.Entry<DateTime, Double> claimUpdate : claimUpdates.entrySet()) {
            double cumulativePaid = claimUpdate.getValue();
            if (lastCumulativePaid == null || cumulativePaid != lastCumulativePaid) {
                lastCumulativePaid = cumulativePaid;
            }
            else {
                if (cumulativePaid == lastCumulativePaid) {
                    reportDatesWithNoPayment.add(claimUpdate.getKey());
                }
            }
        }
        for (DateTime reportedDate : reportDatesWithNoPayment) {
            claimUpdates.remove(reportedDate);
        }
    }

    /**
     * Builds historic claims using the ultimate and historic date and paid information
     * @param ultimate not used as the lastReported is taken
     * @param payoutPattern
     * @param peril
     * @return
     */
//    public List<ClaimDevelopmentPacket> getHistoricClaims(double ultimate, IPattern payoutPattern, IPerilMarker peril) {
//        List<ClaimDevelopmentPacket> claims = new ArrayList<ClaimDevelopmentPacket>(claimUpdates.size() + 2);
//        originalClaim = null;   // reset for every iteration, as a new original claim is required per iteration
//        double remainingReserve = 0;
//        double formerReportedCumulativePaid = 0;
//        for (Map.Entry<DateTime, Double> claimUpdate : claimUpdates.entrySet()) {
//            DateTime reportedDate = claimUpdate.getKey();
//            if (originalClaim == null) {
//                originalClaim = createOriginalClaim(payoutPattern, peril, reportedDate);
//                claims.add(originalClaim);
//                remainingReserve = originalClaim.getReserved();
//                formerReportedCumulativePaid = originalClaim.getPaid();
//            }
//                double cumulativePaid = claimUpdate.getValue();
//                if (cumulativePaid == 0) continue;
//                ClaimDevelopmentPacket claim = ClaimDevelopmentPacketFactory.copy(originalClaim);
//                claim.setIncurred(0);
//                claim.setDate(reportedDate);
//                claim.setPaid(cumulativePaid - formerReportedCumulativePaid);
//                claim.setChangeInReserves(claim.getPaid());
//                claim.setReserved(remainingReserve - claim.getPaid());
//                claims.add(claim);
//
//                formerReportedCumulativePaid = cumulativePaid;
//                remainingReserve = claim.getReserved();
//        }
//        return claims;
//    }
//
//    private ClaimDevelopmentPacket createOriginalClaim(IPattern payoutPattern, IPerilMarker peril, DateTime reportedDate) {
//        ClaimDevelopmentPacket originalClaim = new ClaimDevelopmentPacket();
//        originalClaim.setIncurred(lastReported);
//        if (reportedDate.isBefore(occurrenceDate)) occurrenceDate = reportedDate;
//        originalClaim.setDate(occurrenceDate);
//        originalClaim.setChangeInReserves(originalClaim.getPaid());
//        originalClaim.setReserved(lastReported - originalClaim.getPaid());
//        originalClaim.setOrigin((Component) peril);
//        originalClaim.setOriginalClaim(originalClaim);
//        originalClaim.setOriginalPeriod(contractPeriod);
//        originalClaim.setClaimType(ClaimType.SINGLE);
//        originalClaim.setPayoutPattern(payoutPattern);
//        originalClaim.addMarker(IPerilMarker.class, (IComponentMarker)peril);
//        return originalClaim;
//    }
//
//    public double elapsedMonths(DateTime updateDate) {
//        return DateTimeUtilities.deriveNumberOfMonths(occurrenceDate.withDayOfMonth(1), updateDate);
//    }
//
//    public double getOutstanding() {
//        return lastReported - lastPaid;
//    }
//
//    public void applyVolatility(IRandomNumberGenerator volatilityGenerator) {
//        double volatilityFactor = (Double) volatilityGenerator.nextValue();
//        lastReported = Math.max((originalLastReported - lastPaid) * volatilityFactor  + getLastCumulativePaid(), getLastCumulativePaid());
//    }

    public double getLastCumulativePaid() {
        return lastPaid;
    }

    public DateTime getLastUpdate() {
        return lastUpdateDate;
    }

    public String getClaimID() {
        return claimID;
    }

    public int getContractPeriod() {
        return contractPeriod;
    }

//    public ClaimDevelopmentPacket getOriginalClaim() {
//        return originalClaim;
//    }

     @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(claimID);
        result.append(separator);
        result.append(contractPeriod);
        result.append(separator);
        result.append("occurrence date");
        result.append(separator);
        result.append(occurrenceDate);
        return result.toString();
    }
}
