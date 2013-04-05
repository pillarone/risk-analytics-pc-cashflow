package org.pillarone.riskanalytics.life.longevity;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IIndexedMortalityTable extends IMortalityTable {

    public void addIndexValue(Double year, Double rate);

}
