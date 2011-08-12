package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternUtils {

    public static PatternPacket filterPattern(List<PatternPacket> patterns, ConstrainedString criteria,
                                              Class<? extends IPatternMarker> patternMarker) {
        return filterPattern(patterns, criteria, patternMarker, true);
    }

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

}
