package org.pillarone.riskanalytics.domain.pc.cf.global;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.AbstractPeriodStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AnnualPeriodStrategy extends AbstractPeriodStrategy {

    private DateTime startCover = new DateTime(new DateTime().getYear()+1, 1, 1, 0, 0, 0, 0);
    private Integer numberOfYears = 3;

    public IParameterObjectClassifier getType() {
        return PeriodStrategyType.ANNUAL;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("startCover", startCover);
        parameters.put("numberOfYears", numberOfYears);
        return parameters;
    }

    public DateTime getStartCover() {
        return startCover;
    }

    public DateTime getEndCover() {
        return startCover.plus(Period.years(numberOfYears));
    }

    public int getNumberOfPeriods() {
        return numberOfYears;
    }

    public List<DateTime> getDates() {
        List<DateTime> dates = new ArrayList<DateTime>();
        dates.add(startCover);
        for (int i = 1; i <= getNumberOfPeriods(); i++) {
            dates.add(startCover.plus(Period.years(i)));
        }
        return dates;
    }

    public Integer getNumberOfYears() {
        return numberOfYears;
    }
}
