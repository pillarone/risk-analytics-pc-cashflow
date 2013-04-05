package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class CombineMortalityTableWithFirstYearTable implements IMortalityTable {

    final Map<AgeYearKey, IMortalityTableEntry> cacheTable;
    final IMortalityTable startTable;
    final IMortalityTable allYearTable;
    final String name;


    public CombineMortalityTableWithFirstYearTable(IMortalityTable startTable, IMortalityTable allYearTable, String name) {
        this.startTable = startTable;
        this.allYearTable = allYearTable;
        this.name = name;
        cacheTable = Maps.newHashMap();
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        IMortalityTableEntry entry2010 = startTable.getMortalityObject(age, 2010d);
        if (year == 2010d) {
            return entry2010;
        }
//        if(cacheTable.containsKey(new AgeYearKey(age, year))) {
//            return cacheTable.get(new AgeYearKey(age, year));
//        }

        IMortalityTableEntry mortalityTableEntry = allYearTable.getMortalityObject(age, year);
        if(mortalityTableEntry == null) {
            throw new SimulationException("Entry not found in table. Age; " + age + "... Year : "+ year);
        }
        IMortalityTableEntry combinedEntry = new MortalityTableEntry(age, year, entry2010.mortalityRate() * mortalityTableEntry.mortalityRate());
        cacheTable.put(combinedEntry.getAgeYearKey(), combinedEntry);
        return combinedEntry;
    }

    @Override
    public String toString() {
        return "Name :" + name + super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
