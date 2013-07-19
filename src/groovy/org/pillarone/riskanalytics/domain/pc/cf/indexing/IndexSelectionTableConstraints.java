package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IndexSelectionTableConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "INDEX_SELECTION";
    public static final String INDEX = "Index";
    public static final String MODE = "Index Mode";
    public static final String BASEDATEMODE = "Base Date Mode";
    public static final String DATE = "Date";


    public static final List<String> COLUMN_TITLES = Arrays.asList(
            IndexSelectionTableConstraints.INDEX,
            IndexSelectionTableConstraints.MODE,
            IndexSelectionTableConstraints.BASEDATEMODE,
            IndexSelectionTableConstraints.DATE);

    public boolean matches(int row, int column, Object value) {
        return column  < 3 ? value instanceof String : value instanceof DateTime;
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(IIndexMarker.class, IndexMode.class, BaseDateMode.class, DateTime.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (IIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        }
        else if (IndexMode.class.isAssignableFrom(marker)) {
            return 1;
        }
        else if (BaseDateMode.class.isAssignableFrom(marker)) {
            return 2;
        }
        else if (DateTime.class.isAssignableFrom(marker)) {
            return 3;
        }
        return null;
    }

    public boolean emptyComponentSelectionAllowed(int column) {
        return false;
    }


}
