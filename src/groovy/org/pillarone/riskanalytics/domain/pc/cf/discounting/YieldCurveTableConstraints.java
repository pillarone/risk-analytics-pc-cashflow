package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class YieldCurveTableConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "YIELDCURVE";
    public static final String MONTHS = "Months";
    public static final String RATE = "Rate";
    public static final List<String> COLUMN_TITLES = Arrays.asList(MONTHS, RATE);

    public boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof Integer;
        }
        else {
            return value instanceof Number;
        }
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return column == 0 ? Integer.class : Double.class;
    }

    public Integer getColumnIndex(Class marker) {
        return null;
    }

    public boolean emptyComponentSelectionAllowed(int column) {
        return false;
    }
}

