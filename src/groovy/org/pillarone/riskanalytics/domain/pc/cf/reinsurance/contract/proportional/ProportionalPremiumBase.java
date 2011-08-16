package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ProportionalPremiumBase {
    CEDED, GNPI, GROSS, NET;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
