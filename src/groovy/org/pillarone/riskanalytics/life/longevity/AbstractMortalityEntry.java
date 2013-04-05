package org.pillarone.riskanalytics.life.longevity;

/**
 * author simon.parten @ art-allianz . com
 */
public class AbstractMortalityEntry implements IMortalityTableEntry {

    final AgeYearKey ageYearKey;
    final double mortalityRate;

    public AbstractMortalityEntry(double age, double year, double mortalityRate) {
        this.mortalityRate = mortalityRate;
        ageYearKey = new AgeYearKey(age, year);
    }

    public AbstractMortalityEntry(AgeYearKey ageYearKey, double mortalityRate) {
        this.ageYearKey = ageYearKey;
        this.mortalityRate = mortalityRate;
    }

    public double mortalityRate() {
        return mortalityRate;
    }

    public double getAge() {
        return ageYearKey.getAge();
    }

    public double getYear() {
        return ageYearKey.getYear();
    }

    public AgeYearKey getAgeYearKey() {
        return ageYearKey;
    }

    @Override
    public String toString() {
        return "mortalityRate=" + mortalityRate +
                ",{ageYearKey=" + ageYearKey +
                '}';
    }
}
