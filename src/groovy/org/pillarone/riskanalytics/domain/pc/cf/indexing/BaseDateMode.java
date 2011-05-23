package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum BaseDateMode {
    DATE_OF_LOSS, START_OF_PROJECTION, FIXED_DATE, DAY_BEFORE_FIRST_PERIOD;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
