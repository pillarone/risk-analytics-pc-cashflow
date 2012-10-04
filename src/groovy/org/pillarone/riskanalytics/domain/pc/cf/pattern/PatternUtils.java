package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.jfree.data.time.Day;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.runOff.RunOffPatternUtils;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

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
     *
     * @param patterns      from different origins. Function will fail if one origin would provide several patterns implementing
     *                      the same IPatternMarker interface.
     * @param criteria      the selected component is used for the comparison with the pattern origin
     * @param patternMarker is necessary as a component might produce several patterns of different types, but only one
     *                      per type (see PayoutReportingCombinedPatterns)
     * @return the cloned pattern with the origin component matching the selected component in criteria
     */
    public static PatternPacket filterPattern(List<PatternPacket> patterns, ConstrainedString criteria,
                                              Class<? extends IPatternMarker> patternMarker) {
        return filterPattern(patterns, criteria, patternMarker, true);
    }

    /**
     * @param patterns      from different origins. Function will fail if one origin would provide several patterns implementing
     *                      the same IPatternMarker interface.
     * @param criteria      the selected component is used for the comparison with the pattern origin
     * @param patternMarker is necessary as a component might produce several patterns of different types, but only one
     *                      per type (see PayoutReportingCombinedPatterns)
     * @param returnClone   return a clone of the pattern matching the criteria
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
     *
     * @param payoutPattern
     * @param reportingPattern
     */
    public static void synchronizePatterns(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        if (reportingPattern == null || payoutPattern == null) return;
        if (reportingPattern.getLastCumulativePeriod().getMonths() - payoutPattern.getLastCumulativePeriod().getMonths() > 0) {
            throw new IllegalArgumentException("reporting pattern longer than payout pattern ("
                    + reportingPattern.getLastCumulativePeriod() + ", " + payoutPattern.getLastCumulativePeriod() + ")");
        }
        for (int payoutIdx = 0; payoutIdx < payoutPattern.size(); payoutIdx++) {
            Period payoutPeriod = payoutPattern.getCumulativePeriods().get(payoutIdx);
            if (reportingPattern.size() == payoutIdx) return;   // payout pattern may be longer
            Period reportingPeriod = reportingPattern.getCumulativePeriods().get(payoutIdx);
            if (payoutPeriod.equals(reportingPeriod)) {
                // all fine, do nothing
            } else {
                int difference = payoutPeriod.minus(reportingPeriod).getMonths();
                if (difference < 0) {
                    reportingPattern.insertTrivialPeriod(payoutPeriod, payoutIdx);
                } else if (difference > 0) {
                    payoutPattern.insertTrivialPeriod(reportingPeriod, payoutIdx);
                }
            }
        }
    }

    /**
     * @param originalPattern
     * @param cumulativePeriods
     * @param cumulativePercentages
     * @param lastReportedDateFromHistoricClaim
     *
     * @return an adjusted pattern or the originalPattern if cumulativePeriods is empty
     */
    public static PatternPacket adjustedPattern(PatternPacket originalPattern, List<Period> cumulativePeriods,
                                                List<Double> cumulativePercentages,
                                                DateTime baseDate, DateTime updateDate,
                                                DateTime lastReportedDateFromHistoricClaim,
                                                DateTimeUtilities.Days360 days360) {
        if (cumulativePeriods.size() != cumulativePercentages.size()) {
            throw new IllegalArgumentException("List arguments need to be of same size (periods: "
                    + cumulativePeriods.size() + ", cumulativePercentages: " + cumulativePercentages.size());
        }
        if (cumulativePeriods.isEmpty()) {
            return originalPattern;
        }

        TreeMap<DateTime, Double> absolutePattern = originalPattern.absolutePattern(baseDate, false);
        TreeMap<DateTime, Double> absoluteIncrementalPattern = originalPattern.absolutePattern(baseDate, true);
        double elapsedMonths = days360.days360(baseDate, updateDate) / 30d;
        DateTime nextPaymentDate = absolutePattern.higherEntry(updateDate).getKey();

// --------------- Last paid reported interpolation.
//      The two if statements protect against tail cases. Spec assumes that pattern is zero if lastReportedDate is the same as the base date. See art-863.
        double paidByLatestReportedDate = 0;
        if (!lastReportedDateFromHistoricClaim.equals(baseDate)) {
            paidByLatestReportedDate = RunOffPatternUtils.dateRatioInterpolation(baseDate, null, lastReportedDateFromHistoricClaim, absolutePattern, days360);
        }
        double cumulatedPaidByNextPaymentDate = 0;
        if (!nextPaymentDate.equals(baseDate)) {
            cumulatedPaidByNextPaymentDate = RunOffPatternUtils.dateRatioInterpolation(baseDate, null, nextPaymentDate, absolutePattern, days360);
        }
        double incrementalPaidByNextPaymentDate = cumulatedPaidByNextPaymentDate - paidByLatestReportedDate;
        double paidAfterNextPaymentDate = 1 - cumulatedPaidByNextPaymentDate;
        double outstandingToBePaid = incrementalPaidByNextPaymentDate + paidAfterNextPaymentDate;
        double cumulativeValue = actualCumulativePaid(cumulativePercentages);
        double scaleFactorForUnpaidAmount = 1 - cumulativeValue;

        TreeMap<DateTime, Double> patternEntriesAfterUpdate = filterMap(absolutePattern, updateDate);
        boolean firstPass = true;
        for (Map.Entry<DateTime, Double> entry : patternEntriesAfterUpdate.entrySet()) {
            Period period = new Interval(baseDate, entry.getKey().minusDays(1)).toPeriod();
            double incrementalValue = absoluteIncrementalPattern.get(entry.getKey());
            double scaledPattern = 0d;
            if(firstPass) {
                scaledPattern = incrementalPaidByNextPaymentDate / outstandingToBePaid;
                firstPass = false;
            } else {
                scaledPattern = incrementalValue / outstandingToBePaid;
            }
            cumulativeValue = cumulativeValue + scaledPattern * scaleFactorForUnpaidAmount;
            cumulativePeriods.add(period);
            cumulativePercentages.add(cumulativeValue);
        }
        checkNonDuplicatedDays(cumulativePeriods, cumulativePercentages, baseDate);

        return new PatternPacket(originalPattern, cumulativePercentages, cumulativePeriods);
    }

    private static double actualCumulativePaid(List<Double> cumulativePercentages) {
        double cumulativeValue = 0d;
        if( cumulativePercentages.size() > 0 ) {
            cumulativeValue = cumulativePercentages.get(cumulativePercentages.size() - 1);
        }
        return cumulativeValue;
    }

    private static TreeMap<DateTime, Double> filterMap(TreeMap<DateTime, Double> aMap, DateTime filterDate) {
        final TreeMap<DateTime, Double> dateTimeDoubleTreeMap = new TreeMap<DateTime, Double>();
        for (Map.Entry<DateTime, Double> entry : aMap.entrySet()) {
            if(entry.getKey().minusDays(1).isAfter(filterDate)) {
                dateTimeDoubleTreeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return dateTimeDoubleTreeMap;
    }

    /**
     * Check the period lengths. if they have the same day, and contain the same value for the cumulated pattern value, the
     * entry may be safely removed. If they have the same day, and different pattern values, this is an insanity. Attempt to halt the simulation.
     *
     * @param cumulativePeriods
     * @param cumulativePercentages
     */
    private static void checkNonDuplicatedDays(List<Period> cumulativePeriods, List<Double> cumulativePercentages, DateTime baseDate) {
        final List<Period> copyPeriods = new ArrayList<Period>();
        copyPeriods.addAll(cumulativePeriods);

        Period priorPeriod = Period.days(-1);
        int index = 0;
        for( Period period : copyPeriods ) {
            if(priorPeriod.toDurationFrom(baseDate).equals(period.toDurationFrom(baseDate)  )) {
                if(cumulativePercentages.get(index) .equals(cumulativePercentages.get(index - 1))) {
                    cumulativePercentages.remove(index);
                    cumulativePeriods.remove(period);
                } else throw new PatternValuesNotIncreasingException(cumulativePercentages, cumulativePeriods,
                        "An inferred pattern carries different values for payouts on the same millisecond(!). Pattern index : " + index + " Please contact development. " , baseDate );
            }
            priorPeriod = period;
            index++;
        }
    }

    /**
     * @param originalPattern  pattern to be adjusted if claimUpdates is not empty
     * @param claimUpdates     actual claim updates used to adjust the pattern
     * @param ultimate         adjusted ultimate used for scaling
     * @param baseDate         might be either the projection start date or the occurrence date
     * @param updateDate       after which the effective projection starts/date with ending history
     * @param lastReportedDate
     * @param days360
     * @return adjusted pattern if claimUpdates is not empty, otherwise the originalPattern is returned
     */
    public static PatternPacket adjustedPattern(PatternPacket originalPattern, TreeMap<DateTime, Double> claimUpdates,
                                                double ultimate, DateTime baseDate, DateTime occurrenceDate,
                                                DateTime updateDate, DateTime lastReportedDate, DateTimeUtilities.Days360 days360) {
        if(Math.abs(ultimate) == 0d ) {
            throw new SimulationException("Insanity detected; Attempted to develop historic claim with 0 ultimate value. Pattern : " + originalPattern.toString() + " ... \n \n Claim Updates : " + claimUpdates.toString()) ;
        }

        if (claimUpdates.isEmpty()) {
            return adjustForNoClaimUpdates(originalPattern, baseDate, updateDate);
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
        return adjustedPattern(originalPattern, cumulativePeriods, cumulativeValues, baseDate, updateDate, lastReportedDate, days360);
    }

    public static PatternPacket adjustForNoClaimUpdates(PatternPacket originalPattern, DateTime baseDate, DateTime updateDate) {
        List<Period> cumulativePeriods = new ArrayList<Period>();
        List<Double> cumulativeValues = new ArrayList<Double>();
        for (int index = 0; index < originalPattern.size(); index++) {
            if (baseDate.plus(originalPattern.getCumulativePeriod(index)).isAfter(updateDate)) {
                if(originalPattern.getCumulativePeriod(index).equals(new Period(0)) ) {
                    cumulativePeriods.add(originalPattern.getCumulativePeriod(index));
                } else {
                    cumulativePeriods.add(originalPattern.getCumulativePeriod(index).minusDays(1));
                }
                cumulativeValues.add(originalPattern.getCumulativeValues().get(index));
            }
        }
        return new PatternPacket(originalPattern, cumulativeValues, cumulativePeriods);
    }

    /**
     * @param pattern                      'original' pattern
     * @param index                        befor cumulatedInterpolationPeriod
     * @param cumulatedInterpolationPeriod time point for interpolation
     * @return cumulated interpolation rate at cumulatedInterpolationPeriod
     */
    public static double interpolatedRate(PatternPacket pattern, int index, double cumulatedInterpolationPeriod) {
        if (index == pattern.size()) return 1d; // todo(sku): is this correct?
        int nextCumulatedPeriodInMonths = pattern.getCumulativePeriod(index).getMonths();
        int previousCumulatedPeriodInMonths = index == 0 ? 0 : pattern.getCumulativePeriod(index - 1).getMonths();
        double nextCumulatedValue = pattern.getCumulativeValues().get(index);
        double previousCumulatedValue = index == 0 ? 0 : pattern.getCumulativeValues().get(index - 1);
        return ((nextCumulatedPeriodInMonths - cumulatedInterpolationPeriod) * previousCumulatedValue
                + (cumulatedInterpolationPeriod - previousCumulatedPeriodInMonths) * nextCumulatedValue)
                / (nextCumulatedPeriodInMonths - previousCumulatedPeriodInMonths);
    }

    public static PatternPacket getTrivialSynchronizePatterns(PatternPacket pattern, Class<? extends IPatternMarker> patternMarker) {
        PatternPacket trivialSynchronizedPattern = new PatternPacket.TrivialPattern(patternMarker);
        synchronizePatterns(pattern, trivialSynchronizedPattern);
        return trivialSynchronizedPattern;
    }
}
