package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternTableConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "PATTERN";
    public static final String MONTHS = "Months";

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
