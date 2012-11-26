package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.exceptionUtils.ExceptionUtils;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternValuesNotIncreasingException extends SimulationException {

    public static final String PATTERN_VALUES_NOT_INCREASING = "Incremental Pattern Values go -ve! \n";

    public PatternValuesNotIncreasingException(List<Double> cumulativePeriods) {
        super(PATTERN_VALUES_NOT_INCREASING + ExceptionUtils.getErrorPeriods(cumulativePeriods).toString());
    }

    public PatternValuesNotIncreasingException(List<Double> cumulativePercentages, List<Period> cumPeriods, String errorMessage, DateTime baseDate) {
        super(errorMessage + " \n \n  " + ExceptionUtils.getErrorPeriods(cumulativePercentages, cumPeriods, baseDate).toString());
    }
}
