package org.pillarone.riskanalytics.domain.pc.cf.exposure;


import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;
import org.pillarone.riskanalytics.domain.utils.constraint.IUnityPortion;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class UnderwritingPortion implements IMultiDimensionalConstraints, IUnityPortion {

    public static final String IDENTIFIER = "UNDERWRITING_PORTION";
    public static int UNDERWRITING_COLUMN_INDEX = 0;
    public static int PORTION_COLUMN_INDEX = 1;

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
        return column == 0 ? IUnderwritingInfoMarker.class : Double.class;
    }

    public int getPortionColumnIndex() {
        return PORTION_COLUMN_INDEX;
    }

    public int getComponentNameColumnIndex() {
        return UNDERWRITING_COLUMN_INDEX;
    }

    public Integer getColumnIndex(Class marker) {
        if (IUnderwritingInfoMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        else if (Double.class.isAssignableFrom(marker)) {
            return 1;
        }
        return null;
    }
}
