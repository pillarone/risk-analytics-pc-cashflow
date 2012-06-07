package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternSumNotOneException extends RuntimeException {

    public static final String PATTERN_SUM_NOT_ONE = "Incremental Pattern Values not 1! \n";

    public PatternSumNotOneException(List<Double> cumulativePeriods) {
        super(PATTERN_SUM_NOT_ONE + getErrorPeriods(cumulativePeriods).toString());
    }

    public static StringBuilder getErrorPeriods(List<Double> doubles) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Pattern Entry : double value " + " \n");

        int i = 1;
        for(Double aDouble : doubles) {
            String s = i + "  :  " + aDouble + "\n";
            stringBuilder.append(s);
            i++;
        }
        return stringBuilder;
    }


}
