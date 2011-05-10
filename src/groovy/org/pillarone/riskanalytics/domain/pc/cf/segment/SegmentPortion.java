package org.pillarone.riskanalytics.domain.pc.cf.segment;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

import java.math.BigDecimal;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public class SegmentPortion implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "SEGMENT_PORTION";

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
        return column == 0 ? ISegmentMarker.class : Double.class;
    }

    public Integer getColumnIndex(Class marker) {
        if (ISegmentMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        else if (BigDecimal.class.isAssignableFrom(marker)) {
            return 1;
        }
        return null;
    }
}