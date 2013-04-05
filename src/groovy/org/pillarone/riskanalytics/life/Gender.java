package org.pillarone.riskanalytics.life;

import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException;
import org.pillarone.riskanalytics.life.longevity.IMortalityTable;

/**
 * author simon.parten @ art-allianz . com
 */
public enum Gender {
    MALE {
        @Override
        public double survivalProbability(IMortalityTable maleMortalityTable, IMortalityTable femaleMortality, double memberAge, double currentYear) {
            return maleMortalityTable.getMortalityObject( memberAge, currentYear).mortalityRate();
        }

        @Override
        public String getStringValue() {
            return "MALE";
        }
    }, FEMALE {
        @Override
        public double survivalProbability(IMortalityTable maleMortalityTable, IMortalityTable femaleMortality, double memberAge, double currentYear) {
            return femaleMortality.getMortalityObject( memberAge, currentYear).mortalityRate();
        }

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


    public abstract double survivalProbability(IMortalityTable maleMortalityTable, IMortalityTable femaleMortality, double memberAge, double currentYear);
}
