package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Map;

/**
 * Used for commission and limit/deductible updating.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum BasedOnClaimProperty {
    ULTIMATE, REPORTED, PAID;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
