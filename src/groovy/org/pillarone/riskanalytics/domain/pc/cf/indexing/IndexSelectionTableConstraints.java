package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): discuss with msp if framework extension to support different marker interfaces within one implementation class only
public class IndexSelectionTableConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "INDEX_SELECTION";
    public static final String INDEX = "Index";
    public static final String MODE = "Mode";
    public static final String BASEDATEMODE = "Base Date Mode";


    public static final List<String> COLUMN_TITLES = Arrays.asList(
            IndexSelectionTableConstraints.INDEX,
            IndexSelectionTableConstraints.MODE,
            IndexSelectionTableConstraints.BASEDATEMODE);

    public boolean matches(int row, int column, Object value) {
        return value instanceof String;
    }

    public String getName() {
        return IDENTIFIER;
    }

    public Class getColumnType(int column) {
        return Arrays.asList(IIndexMarker.class, IndexMode.class, BaseDateMode.class).get(column);
    }

    public Integer getColumnIndex(Class marker) {
        if (IIndexMarker.class.isAssignableFrom(marker)) {
            return 0;
        } else if (IndexMode.class.isAssignableFrom(marker)) {
            return 1;
        } else if (BaseDateMode.class.isAssignableFrom(marker)) {
            return 2;
        }
        return null;
    }
}
