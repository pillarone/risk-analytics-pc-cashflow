package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum UnifiedADCLPTBase {
    ABSOLUTE, OUTSTANDING_PERCENTAGE;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
