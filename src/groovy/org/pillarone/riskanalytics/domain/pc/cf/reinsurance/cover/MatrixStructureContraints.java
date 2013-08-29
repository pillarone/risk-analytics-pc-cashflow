package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector;
import org.pillarone.riskanalytics.domain.utils.marker.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MatrixStructureContraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "MATRIX_STRUCTURE";

    public static final String LEGAL_ENTITY = "Legal Entity";
    public static final String SEGMENTS = "Segments";
    public static final String GENERATORS = "Generators";
    public static final String LOSS_KIND_OF = "Kind of Loss";

    public static final int LEGAL_ENTITY_OF_COLUMN_INDEX = 0;
    public static final int SEGMENTS_OF_COLUMN_INDEX = 1;
    public static final int GENERATORS_OF_COLUMN_INDEX = 2;
    public static final int LOSS_KIND_OF_OF_COLUMN_INDEX = 3;

    public boolean matches(int row, int column, Object value) {
        return value instanceof String;
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        if (column == 0) return ILegalEntityMarker.class;
        if (column == 1) return ISegmentMarker.class;
        if (column == 2) return IClaimMarker.class;
        if (column == 3) return ClaimTypeSelector.class;
        return null;
    }

    public Integer getColumnIndex(Class marker) {
        if (ILegalEntityMarker.class.isAssignableFrom(marker)) {
            return LEGAL_ENTITY_OF_COLUMN_INDEX;
        } else if (ISegmentMarker.class.isAssignableFrom(marker)) {
            return SEGMENTS_OF_COLUMN_INDEX;
        } else if (IClaimMarker.class.isAssignableFrom(marker)) {
            return GENERATORS_OF_COLUMN_INDEX;
        }
        return null;
    }

    public boolean emptyComponentSelectionAllowed(int column) {
        return true;
    }
}
