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
            if (firstPeriod && cumulatedMonths > 0)
                periods.add(Period.months(0));   // make sure paid is 0 if no payout ratio is defined for period 0
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
        return new PatternPacket(patternMarker, cumulativeValues, cumulativePeriods);
    }

    protected static PatternPacket getIncrementalPattern(ConstrainedMultiDimensionalParameter incrementalPattern,
                                                         String columnName, Class<? extends IPatternMarker> patternMarker) {
        int columnMonthIndex = incrementalPattern.getColumnIndex(PatternTableConstraints.MONTHS);
        List<Double> incrementalValues = getPatternValues(incrementalPattern, columnMonthIndex,
                incrementalPattern.getColumnIndex(columnName));
        List<Double> cumulativeValues = getCumulativePatternValues(incrementalValues);
        List<Period> cumulativePeriods = getCumulativePeriods(incrementalPattern, columnMonthIndex);
        return new PatternPacket(patternMarker, cumulativeValues, cumulativePeriods);
    }

    protected static PatternPacket getAgeToAgePattern(ConstrainedMultiDimensionalParameter ageToAgePattern,
                                                      String columnName, Class<? extends IPatternMarker> patternMarker) {
        int columnMonthIndex = ageToAgePattern.getColumnIndex(PatternTableConstraints.MONTHS);
        List<Double> ageToAgeValues = getPatternValues(ageToAgePattern, columnMonthIndex,
                ageToAgePattern.getColumnIndex(columnName));
        List<Double> cumulativeValues = getCumulativePatternValuesFromLinkRatios(ageToAgeValues);
        List<Period> cumulativePeriods = getCumulativePeriods(ageToAgePattern, columnMonthIndex);
        return new PatternPacket(patternMarker, cumulativeValues, cumulativePeriods);
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

    protected static List<Double> getCumulativePatternValuesFromLinkRatios(List<Double> linkRatios) {
        List<Double> cumulativeValues = new ArrayList<Double>(linkRatios.size());
        boolean firstEntryNull = false;
        if (linkRatios.get(0) == 0) {
            firstEntryNull = true;
            linkRatios.remove(0);
        }
        double product = 1.0;
        for (Double ratio : linkRatios) {
            product *= ratio;
        }
        cumulativeValues.add(0, 1.0 / product);
        for (int i = 1; i < linkRatios.size(); i++) {
            cumulativeValues.add(i, linkRatios.get(i - 1) * cumulativeValues.get(i - 1));
        }
        if (firstEntryNull) {
            cumulativeValues.add(0, 0d);
        }
        return cumulativeValues;
    }
}
