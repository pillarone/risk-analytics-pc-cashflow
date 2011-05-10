package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum StopLossPremiumBase {
    ABSOLUTE, GNPI;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
