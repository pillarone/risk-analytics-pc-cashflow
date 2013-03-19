package org.pillarone.riskanalytics.domain.pc.cf.exceptionUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.domain.pc.cf.global.SimulationConstants;
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

    /**
     * This function ( Log10(value) ) ^ 2 / 1000 + EPSILON
     *
     * increases as values get larger, but remains below the materiality threshold for error checking. Use it when we want
     * to throw an excpetion where inconsistent numbers are detected.
     *
     * @return
     */
    public static double getCheckValue(double value) {
        if(value == 0d) {
            return SimulationConstants.EPSILON;
        }
        double positiveValue = Math.abs(value);
        double log10Cashflow = Math.log10(positiveValue);
        return ((log10Cashflow * log10Cashflow) / 1000d) + SimulationConstants.EPSILON;
    }
}
