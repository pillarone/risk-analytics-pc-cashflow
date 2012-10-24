package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PeriodsNotIncreasingException;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateHistoricClaim {

    private final int contractPeriod;
    private TreeMap<DateTime, Double> claimPaidUpdates = new TreeMap<DateTime, Double>();
    private TreeMap<DateTime, Double> claimReportedUpdates = new TreeMap<DateTime, Double>();

    private DateTime contractPeriodStartDate;
    private PayoutPatternBase base;

    public AggregateHistoricClaim(int contractPeriod, IPeriodCounter periodCounter, PayoutPatternBase base) {
        this.contractPeriod = contractPeriod;
        this.base = base;
        contractPeriodStartDate = periodCounter.startOfPeriod(contractPeriod);
    }

    /**
     * Helper method for updating claimPaidUpdates
     *
     * @param reportedDate
     * @param cumulativePaid
     */
    public void add(DateTime reportedDate, double cumulativeReported, double cumulativePaid) {
        claimReportedUpdates.put(reportedDate, cumulativeReported);
        claimPaidUpdates.put(reportedDate, cumulativePaid);
    }

    /**
     * @return true if both there are no claim paid and reported updates
     */
    public boolean noUpdates() {
        return claimPaidUpdates.isEmpty() && claimReportedUpdates.isEmpty();
    }


    public GrossClaimRoot claimWithAdjustedPattern(PatternPacket payoutPattern, ClaimRoot claimRoot, DateTime updateDate, DateTimeUtilities.Days360 days360, boolean sanityChecks) {
        DateTime startDateForPatterns = base.startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull());

        PatternPacket patternPacket = null;
        try {
            patternPacket = PatternUtils.adjustedPattern(payoutPattern, claimPaidUpdates, claimRoot.getUltimate(), startDateForPatterns,
                    claimRoot.getOccurrenceDate(), updateDate, lastReportedDate(updateDate), days360);
        } catch (PeriodsNotIncreasingException e) {
            throw new SimulationException("Aggregate historic claims caught an exception claiming pattern period values are incorrect. " +
                    "A potential cause of this" +
                    "would be if the reported date of a claim was before the start of it's model period. " +
                    "Is your updating table correct? Please check entries with contract period; " + contractPeriod +
                    ". Are they consistent with the contract dates. If so, please contact development.", e);
        }

        patternPacket.consistencyCheck(sanityChecks, sanityChecks, sanityChecks, sanityChecks);
        return new GrossClaimRoot(claimRoot, patternPacket , startDateForPatterns);
    }

    public DateTime firstActualPaidDateOrNull() {
        return claimPaidUpdates.firstKey();
    }

    /**
     * last reported before or at updateDate
     */
    public double reportedToDate(DateTime updateDate) {
        return claimReportedUpdates.floorEntry(updateDate).getValue();
    }

    public DateTime lastReportedDate(DateTime updateDate) {
        return claimReportedUpdates.floorEntry(updateDate).getKey();
    }

    public double outstandingShare(PatternPacket pattern, DateTime baseDate, DateTime updateDate) {
        double elapsedMonths = DateTimeUtilities.days360(baseDate, updateDate) / 30d;
        return pattern.outstandingShare(elapsedMonths);
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(contractPeriod);
        return result.toString();
    }

}
