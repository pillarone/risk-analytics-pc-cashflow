package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AnnualIndexTableConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "INDEX";
    public static final String DATE = "Date";
    public static final String CHANGE = "Change";

    public boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof DateTime;
        }
        else {
            return value instanceof Number;
        }
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return column == 0 ? DateTime.class : Double.class;
    }

    public Integer getColumnIndex(Class marker) {
        return null;
    }
}
