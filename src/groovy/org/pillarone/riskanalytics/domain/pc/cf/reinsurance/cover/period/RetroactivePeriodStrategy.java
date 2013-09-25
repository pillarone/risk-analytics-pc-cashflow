package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RetroactivePeriodStrategy extends AbstractPeriodStrategy {

    private DateTime coveredOccurencePeriodFrom;
    private DateTime coveredOccurencePeriodTo;
    private DateTime coveredDevelopmentPeriodStartDate;

    public IParameterObjectClassifier getType() {
        return PeriodStrategyType.RETROACTIVE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("coveredOccurencePeriodFrom", coveredOccurencePeriodFrom);
        parameters.put("coveredOccurencePeriodTo", coveredOccurencePeriodTo);
        parameters.put("coveredDevelopmentPeriodStartDate", coveredDevelopmentPeriodStartDate);
        return parameters;
    }

    public DateTime getStartCover() {
        return coveredOccurencePeriodFrom;
    }

    public DateTime getEndCover() {
        return coveredOccurencePeriodTo.plusDays(1); // in order to have an interval open on the right side
    }

    public int getNumberOfPeriods() {
        return new Period(coveredOccurencePeriodFrom, coveredOccurencePeriodTo).getYears();
    }

    public List<DateTime> getDates() {
        List<DateTime> dates = new ArrayList<DateTime>();
        dates.add(coveredOccurencePeriodFrom);
        for (int i = 1; i <= getNumberOfPeriods(); i++) {
            dates.add(coveredOccurencePeriodFrom.plus(Period.years(i)));
        }
        return dates;
    }

    public boolean isCovered(ClaimCashflowPacket claim) {
        return super.isCovered(claim.getOccurrenceDate()) && !(claim.getUpdateDate().isBefore(coveredDevelopmentPeriodStartDate));
    }

    public boolean isCoverStartsInPeriod(PeriodScope periodScope) {
        DateTime periodStart = periodScope.getCurrentPeriodStartDate();
        DateTime periodEnd = periodScope.getNextPeriodStartDate();
        return !coveredDevelopmentPeriodStartDate.isBefore(periodStart) && coveredDevelopmentPeriodStartDate.isBefore(periodEnd);
    }
}
