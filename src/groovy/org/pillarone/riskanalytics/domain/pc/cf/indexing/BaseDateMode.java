package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum BaseDateMode {
    START_OF_PROJECTION, DATE_OF_LOSS;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
