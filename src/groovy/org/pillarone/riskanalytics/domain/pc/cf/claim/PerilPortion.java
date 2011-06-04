package org.pillarone.riskanalytics.domain.pc.cf.claim;


import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class PerilPortion implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "PERIL_PORTION";

    public boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof String;
        }
        else {
            return value instanceof Number;
        }
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return column == 0 ? IPerilMarker.class : Double.class;
    }

    public Integer getColumnIndex(Class marker) {
        if (IPerilMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        else if (Double.class.isAssignableFrom(marker)) {
            return 1;
        }
        return null;
    }

}
