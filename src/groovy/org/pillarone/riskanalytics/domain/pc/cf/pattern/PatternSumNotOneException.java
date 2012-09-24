package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternSumNotOneException extends RuntimeException {

    public static final String PATTERN_SUM_NOT_ONE = "Incremental Pattern Values not 1! \n";
    private static final DecimalFormat format = new DecimalFormat("#.#####");

    public PatternSumNotOneException(List<Double> cumulativePeriods) {
        super(PATTERN_SUM_NOT_ONE + getErrorPeriods(cumulativePeriods).toString());
    }

    public static StringBuilder getErrorPeriods(List<Double> doubles) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Pattern Entry : double value " + " \n");


        int i = 1;
        for(Double aDouble : doubles) {
            String s = i + "  :  " + format.format(aDouble) + "\n";
            stringBuilder.append(s);
            i++;
        }
        return stringBuilder;
    }

    public static StringBuilder getErrorPeriods(List<Double> doubles, List<Period> periods, DateTime baseDate) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Pattern Entry : double value : milliseconds" + " \n");

        int i = 1;
        for(Double aDouble : doubles) {
            String s = i + "  :  " + format.format(aDouble) + "  :  " + periods.get(i-1).toDurationFrom(baseDate).getMillis() + "\n";
            stringBuilder.append(s);
            i++;
        }
        return stringBuilder;
    }


}
