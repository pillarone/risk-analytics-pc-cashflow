package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ExposureBase {

    ABSOLUTE, PREMIUM_WRITTEN, NUMBER_OF_POLICIES, SUM_INSURED;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
