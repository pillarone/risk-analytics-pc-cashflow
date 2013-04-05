package org.pillarone.riskanalytics.life.longevity;

/**
 * author simon.parten @ art-allianz . com
 */
public class MortalityTableEntry extends AbstractMortalityEntry {

    public MortalityTableEntry(double age, double year, double mortalityRate) {
        super(age, year, mortalityRate);
    }

    public MortalityTableEntry(AgeYearKey ageYearKey, double mortalityRate) {
        super(ageYearKey, mortalityRate);
    }

}
