package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
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

    private PatternPacket adjustedPattern(PatternPacket payoutPattern, ClaimRoot claimRoot, DateTime updateDate) {
        DateTime startDateForPattern = base.startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull());
        return PatternUtils.adjustedPattern(payoutPattern, claimPaidUpdates, claimRoot.getUltimate(), startDateForPattern,
                claimRoot.getOccurrenceDate(), updateDate);
    }

    public GrossClaimRoot claimWithAdjustedPattern(PatternPacket payoutPattern, ClaimRoot claimRoot, DateTime updateDate) {
        DateTime startDateForPatterns = base.startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull());
        return new GrossClaimRoot(claimRoot, adjustedPattern(payoutPattern, claimRoot, updateDate), startDateForPatterns);
    }

    public DateTime firstActualPaidDateOrNull() {
        return claimPaidUpdates.firstKey();
    }

    /** last reported before or at updateDate */
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
