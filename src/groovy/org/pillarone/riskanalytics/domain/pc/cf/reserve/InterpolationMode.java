package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public enum InterpolationMode {
    NONE, LINEAR;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
