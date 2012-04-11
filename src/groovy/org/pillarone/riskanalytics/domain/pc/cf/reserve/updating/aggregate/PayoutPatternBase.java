package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum PayoutPatternBase {
    CLAIM_OCCURANCE_DATE, PERIOD_START_DATE;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
