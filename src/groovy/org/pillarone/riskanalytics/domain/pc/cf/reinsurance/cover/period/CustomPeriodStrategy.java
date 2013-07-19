package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CustomPeriodStrategy extends AbstractPeriodStrategy {

    public static final String STARTDATE = "Start Date";
    public static final String ENDDATE = "End Date";

    private ConstrainedMultiDimensionalParameter periods;

    /** key: start date, value: end date */
    private TreeMap<DateTime, DateTime> coverMap;
    private Set<Integer> coveredPeriods;

    public IParameterObjectClassifier getType() {
        return PeriodStrategyType.CUSTOM;
    }

    public Map getParameters() {
        Map<String, ConstrainedMultiDimensionalParameter> parameters = new HashMap<String, ConstrainedMultiDimensionalParameter>(1);
        parameters.put(PERIODS, periods);
        return parameters;
    }

    @Override
    public boolean isCovered(DateTime date) {
        initCoverMap();
        Map.Entry<DateTime, DateTime> floorEntry = coverMap.floorEntry(date);
        return floorEntry != null && date.isBefore(floorEntry.getValue());
    }

    @Override
    public boolean currentPeriodContainsCover(IPeriodCounter periodCounter) {
        initCoverPeriods(periodCounter);
        return coveredPeriods.contains(periodCounter.currentPeriodIndex());
    }

    public DateTime getStartCover() {
        int startDateColumnIndex = periods.getColumnIndex(STARTDATE);
        return (DateTime) periods.getValueAt(1, startDateColumnIndex);
    }

    public DateTime getEndCover() {
        int endDateColumnIndex = periods.getColumnIndex(ENDDATE);
        DateTime endOfLastPeriod = (DateTime) periods.getValueAt(periods.getRowCount() - 1, endDateColumnIndex);
        // todo: make p14n consistent
        return endOfLastPeriod.plusDays(1); // in order to have an interval open on the right side
    }

    public int getNumberOfPeriods() {
        return periods.getValueRowCount();
    }

    private static final String PERIODS = "periods";

    private void initCoverMap() {
        if (coverMap == null) {
            coverMap = new TreeMap<DateTime, DateTime>();
            int startDateColumnIndex = periods.getColumnIndex(STARTDATE);
            int endDateColumnIndex = periods.getColumnIndex(ENDDATE);
            for (int row = periods.getTitleRowCount(); row < periods.getRowCount(); row++) {
                coverMap.put((DateTime) periods.getValueAt(row, startDateColumnIndex), (DateTime) periods.getValueAt(row, endDateColumnIndex));
            }
        }
    }

    @Override
    public List<DateTime> getDates() {
        initCoverMap();
        List<DateTime> aList = Lists.newArrayList();
        aList.addAll(coverMap.keySet());
        aList.add(coverMap.lastEntry().getValue());
        return aList;
    }

    private void initCoverPeriods(IPeriodCounter periodCounter) {
        if (coveredPeriods == null) {
            coveredPeriods = new HashSet<Integer>();
            int startDateColumnIndex = periods.getColumnIndex(STARTDATE);
            int endDateColumnIndex = periods.getColumnIndex(ENDDATE);
            for (int row = periods.getTitleRowCount(); row < periods.getRowCount(); row++) {
                DateTime startDate = (DateTime) periods.getValueAt(row, startDateColumnIndex);
                DateTime endDate = (DateTime) periods.getValueAt(row, endDateColumnIndex);
                int startPeriod = periodCounter.belongsToPeriod(startDate);
                int endPeriod = periodCounter.belongsToPeriod(endDate);
                for (int period = startPeriod; period <= endPeriod; period++) {
                    coveredPeriods.add(period);
                }
            }
        }
    }
}
