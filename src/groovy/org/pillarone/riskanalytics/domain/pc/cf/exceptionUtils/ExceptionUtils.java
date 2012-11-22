package org.pillarone.riskanalytics.domain.pc.cf.exceptionUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * author simon.parten @ art-allianz . com
 */
public class ExceptionUtils {

    private static final DecimalFormat format = new DecimalFormat("#.#####");

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

    public static StringBuilder getErrorDatesAndValues( NavigableMap<DateTime, Double> theMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" Date entry : Value " + "  \n");

        for (Map.Entry<DateTime, Double> entry : theMap.entrySet()) {
            String s = DateTimeUtilities.formatDate.print(entry.getKey()) + " : " + entry.getValue() + " \n";
            stringBuilder.append(s);
        }

        return stringBuilder;

    }



}
