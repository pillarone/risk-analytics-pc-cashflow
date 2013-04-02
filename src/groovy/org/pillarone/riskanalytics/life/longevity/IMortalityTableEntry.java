package org.pillarone.riskanalytics.life.longevity;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IMortalityTableEntry {

    public double mortalityRate();

    public double getAge();

    public double getYear();

    public AgeYearKey getAgeYearKey();

}
