package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class CombineMortalityOneMinusProbTable implements IMortalityTable {

    final Map<AgeYearKey, IMortalityTableEntry> cacheTable;
    final IMortalityTable startTable;
    final IMortalityTable allYearTable;


    public CombineMortalityOneMinusProbTable(IMortalityTable startTable, IMortalityTable allYearTable) {
        this.startTable = startTable;
        this.allYearTable = allYearTable;
        cacheTable = Maps.newHashMap();
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        if (year == 2010d) {
            IMortalityTableEntry firstTableEntry = startTable.getMortalityObject(age, 2010d);
            return firstTableEntry;
        }
        if(cacheTable.containsKey(new AgeYearKey(age, year))) {
            return cacheTable.get(new AgeYearKey(age, year));
        }
        IMortalityTableEntry priorYearEntry = startTable.getMortalityObject(age, year - 1);
        IMortalityTableEntry mortalityTableEntry = allYearTable.getMortalityObject(age, year);
        if(mortalityTableEntry == null) {
            throw new SimulationException("Entry not found in table. Age; " + age + "... Year : "+ year);
        }
        IMortalityTableEntry combinedEntry = new MortalityTableEntry(age, year, priorYearEntry.mortalityRate() * (1 - mortalityTableEntry.mortalityRate()));
        cacheTable.put(combinedEntry.getAgeYearKey(), combinedEntry);
        return combinedEntry;
    }
}
