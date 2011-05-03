package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PremiumIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    public static final String IDENTIFIER = "PREMIUM_INDEX_SELECTION";

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(IPremiumIndexMarker.class, IndexMode.class, BaseDateMode.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (IPremiumIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        } else if (IndexMode.class.isAssignableFrom(marker)) {
            return 1;
        } else if (BaseDateMode.class.isAssignableFrom(marker)) {
            return 2;
        }
        return null;
    }
}
