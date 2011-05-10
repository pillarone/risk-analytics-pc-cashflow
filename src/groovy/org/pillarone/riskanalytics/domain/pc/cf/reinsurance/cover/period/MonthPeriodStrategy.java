package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MonthPeriodStrategy extends AbstractPeriodStrategy {

    private DateTime startCover = new DateTime(2011,1,1,0,0,0,0);
    private Integer numberOfMonths = 12;


    public IParameterObjectClassifier getType() {
        return PeriodStrategyType.MONTHS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("startCover", startCover);
        parameters.put("numberOfYears", numberOfMonths);
        return parameters;
    }

    public DateTime getStartCover() {
        return startCover;
    }

    public DateTime getEndCover() {
        return startCover.plus(Period.years(numberOfMonths));
    }

    public int getNumberOfPeriods() {
        return numberOfMonths;
    }

    public List<DateTime> getDates() {
        List<DateTime> dates = new ArrayList<DateTime>();
        dates.add(startCover);
        for (int i = 1; i <= getNumberOfPeriods(); i++) {
            dates.add(startCover.plus(Period.years(i)));
        }
        return dates;
    }
}
