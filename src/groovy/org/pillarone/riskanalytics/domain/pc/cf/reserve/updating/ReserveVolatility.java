package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ReserveVolatility {
    NONE, LOW, MEDIUM, HIGH;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
