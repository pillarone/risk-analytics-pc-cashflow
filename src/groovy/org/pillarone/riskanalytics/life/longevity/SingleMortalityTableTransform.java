package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.Map;

/**
 * author simon.parten @ art - allianz . com
 */
public class SingleMortalityTableTransform implements IMortalityTable {

    final Map<AgeYearKey, IMortalityTableEntry> cacheTable;
    final IMortalityTable startTable;
    final String name;


    public SingleMortalityTableTransform(IMortalityTable startTable, String name) {
        this.startTable = startTable;
        this.name = name;
        cacheTable = Maps.newHashMap();
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        double startYear = 2010d;
        if (cacheTable.containsKey(new AgeYearKey(age, year))) {
            return cacheTable.get(new AgeYearKey(age, year));
        }
        if (year == startYear) {
            throw new SimulationException("");
        }
        if (year == 2011d) {
            double mortalityRate = startTable.getMortalityObject(age, year).mortalityRate();
            double newRate = Math.sqrt(1 - mortalityRate);
            IMortalityTableEntry newEntry = new MortalityTableEntry(age, year, newRate);
            cacheTable.put(newEntry.getAgeYearKey(), newEntry);
            return newEntry;
        }

        IMortalityTableEntry priorYear = this.getMortalityObject(age, year - 1);

//        IMortalityTableEntry simulatedQxThisYear = startTable.getMortalityObject(age, year);
//        IMortalityTableEntry simulatedQxLastYear = startTable.getMortalityObject( age - 1, year - 1);
        double ageNextYear = age + (year - startYear - 1);

        IMortalityTableEntry simulatedQxThisYear = startTable.getMortalityObject(Math.min(ageNextYear - 1, 120), year - 1);
        IMortalityTableEntry simulatedQxNexttYear = startTable.getMortalityObject(Math.min(ageNextYear, 120), year);

        if (simulatedQxNexttYear == null) {
            throw new SimulationException("");
        }
        if (simulatedQxThisYear == null) {
            throw new SimulationException("");
        }

        double survivalProbLast = Math.sqrt(1 - simulatedQxNexttYear.mortalityRate());
        double survivalProbThis = Math.sqrt(1 - simulatedQxThisYear.mortalityRate());
        double survialProbability = survivalProbLast * survivalProbThis * priorYear.mortalityRate();
        IMortalityTableEntry entry = new MortalityTableEntry(age, year, survialProbability);
        cacheTable.put(entry.getAgeYearKey(), entry);

        return entry;
    }

    @Override
    public String toString() {
        return "Name :" + name + super.toString();
    }
}
