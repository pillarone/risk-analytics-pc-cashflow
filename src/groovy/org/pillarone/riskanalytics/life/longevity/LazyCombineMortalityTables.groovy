package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class LazyCombineMortalityTables implements IMortalityTable {

    final Map<AgeYearKey, IMortalityTableEntry> cacheTable;

    final IMortalityTable table1;
    final Closure table1AgeTransform
    final Closure table1YearTransform

    final IMortalityTable table2;
    final Closure table2AgeTransform
    final Closure table2YearTransform

    final Closure rateTransform


    public LazyCombineMortalityTables(IMortalityTable table1, Closure table1AgeTransform, Closure table1YearTransform, IMortalityTable table2, Closure table2AgeTransform, Closure table2YearTransform, Closure rateTrasform) {
        cacheTable = Maps.newHashMap();
        this.table1 = table1
        this.table1AgeTransform = table1AgeTransform
        this.table1YearTransform = table1YearTransform
        this.table2 = table2
        this.table2AgeTransform = table2AgeTransform
        this.table2YearTransform = table2YearTransform
        this.rateTransform = rateTrasform
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        if (cacheTable.containsKey(new AgeYearKey(age, year))){
            return  cacheTable.get(new AgeYearKey(age, year))
        }

        Double table1Age = table1AgeTransform() (age)
        Double table1Year = table1YearTransform() (year)
        IMortalityTableEntry table1Entry = table1.getMortalityObject(table1Age, table1Year)

        Double table2Age = table2AgeTransform() (age)
        Double table2Year = table2YearTransform() (year)
        IMortalityTableEntry table2Entry = table1.getMortalityObject(table2Age, table2Year)

        Double newRate = rateTransform()(table1Entry.mortalityRate(), table2Entry.mortalityRate() )

        IMortalityTableEntry entry = new MortalityTableEntry(age, year, newRate)
        cacheTable.put(entry.getAgeYearKey(), entry)
        return entry

    }
}
