package org.pillarone.riskanalytics.domain.pc.cf.legalentity;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntityPortionConstraints implements IMultiDimensionalConstraints, IUnityPortion {

    public static final String IDENTIFIER = "LEGAL_ENTITY_PORTION";

    public static int COMPANY_COLUMN_INDEX = 0;
    public static int PORTION_COLUMN_INDEX = 1;

    public static final String REINSURER = "Reinsurer";
    public static final String PORTION = "Covered Portion";

    public static final List<String> COLUMN_TITLES = Arrays.asList(REINSURER, PORTION);


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
        return column == 0 ? ILegalEntityMarker.class : Double.class;
    }

    public int getPortionColumnIndex() {
        return PORTION_COLUMN_INDEX;
    }

    public int getComponentNameColumnIndex() {
        return COMPANY_COLUMN_INDEX;
    }

    public Integer getColumnIndex(Class marker) {
        if (ILegalEntityMarker.class.isAssignableFrom(marker)) {
            return COMPANY_COLUMN_INDEX;
        }
        else if (ILegalEntityMarker.class.isAssignableFrom(marker)) {
            return PORTION_COLUMN_INDEX;
        }
        return null;
    }

}
