package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum IncludeType {
    INCLUDED, NOTINCLUDED, ONLY;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
