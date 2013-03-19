package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateHistoricClaimsConstraints implements IMultiDimensionalConstraints {

    private static final String CONTRACT_PERIOD = "Contract Period"
    private static final String REPORTED_AMOUNT = "Reported Amount"
    private static final String PAID_AMOUNT = "Paid Amount"
    private static final String REPORT_DATE = "Report Date";

    public static final String IDENTIFIER = "AGGREGATE_HISTORIC_CLAIMS"
    public static final List COLUMN_HEADERS = [CONTRACT_PERIOD, REPORTED_AMOUNT, PAID_AMOUNT, REPORT_DATE]
    public static final int CONTRACT_PERIOD_INDEX = 0
    public static final int REPORTED_AMOUNT_INDEX = 1
    public static final int PAID_AMOUNT_INDEX = 2
    public static final int REPORT_DATE_INDEX = 3

    static ConstrainedMultiDimensionalParameter getDefault() {
        return new ConstrainedMultiDimensionalParameter(
                [[],[],[],[]],
//            example default entry below
//            [[1], [0d], [0d], [new DateTime(2010,1,1,0,0,0,0)]],
                COLUMN_HEADERS,
                ConstraintsFactory.getConstraints(IDENTIFIER));
    }

    boolean matches(int row, int column, Object value) {
        switch (column) {
            case CONTRACT_PERIOD_INDEX:
                return value instanceof Integer
            case REPORTED_AMOUNT_INDEX:
            case PAID_AMOUNT_INDEX:
                return value instanceof Number
            case REPORT_DATE_INDEX:
                return value instanceof DateTime
        }
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return [Integer,
                Double,
                Double,
                DateTime,
        ].get(column)
    }

    Integer getColumnIndex(Class marker) {
        return null
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false;
    }
}
