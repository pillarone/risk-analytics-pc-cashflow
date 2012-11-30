package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PeriodsNotIncreasingException;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SingleHistoricClaim {

    private String claimID;
    private int contractPeriod;
    /**
     * contains all cumulative paid claims except the first and last belonging to the claimID
     */
    private TreeMap<DateTime, Double> claimCumulativePaidUpdates = new TreeMap<DateTime, Double>();
    private TreeMap<DateTime, Double> claimCumulativeReportedUpdates = new TreeMap<DateTime, Double>();

    private DateTime occurrenceDate;

    private double lastPaid;
    private double lastReported;
    private double originalLastReported;
    private DateTime lastUpdateDate;
    private DateTime contractPeriodStartDate;

    public SingleHistoricClaim(String claimID, int contractPeriod, DateTime occurrenceDate, IPeriodCounter periodCounter) {
        this.claimID = claimID;
        this.contractPeriod = contractPeriod;
        this.occurrenceDate = occurrenceDate;
        contractPeriodStartDate = periodCounter.startOfPeriod(contractPeriod);
    }

    /**
     * @param reportedDate
     * @param cumulativePaid
     * @param cumulativeReported
     * @param claimID
     */
    public void add(DateTime reportedDate, double cumulativePaid, double cumulativeReported,
                    boolean firstReportOfClaim, IPeriodCounter periodCounter, String claimID) {
        if (!claimID.equals(this.claimID)) {
            throw new SimulationException("Attempted to add the values from claim ID ; " + claimID + "into claim ; " +
                    this.claimID + " this should never happen. Contact development");
        }
        if (reportedDate.isBefore(occurrenceDate)) occurrenceDate = reportedDate;
        if (lastUpdateDate == null || reportedDate.isAfter(lastUpdateDate)) {
            lastUpdateDate = reportedDate;
            lastPaid = cumulativePaid;
            lastReported = cumulativeReported;
            originalLastReported = lastReported;
        }
        claimCumulativePaidUpdates.put(reportedDate, cumulativePaid);
        claimCumulativeReportedUpdates.put(reportedDate, cumulativeReported);
        if (firstReportOfClaim) {
            int reportedPeriod = periodCounter.belongsToPeriod(reportedDate);
            int occurrencePeriod = periodCounter.belongsToPeriod(occurrenceDate);
            if (occurrencePeriod < reportedPeriod) {
                claimCumulativePaidUpdates.put(occurrenceDate, 0d);
            }
        }
    }

    public void removeReportedDateWithZeroIncrementalPaid() {
        Double lastCumulativePaid = -1d;
        List<DateTime> reportDatesWithNoPayment = new ArrayList<DateTime>();
        for (Map.Entry<DateTime, Double> claimUpdate : claimCumulativePaidUpdates.entrySet()) {
            double cumulativePaid = claimUpdate.getValue();
            if (cumulativePaid == lastCumulativePaid) {
                reportDatesWithNoPayment.add(claimUpdate.getKey());
            }
            if (cumulativePaid < lastCumulativePaid) {
                throw new SimulationException("Please check the entries for claim with ID ; " + claimID + ". It appears that paid entry with date " +
                        DateTimeUtilities.formatDate.print(claimUpdate.getKey()) + " has a prior cumulative paid of " + cumulativePaid);
            }
            lastCumulativePaid = cumulativePaid;
        }


        for (DateTime reportedDate : reportDatesWithNoPayment) {
            claimCumulativePaidUpdates.remove(reportedDate);
        }
    }

    public DateTime firstActualPaidDateOrNull() {
        return claimCumulativePaidUpdates.firstKey();
    }

    /**
     * @return true if there are no claim paid and reported updates
     */
    public boolean noUpdates() {
        return claimCumulativePaidUpdates.isEmpty() && claimCumulativeReportedUpdates.isEmpty();
    }

    public GrossClaimRoot claimWithAdjustedPattern(PatternPacket originalPayoutPattern, PayoutPatternBase base, DateTime updateDate, DateTimeUtilities.Days360 days360) {
        double ultimate = lastReported;
//        applyVolatility(ultimate);
        DateTime exposureStartDate = contractPeriodStartDate;
        ClaimRoot claimRoot = new ClaimRoot(ultimate, ClaimType.SINGLE, exposureStartDate, occurrenceDate);
        DateTime baseDate = base.startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull());
        PatternPacket adjustedPayoutPattern = PatternUtils.adjustedPattern(originalPayoutPattern, claimCumulativePaidUpdates, ultimate,
                baseDate, occurrenceDate, updateDate, lastUpdateDate, days360);
        return new GrossClaimRoot(ultimate, ClaimType.SINGLE, exposureStartDate, occurrenceDate, adjustedPayoutPattern, null);
    }

    public void applyVolatility(IRandomNumberGenerator volatilityGenerator) {
        double volatilityFactor = (Double) volatilityGenerator.nextValue();
        lastReported = Math.max((originalLastReported - lastPaid) * volatilityFactor + getLastCumulativePaid(), getLastCumulativePaid());
    }

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

    public DateTime lastReportedDate(DateTime updateDate) {
        return claimCumulativeReportedUpdates.floorEntry(updateDate).getKey();
    }

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
