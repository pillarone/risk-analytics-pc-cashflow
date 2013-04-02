package org.pillarone.riskanalytics.life;

import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException;

/**
 * author simon.parten @ art-allianz . com
 */
public enum Gender {
    MALE {
        @Override
        public String getStringValue() {
            return "MALE";
        }
    }, FEMALE {
        @Override
        public String getStringValue() {
            return "FEMALE";
        }
    };

    public abstract String getStringValue();

    public static Gender getStringValue(String string) {
        if(string.equals("MALE") || string.equals("M")) {
            return MALE;
        }
        if(string.equals("FEMALE") || string.equals("F")) {
            return FEMALE;
        }
        throw new RiskAnalyticsInconsistencyException("Unrecopgnised value : {" + string +" } is not recognised as a valid entry for this table.");

    }


}
