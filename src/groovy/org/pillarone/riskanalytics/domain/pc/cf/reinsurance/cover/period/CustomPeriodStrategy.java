package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CustomPeriodStrategy extends AbstractPeriodStrategy {

    public static final String STARTDATE = "Start Date";
    public static final String ENDDATE = "End Date";

    private ConstrainedMultiDimensionalParameter periods;

    public IParameterObjectClassifier getType() {
        return PeriodStrategyType.CUSTOM;
    }

    public Map getParameters() {
        Map<String, ConstrainedMultiDimensionalParameter> parameters = new HashMap<String, ConstrainedMultiDimensionalParameter>(1);
        parameters.put(PERIODS, periods);
        return parameters;
    }

    public DateTime getStartCover() {
        int startDateColumnIndex = periods.getColumnIndex(STARTDATE);
        return (DateTime) periods.getValueAt(1, startDateColumnIndex);
    }

    public DateTime getEndCover() {
        int endDateColumnIndex = periods.getColumnIndex(ENDDATE);
        DateTime endOfLastPeriod = (DateTime) periods.getValueAt(periods.getRowCount() - 1, endDateColumnIndex);
        return endOfLastPeriod.plusDays(1); // in order to have an interval open on the right side
    }

    public int getNumberOfPeriods() {
        return periods.getValueRowCount();
    }

    public List<DateTime> getDates() {
        List<DateTime> dates = new ArrayList<DateTime>();
        int startDateColumnIndex = periods.getColumnIndex(STARTDATE);
        for (int row = periods.getTitleRowCount(); row <periods.getRowCount(); row++) {
            dates.add((DateTime) periods.getValueAt(row, startDateColumnIndex));
        }
        dates.add(getEndCover());
        return dates;
    }

    private static final String PERIODS = "periods";
}
