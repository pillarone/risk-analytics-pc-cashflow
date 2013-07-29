package org.pillarone.riskanalytics.life.longevity

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.simulation.SimulationException

/**
 * author simon.parten @ art - allianz . com
 */
@CompileStatic
public class SingleMortalityTableTransformStrategy extends AbstractParameterObject implements IMortalityTable {

    private ConstrainedString startTable = new ConstrainedString(IMortalityTableMarker.class, '')
    private Double startYear = 2011d

    public MortalityTableType getType() {
        return MortalityTableType.REFERENCE;
    }

    public Map getParameters() {
        ['startTable' : startTable, 'startYear' : startYear]
    }



    final Map<AgeYearKey, IMortalityTableEntry> cacheTable;


    public SingleMortalityTableTransformStrategy(IMortalityTable startTable, String name) {
//        this.startTable = startTable;
//        this.name = name;
        cacheTable = [:]
    }

    /** @param startTable death probabilities */
    public SingleMortalityTableTransformStrategy(ConstrainedString startTable, Double startYear) {
        this.startTable = startTable;
        this.startYear = startYear;
        cacheTable = [:]
    }



    /**
     * @param age
     * @param year
     * @return survival probabilities
     */
    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        if (cacheTable.containsKey(new AgeYearKey(age, year))) {
            return cacheTable.get(new AgeYearKey(age, year));
        }
        if (year == startYear) {
            throw new SimulationException("");
        }
        MortalityTable table = startTable.selectedComponent as MortalityTable
        if (year == 2011d) {
            double mortalityRate = table.getMortalityObject(age, year).mortalityRate();
            double newRate = Math.sqrt(1d - mortalityRate);
            IMortalityTableEntry newEntry = new MortalityTableEntry(age, year, newRate);
            cacheTable.put(newEntry.getAgeYearKey(), newEntry);
            return newEntry;
        }

        IMortalityTableEntry priorYear = this.getMortalityObject(age, year - 1d);

//        IMortalityTableEntry simulatedQxThisYear = startTable.getMortalityObject(age, year);
//        IMortalityTableEntry simulatedQxLastYear = startTable.getMortalityObject( age - 1, year - 1);
        double ageNextYear = age + (year - startYear - 1d);

        IMortalityTableEntry simulatedQxThisYear = table.getMortalityObject(Math.min(ageNextYear - 1d, 120d), year - 1);
        IMortalityTableEntry simulatedQxNexttYear = table.getMortalityObject(Math.min(ageNextYear, 120d), year);

        if (simulatedQxNexttYear == null) {
            throw new SimulationException("");
        }
        if (simulatedQxThisYear == null) {
            throw new SimulationException("");
        }

        double survivalProbLast = Math.sqrt(1d - simulatedQxNexttYear.mortalityRate());
        double survivalProbThis = Math.sqrt(1d - simulatedQxThisYear.mortalityRate());
        double survivalProbability = survivalProbLast * survivalProbThis * priorYear.mortalityRate();
        IMortalityTableEntry entry = new MortalityTableEntry(age, year, survivalProbability);
        cacheTable.put(entry.getAgeYearKey(), entry);

        return entry;
    }

}
