package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternUtils {

    /**
     * Helper method calling the equally name method, setting returnClone = true.
     * @param patterns from different origins. Function will fail if one origin would provide several patterns implementing
     *                 the same IPatternMarker interface.
     * @param criteria the selected component is used for the comparison with the pattern origin
     * @param patternMarker is necessary as a component might produce several patterns of different types, but only one
     *                      per type (see PayoutReportingCombinedPatterns)
     * @return the cloned pattern with the origin component matching the selected component in criteria
     */
    public static PatternPacket filterPattern(List<PatternPacket> patterns, ConstrainedString criteria,
                                              Class<? extends IPatternMarker> patternMarker) {
        return filterPattern(patterns, criteria, patternMarker, true);
    }

    /**
     * @param patterns from different origins. Function will fail if one origin would provide several patterns implementing
     *                 the same IPatternMarker interface.
     * @param criteria the selected component is used for the comparison with the pattern origin
     * @param patternMarker is necessary as a component might produce several patterns of different types, but only one
     *                      per type (see PayoutReportingCombinedPatterns)
     * @param returnClone return a clone of the pattern matching the criteria
     * @return the pattern with the origin component matching the selected component in criteria
     */
    public static PatternPacket filterPattern(List<PatternPacket> patterns, ConstrainedString criteria,
                                              Class<? extends IPatternMarker> patternMarker, boolean returnClone) {
        for (PatternPacket pattern : patterns) {
            if (pattern.getOrigin().equals(criteria.getSelectedComponent())
                && pattern.samePatternType(patternMarker)) {
                return returnClone ? pattern.clone() : pattern;
            }
        }
        return null;
    }

    /**
     * @return payout and reported pattern have the same period entries. True even if one of them is null
     */
    public static boolean synchronizedPatterns(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        return hasSameCumulativePeriods(payoutPattern, reportingPattern, true);
    }

    public static boolean hasSameCumulativePeriods(PatternPacket payout, PatternPacket reporting, boolean payoutPatternMaybeLonger) {
        boolean sameSizeOrPayoutLonger = payout.size() >= reporting.size();
        boolean samePeriods = sameSizeOrPayoutLonger;
        if (sameSizeOrPayoutLonger) {
            for (int developmentPeriod = 0; samePeriods && developmentPeriod < reporting.size(); developmentPeriod++) {
                samePeriods = payout.incrementMonths(developmentPeriod).equals(reporting.incrementMonths(developmentPeriod));
            }
        }
        return samePeriods;
    }

    /**
     * Both parameters are modified if a period is missing within one pattern it is inserted. Missing reporting periods
     * at the end are neglected.
     * @param payoutPattern
     * @param reportingPattern
     */
    public static void synchronizePatterns(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        if (reportingPattern == null || payoutPattern == null) return;
        if (reportingPattern.getLastCumulativePeriod().getMonths() - payoutPattern.getLastCumulativePeriod().getMonths() > 0) {
            throw new IllegalArgumentException("reporting pattern longer than payout pattern ("
                    + reportingPattern.getLastCumulativePeriod() + ", " + payoutPattern.getLastCumulativePeriod() +")");
        }
        for (int payoutIdx = 0; payoutIdx < payoutPattern.size(); payoutIdx++) {
            Period payoutPeriod = payoutPattern.getCumulativePeriods().get(payoutIdx);
            if (reportingPattern.size() == payoutIdx) return;   // payout pattern may be longer
            Period reportingPeriod = reportingPattern.getCumulativePeriods().get(payoutIdx);
            if (payoutPeriod.equals(reportingPeriod)) {
                // all fine, do nothing
            }
            else {
                int difference = payoutPeriod.minus(reportingPeriod).getMonths();
                if (difference < 0) {
                    reportingPattern.insertTrivialPeriod(payoutPeriod, payoutIdx);
                }
                else if (difference > 0) {
                    payoutPattern.insertTrivialPeriod(reportingPeriod, payoutIdx);
                }
            }
        }
    }

    /**
     *
     * @param originalPattern
     * @param cumulativePeriods
     * @param cumulativePercentages
     * @return an adjusted pattern or the originalPattern if cumulativePeriods is empty
     */
    public static PatternPacket adjustedPattern(PatternPacket originalPattern, List<Period> cumulativePeriods,
                                                List<Double> cumulativePercentages, DateTime baseDate, DateTime updateDate) {
        if (cumulativePeriods.size() != cumulativePercentages.size()) {
            throw new IllegalArgumentException("List arguments need to be of same size (periods: "
                    + cumulativePeriods.size() + ", cumulativePercentages: " + cumulativePercentages.size());
        }
        if (cumulativePeriods.isEmpty()) {
            return originalPattern;
        }

        double elapsedMonths = days360(baseDate, updateDate) / 30d;
        DateTime lastReportedDate = baseDate.plus(cumulativePeriods.get(cumulativePeriods.size() - 1));
        double elapsedMonthsTillLastReportedDate = days360(baseDate, lastReportedDate) / 30d;
        int nextPatternIndexLastReported = originalPattern.thisOrNextPayoutIndex(elapsedMonthsTillLastReportedDate);
        Integer nextPatternIndex = originalPattern.thisOrNextPayoutIndex(elapsedMonths);
        double paidByLatestReportedDate = interpolatedRate(originalPattern, nextPatternIndexLastReported, elapsedMonthsTillLastReportedDate);
        int nextCumulatedPeriod = originalPattern.getCumulativePeriod(nextPatternIndex).getMonths();
        double cumulatedPaidByNextPaymentDate = interpolatedRate(originalPattern, nextPatternIndex + 1, nextCumulatedPeriod);
        double incrementalPaidByNextPaymentDate = cumulatedPaidByNextPaymentDate - paidByLatestReportedDate;
        Double lastCumulativeRate = cumulativePercentages.get(cumulativePercentages.size() - 1);
        double effectiveOutstandingRate = 1 - lastCumulativeRate;
        double outstandingRate = incrementalPaidByNextPaymentDate + (1 - cumulatedPaidByNextPaymentDate);
        for (int index = nextPatternIndex; index < originalPattern.size(); index++) {
            double originalCumulativeValue = originalPattern.getCumulativeValues().get(index);
            double originalIncrement = index == 0 ? originalCumulativeValue : originalCumulativeValue - originalPattern.getCumulativeValues().get(index - 1);
            double adjustedIncrement = originalIncrement / outstandingRate;
            if (index == nextPatternIndex) {
                adjustedIncrement = incrementalPaidByNextPaymentDate / outstandingRate;
            }
            lastCumulativeRate += adjustedIncrement * effectiveOutstandingRate;
            cumulativePercentages.add(lastCumulativeRate);
            // Note the "-1" in the date original pay date calculation, this means that when using the period start
            // date as the payout base a payout specified at month 12 will be seen in the first annual
            cumulativePeriods.add(originalPattern.getCumulativePeriods().get(index).minusDays(1)); //.minus(differenceBaseDateOccurrenceDate));
        }
        return new PatternPacket(originalPattern, cumulativePercentages, cumulativePeriods);
    }

    /**
     *
     * @param originalPattern pattern to be adjusted if claimUpdates is not empty
     * @param claimUpdates actual claim updates used to adjust the pattern
     * @param ultimate adjusted ultimate used for scaling
     * @param baseDate might be either the projection start date or the occurrence date
     * @param updateDate after which the effective projection starts/date with ending history
     * @return adjusted pattern if claimUpdates is not empty, otherwise the originalPattern is returned
     */
    public static PatternPacket adjustedPattern(PatternPacket originalPattern, TreeMap<DateTime, Double> claimUpdates,
                                                double ultimate, DateTime baseDate, DateTime occurrenceDate, DateTime updateDate) {
        if (claimUpdates.isEmpty()) {
            List<Period> cumulativePeriods = new ArrayList<Period>();
            List<Double> cumulativeValues = new ArrayList<Double>();
            for (int index = 0; index < originalPattern.size(); index++) {
                if (baseDate.plus(originalPattern.getCumulativePeriod(index)).isAfter(updateDate)) {
                    cumulativePeriods.add(originalPattern.getCumulativePeriod(index).minusDays(1));
                    cumulativeValues.add(originalPattern.getCumulativeValues().get(index));
                }
            }
            return new PatternPacket(originalPattern, cumulativeValues, cumulativePeriods);
        }
        List<Period> cumulativePeriods = new ArrayList<Period>();
        List<Double> cumulativeValues = new ArrayList<Double>();
        if (!claimUpdates.containsKey(baseDate)) {
            cumulativePeriods.add(new Period());
            cumulativeValues.add(0d);
        }
        for (Map.Entry<DateTime, Double> claimUpdate : claimUpdates.entrySet()) {
            cumulativeValues.add(Math.abs(claimUpdate.getValue() / ultimate));
            cumulativePeriods.add(new Period(baseDate, claimUpdate.getKey()));
        }
        return adjustedPattern(originalPattern, cumulativePeriods, cumulativeValues, baseDate, updateDate);
    }

    /**
     * @param pattern 'original' pattern
     * @param index befor cumulatedInterpolationPeriod
     * @param cumulatedInterpolationPeriod time point for interpolation
     * @return cumulated interpolation rate at cumulatedInterpolationPeriod
     */
    public static double interpolatedRate(PatternPacket pattern, int index, double cumulatedInterpolationPeriod) {
        if (index == pattern.size()) return 1d; // todo(sku): is this correct?
        int nextCumulatedPeriodInMonths = pattern.getCumulativePeriod(index).getMonths();
        int previousCumulatedPeriodInMonths = index == 0 ?  0 : pattern.getCumulativePeriod(index - 1).getMonths();
        double nextCumulatedValue = pattern.getCumulativeValues().get(index);
        double previousCumulatedValue = index == 0 ? 0 : pattern.getCumulativeValues().get(index - 1);
        return ((nextCumulatedPeriodInMonths - cumulatedInterpolationPeriod) * previousCumulatedValue
                + (cumulatedInterpolationPeriod - previousCumulatedPeriodInMonths) * nextCumulatedValue)
                / (nextCumulatedPeriodInMonths - previousCumulatedPeriodInMonths);
    }

    /**
     * Utility function for use mainly in interest calculation and interpolations.
     *
     * @param startDate
     * @param endDate
     * @return integer number of days between dates assuming every month has 30 days.
     */
    public static int days360(DateTime startDate, DateTime endDate) {

        int startDayOfMonth = (startDate.getDayOfMonth() == 31) ? 30 : startDate.getDayOfMonth();
        int startDay = startDate.getMonthOfYear() * 30 + startDayOfMonth;
        int endDay = (endDate.getYear() - startDate.getYear()) * 360 + endDate.getMonthOfYear() * 30 + endDate.getDayOfMonth();

        return endDay - startDay;
    }

    public static double days360ProportionOfPeriod(DateTime periodStart, DateTime periodEnd, DateTime toDate  ) {
        double daysInPeriod = days360(periodStart, periodEnd);
        double daysToUpdate = days360(periodStart, toDate);
        return  daysToUpdate / daysInPeriod;
    }

    public static PatternPacket getTrivialSynchronizePatterns(PatternPacket pattern, Class<? extends IPatternMarker> patternMarker) {
        PatternPacket trivialSynchronizedPattern = new PatternPacket.TrivialPattern(patternMarker);
        synchronizePatterns(pattern, trivialSynchronizedPattern);
        return trivialSynchronizedPattern;
    }
}
