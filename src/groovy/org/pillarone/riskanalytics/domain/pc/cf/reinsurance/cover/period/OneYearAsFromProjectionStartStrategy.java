package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class OneYearAsFromProjectionStartStrategy extends AbstractPeriodStrategy {

    private DateTime startCover;

    public IParameterObjectClassifier getType() {
        return PeriodStrategyType.ONEYEAR;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public DateTime getStartCover() {
        return startCover;
    }

    public DateTime getEndCover() {
        return startCover.plus(Period.years(1));
    }

    public int getNumberOfPeriods() {
        return 1;
    }

    public List<DateTime> getDates() {
        List<DateTime> dates = new ArrayList<DateTime>();
        dates.add(startCover);
        dates.add(getEndCover());
        return dates;
    }

    @Override
    public void initStartCover(DateTime date) {
        startCover = new DateTime(date);
    }
}
