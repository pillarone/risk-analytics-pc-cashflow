package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SeverityIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    public static final String IDENTIFIER = "SEVERITY_INDEX_SELECTION";

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(ISeverityIndexMarker.class, IndexMode.class, BaseDateMode.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (ISeverityIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        } else if (IndexMode.class.isAssignableFrom(marker)) {
            return 1;
        } else if (BaseDateMode.class.isAssignableFrom(marker)) {
            return 2;
        }
        return null;
    }
}
