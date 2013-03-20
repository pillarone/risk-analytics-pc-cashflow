package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReservesIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    public static final String IDENTIFIER = "RESERVE_INDEX_SELECTION";

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(IReservesIndexMarker.class, IndexMode.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (IReservesIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        return super.getColumnIndex(marker);
    }

    public static final List<String> COLUMN_TITLES = Arrays.asList(
            IndexSelectionTableConstraints.INDEX,
            IndexSelectionTableConstraints.MODE);

    public boolean matches(int row, int column, Object value) {
        return value instanceof String;
    }


}
