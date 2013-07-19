package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ExposureBase {

    ABSOLUTE {
        @Override
        public FrequencyBase convert() {
            return FrequencyBase.ABSOLUTE;
        }
    },
    PREMIUM_WRITTEN {
        @Override
        public FrequencyBase convert() {
            return FrequencyBase.PREMIUM_WRITTEN;
        }
    },
    NUMBER_OF_POLICIES {
        @Override
        public FrequencyBase convert() {
            return FrequencyBase.NUMBER_OF_POLICIES;
        }
    },
    SUM_INSURED {
        @Override
        public FrequencyBase convert() {
            return FrequencyBase.SUM_INSURED;
        }
    };

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }

    public abstract FrequencyBase convert();
}
