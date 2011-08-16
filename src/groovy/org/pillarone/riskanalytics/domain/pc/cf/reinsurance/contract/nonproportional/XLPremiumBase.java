package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum XLPremiumBase {
    ABSOLUTE, GNPI, RATE_ON_LINE, NUMBER_OF_POLICIES;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
