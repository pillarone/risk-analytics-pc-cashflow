package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum CommissionBasedOnClaims {
    ULTIMATE, REPORTED, PAID;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
