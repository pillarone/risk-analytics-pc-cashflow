package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): read complete column instead of every single row
public abstract class AbstractPatternStrategy extends AbstractParameterObject {

    protected static List<Double> getPatternValues(ConstrainedMultiDimensionalParameter pattern, int monthColumnIndex, int patternColumnIndex) {
        if (pattern.getValues().size() > 0 && pattern.getValues().get(0) instanceof List) {
           int cumulatedMonths = InputFormatConverter.getInt(pattern.getValueAt(pattern.getTitleRowCount(), monthColumnIndex));
           List<Double> patternValues = new ArrayList<Double>();
           if (cumulatedMonths > 0) {
               patternValues.add(0d);    // make sure paid is 0 if no payout ratio is defined for period 0
           }
           for (int row = pattern.getTitleRowCount(); row < pattern.getRowCount(); row++) {
               patternValues.add(InputFormatConverter.getDouble(pattern.getValueAt(row, patternColumnIndex)));
           }
           return patternValues;
       }
       return pattern.getValues();
    }

    protected static List<Period> getCumulativePeriods(ConstrainedMultiDimensionalParameter pattern, int monthColumnIndex) {
        List<Period> periods = new ArrayList<Period>();
        boolean firstPeriod = true;
        for (int row = pattern.getTitleRowCount(); row < pattern.getRowCount(); row++) {
            int cumulatedMonths = InputFormatConverter.getInt(pattern.getValueAt(row, monthColumnIndex));
            if (firstPeriod && cumulatedMonths > 0) periods.add(Period.months(0));   // make sure paid is 0 if no payout ratio is defined for period 0
            firstPeriod = false;
            periods.add(Period.months(cumulatedMonths));
        }
        return periods;
    }

    protected static PatternPacket getCumulativePattern(ConstrainedMultiDimensionalParameter cumulativePattern,
                                                 String columnName, Class<? extends IPatternMarker> patternMarker) {
        int columnMonthIndex = cumulativePattern.getColumnIndex(PatternTableConstraints.MONTHS);
        List<Double> cumulativeValues = getPatternValues(cumulativePattern, columnMonthIndex,
                cumulativePattern.getColumnIndex(columnName));
        List<Period> cumulativePeriods = getCumulativePeriods(cumulativePattern, columnMonthIndex);
        return new  PatternPacket(patternMarker, cumulativeValues, cumulativePeriods);
    }

    protected static PatternPacket getIncrementalPattern(ConstrainedMultiDimensionalParameter incrementalPattern,
                                                       String columnName, Class<? extends IPatternMarker> patternMarker) {
        int columnMonthIndex = incrementalPattern.getColumnIndex(PatternTableConstraints.MONTHS);
        List<Double> incrementalValues = getPatternValues(incrementalPattern, columnMonthIndex,
                incrementalPattern.getColumnIndex(columnName));
        List<Double> cumulativeValues = getCumulativePatternValues(incrementalValues);
        List<Period> cumulativePeriods = getCumulativePeriods(incrementalPattern, columnMonthIndex);
        return new PatternPacket(IRecoveryPatternMarker.class, cumulativeValues, cumulativePeriods);
    }

    protected static List<Double> getCumulativePatternValues(List<Double> incrementalValues) {
        List<Double> cumulativeValues = new ArrayList<Double>(incrementalValues.size());
        double cumulative = 0d;
        for (Double increment : incrementalValues) {
            cumulative += increment;
            cumulativeValues.add(cumulative);
        }
        return cumulativeValues;
    }
}
