package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation;

import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public enum RiskBandAllocationBaseLimited {
    NUMBER_OF_POLICIES, PREMIUM;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
