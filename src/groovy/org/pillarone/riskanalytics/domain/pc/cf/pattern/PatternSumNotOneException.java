package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.exceptionUtils.ExceptionUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternSumNotOneException extends SimulationException {

    public static final String PATTERN_SUM_NOT_ONE = "Incremental Pattern Values not 1! \n";


    public PatternSumNotOneException(List<Double> cumulativePeriods) {
        super(PATTERN_SUM_NOT_ONE + ExceptionUtils.getErrorPeriods(cumulativePeriods).toString());
    }

}
