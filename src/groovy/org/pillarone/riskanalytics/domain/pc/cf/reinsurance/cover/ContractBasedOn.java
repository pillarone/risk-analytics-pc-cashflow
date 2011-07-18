package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractBasedOn implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "CONTRACT_BASEDON";

    public static final String CONTRACT = "Covered Contracts";
    public static final String BASED_ON = "Based On";

    public static final int CONTRACT_COLUMN_INDEX = 0;
    public static final int BASED_ON_COLUMN_INDEX = 1;

    public boolean matches(int row, int column, Object value) {
        return value instanceof String;
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return column == 0 ? IReinsuranceContractMarker.class : ContractBase.class;
    }

    public Integer getColumnIndex(Class marker) {
        if (IReinsuranceContractMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        return null;
    }
}
