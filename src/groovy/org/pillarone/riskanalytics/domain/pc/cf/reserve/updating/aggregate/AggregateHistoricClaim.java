package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternSumNotOneException;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private PatternPacket adjustedPattern(PatternPacket payoutPattern, ClaimRoot claimRoot, DateTime updateDate) {
        DateTime startDateForPattern = base.startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull());


        final ArrayList<Period> newPatternPeriods = new ArrayList<Period>();
        final ArrayList<Double> newPatternDoubles = new ArrayList<Double>();
        double proportionOfSimulatedClaimAlreadyPaidOutAsActuals = 0;

        double priorCumulativeValue = 0d;
//        Now go around the paid updates and figure out where they fall against what percentage of ultimate claim.
        for (Map.Entry<DateTime, Double> entry : claimPaidUpdates.entrySet()) {
            Period period = new Period(startDateForPattern, startDateForPattern.plusDays(1));
            if(!startDateForPattern.equals(entry.getKey())) {
                period = new Period(startDateForPattern, entry.getKey().minusDays(1));
            }
            final Double aDouble = Math.abs(( entry.getValue() - priorCumulativeValue) / claimRoot.getUltimate());
            newPatternPeriods.add(period);
            newPatternDoubles.add(aDouble);
            proportionOfSimulatedClaimAlreadyPaidOutAsActuals += aDouble;
            priorCumulativeValue = entry.getValue();
        }
        if (proportionOfSimulatedClaimAlreadyPaidOutAsActuals > 1) {
            throw new RuntimeException("Actual entries are greater than ultimate claim! This is not allowed . Claim: " + claimRoot.toString() + " Proportion of Claim already paid: " + proportionOfSimulatedClaimAlreadyPaidOutAsActuals);
        }

//        Now that we have the actual part of the pattern contructed, add the remainder of the pattern rescaled against it's incremental amounts.

        //        This pattern has the rescaled original pattern ignoring updates.
        PatternPacket patternPacket = payoutPattern.rescalePatternToUpdateDate(updateDate, startDateForPattern, false);

        final List<Period> oldPeriod = patternPacket.getCumulativePeriods();
        for (Period period : oldPeriod) {
            newPatternPeriods.add(period);
        }

        final List<Double> incrementalValues = patternPacket.getIncrementalValues();
        double cumulatedNewPatternValue = 0d;
        for (Double oldPatternValue : incrementalValues) {
            double incrementalNewPatternValue = ( oldPatternValue * (1 - proportionOfSimulatedClaimAlreadyPaidOutAsActuals));
            cumulatedNewPatternValue += incrementalNewPatternValue;
            newPatternDoubles.add(cumulatedNewPatternValue + proportionOfSimulatedClaimAlreadyPaidOutAsActuals);
        }

        if(cumulatedNewPatternValue + proportionOfSimulatedClaimAlreadyPaidOutAsActuals != 1 ) {
            throw new PatternSumNotOneException(newPatternDoubles);
        }

        return new PatternPacket(payoutPattern, newPatternDoubles, newPatternPeriods);

    }

    public GrossClaimRoot claimWithAdjustedPattern(PatternPacket payoutPattern, ClaimRoot claimRoot, DateTime updateDate) {
        DateTime startDateForPatterns = base.startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull());
        return new GrossClaimRoot(claimRoot, adjustedPattern(payoutPattern, claimRoot, updateDate), startDateForPatterns);
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
