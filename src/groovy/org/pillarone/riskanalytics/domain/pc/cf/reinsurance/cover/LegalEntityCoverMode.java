package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum LegalEntityCoverMode {
    INWARD, INWARDANDORIGINALCLAIMS, ORIGINALCLAIMS;

     public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
