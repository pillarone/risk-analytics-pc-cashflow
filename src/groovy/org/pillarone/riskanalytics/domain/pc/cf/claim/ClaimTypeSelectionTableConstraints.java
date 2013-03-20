package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.pillarone.riskanalytics.domain.pc.cf.indexing.IReservesIndexMarker;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexSelectionTableConstraints;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimTypeSelectionTableConstraints extends IndexSelectionTableConstraints {

    public static final String IDENTIFIER = "CLAIM_TYPE_SELECTION";
    public static final String TYPE = "Claim Type";

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(ClaimType.class).get(column);
    }

    public static final List<String> COLUMN_TITLES = Arrays.asList(
            ClaimTypeSelectionTableConstraints.TYPE);

    public boolean matches(int row, int column, Object value) {
        return value instanceof String;
    }

    public Integer getColumnIndex(Class marker) {
        if (ClaimType.class.isAssignableFrom(marker)) {
            return 0;
        }
        return null;
    }

}
