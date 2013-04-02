package org.pillarone.riskanalytics.life.longevity;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IMortalityTable {

    public IMortalityTableEntry getMortalityObject(Double age, Double year);

}
