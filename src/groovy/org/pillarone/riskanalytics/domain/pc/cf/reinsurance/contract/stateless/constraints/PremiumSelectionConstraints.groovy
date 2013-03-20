package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.domain.utils.marker.IPremiumInfoMarker;


public class PremiumSelectionConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "PREMIUM_SELECTION";

    public static final int PREMIUM_INDEX = 0
    public static final String PREMIUM_TITLE = "Selected Premium"

    boolean matches(int row, int column, Object value) {
        if(column != 0) {
            throw new IllegalArgumentException("Selection premium only in column")
        }
        return (value instanceof String)
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        if(column != 0) {
            throw new IllegalArgumentException("Selection premium only in column")
        }
        return IPremiumInfoMarker.class
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    Integer getColumnIndex(Class marker) {
        if(marker instanceof IPremiumInfoMarker) {
            return 0
        }
        throw new IllegalArgumentException("Selection premium only in column")
    }
}
