package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    public static final String IDENTIFIER = "REINSURANCE_CONTRACT_INDEX_SELECTION";

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(IReinsuranceContractIndexMarker.class, IndexMode.class, BaseDateMode.class, DateTime.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (IReinsuranceContractIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        return super.getColumnIndex(marker);
    }
}
