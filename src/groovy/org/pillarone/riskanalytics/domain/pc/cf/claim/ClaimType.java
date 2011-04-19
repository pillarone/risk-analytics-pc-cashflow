package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ClaimType {
    ATTRITIONAL, SINGLE, EVENT, AGGREGATED_ATTRITIONAL, AGGREGATED_SINGLE, AGGREGATED_EVENT, RESERVE, AGGREGATED_RESERVES, AGGREGATED;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
