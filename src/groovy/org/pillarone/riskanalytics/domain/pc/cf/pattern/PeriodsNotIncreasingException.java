package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PeriodsNotIncreasingException extends SimulationException {

    public static final String PERIODS_NOT_INCREASING = "Periods need to be increasing! \n";

    public PeriodsNotIncreasingException(List<Period> cumulativePeriods) {
        super(PERIODS_NOT_INCREASING + getErrorPeriods(cumulativePeriods).toString());
    }

    public static StringBuilder getErrorPeriods(List<Period> cumulativePeriods) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Pattern Entry : days " + " \n");

        int i = 1;
        for(Period period : cumulativePeriods ) {
            String s = i + "  :  " + period.getDays() + "\n";
            stringBuilder.append(s);
            i++;
        }
        return stringBuilder;
    }
}
