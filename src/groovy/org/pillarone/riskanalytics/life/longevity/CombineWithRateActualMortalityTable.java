package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class CombineWithRateActualMortalityTable implements IMortalityTable, IIndexedMortalityTable {

    final Map<AgeYearKey, IMortalityTableEntry> cacheTable;
    final IMortalityTable rates2010;
    final IMortalityTable actual;
    final FixedMortalityTable indexRates;
    final String name;


    public CombineWithRateActualMortalityTable(IMortalityTable actual, IMortalityTable rates2010, String name) {
        this.rates2010 = rates2010;
        this.actual = actual;
        this.name = name;
        cacheTable = Maps.newHashMap();
        indexRates = new FixedMortalityTable(name);
    }

    public void addIndexValue(Double year, Double rate){
        indexRates.addIndexValue(year, rate);
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        if(year == 2010d) {
            return rates2010.getMortalityObject(age - 0.5, year);
        }
        if(age >= 99) {
            return new MortalityTableEntry(age, year, 0.8d);
        }
        if(actual.getMortalityObject(age, year) == null) {
            IMortalityTableEntry priorYearEntry = this.getMortalityObject(age, year - 1);
            Double mortalityRate = priorYearEntry.mortalityRate() * (1 - (indexRates.getMortalityObject(age, year).mortalityRate() - 1));
            MortalityTableEntry newEntry = new MortalityTableEntry(age, year, mortalityRate);
            cacheTable.put(newEntry.ageYearKey, newEntry);
            return newEntry;
        }
        return actual.getMortalityObject(age, year);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name : " + name + ". " + super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
