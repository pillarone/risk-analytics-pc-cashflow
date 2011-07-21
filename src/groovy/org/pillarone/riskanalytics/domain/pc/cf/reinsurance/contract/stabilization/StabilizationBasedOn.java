package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum StabilizationBasedOn {

    PAID, REPORTED;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }

    public boolean isPaid() {
        return equals(PAID);
    }

    public boolean isReported() {
        return equals(REPORTED);
    }
}
