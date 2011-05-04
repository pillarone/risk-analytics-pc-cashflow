package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencyIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    public static final String IDENTIFIER = "FREQUENCY_INDEX_SELECTION";

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(IFrequencyIndexMarker.class, IndexMode.class, BaseDateMode.class, DateTime.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (IFrequencyIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        return super.getColumnIndex(marker);
    }
}
