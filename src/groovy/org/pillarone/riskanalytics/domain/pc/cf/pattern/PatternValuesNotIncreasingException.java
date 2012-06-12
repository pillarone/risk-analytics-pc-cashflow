package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternValuesNotIncreasingException extends RuntimeException {

    public static final String PATTERN_VALUES_NOT_INCREASING = "Incremental Pattern Values go -ve! \n";

    public PatternValuesNotIncreasingException(List<Double> cumulativePeriods) {
        super(PATTERN_VALUES_NOT_INCREASING + PatternSumNotOneException.getErrorPeriods(cumulativePeriods).toString());
    }
}
