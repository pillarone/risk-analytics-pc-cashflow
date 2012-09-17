package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
*   author simon.parten @ art-allianz . com
 */
public class ContractBasedOn implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "CONTRACT_BASEDON"

    public static final String CONTRACT = "Covered structures";
    public static final String BASED_ON = "Based On";

    public static final int CONTRACT_COLUMN_INDEX = 0;
    public static final int BASED_ON_COLUMN_INDEX = 1;

    boolean matches(int row, int column, Object value) {
        value instanceof String
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? IReinsuranceContractMarker : ContractBase
    }

    Integer getColumnIndex(Class marker) {
        if (IReinsuranceContractMarker.isAssignableFrom(marker)) {
            return 0
        }
        else if (BigDecimal.isAssignableFrom(marker)) {
            return 1
        }
        return null;
    }
}
