package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
*   author simon.parten @ art-allianz . com
 */
public class ContractConstraint implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "CONTRACT_CONSTRAINT"

    public static final String CONTRACT = "Covered structures";

    public static final int CONTRACT_COLUMN_INDEX = 0;

    boolean matches(int row, int column, Object value) {
        return value instanceof String
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        if(column == 0) {
            return IReinsuranceContractMarker
        }
        throw new IllegalArgumentException("Column number ;" + column + "greater than number of allowed columns in table")
    }

    Integer getColumnIndex(Class marker) {
        if (IReinsuranceContractMarker.isAssignableFrom(marker)) {
            return 0
        }
        return null;
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false
    }
}
