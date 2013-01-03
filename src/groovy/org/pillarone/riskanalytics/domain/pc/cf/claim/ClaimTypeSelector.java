package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ClaimTypeSelector {
    ANY, ATTRITIONAL, SINGLE, AGGREGATED_EVENT, AGGREGATED_RESERVES;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
