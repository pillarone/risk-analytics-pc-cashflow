package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class AbstractMortalityTable implements IMortalityTable {

    final Map<AgeYearKey, IMortalityTableEntry> table;

    public AbstractMortalityTable(ConstrainedMultiDimensionalParameter guiTable) {
        List<String> columnHeaders = (List<String>) guiTable.getColumnNames();

        HashMap<AgeYearKey, IMortalityTableEntry> tableEntries = Maps.newHashMap();

        for (int i = 1; i < guiTable.getValueRowCount(); i++) {
            for(int j = 1  ; j < guiTable.getValueColumnCount(); j++) {
                Double year = Double.parseDouble(columnHeaders.get(j));
                Double age = ((Number) guiTable.getValueAt(i, 0)).doubleValue();
                Double rate = ((Number) guiTable.getValueAt(i, j)).doubleValue();
                MortalityTableEntry mortalityTableEntry = new MortalityTableEntry(age, year, rate);
                tableEntries.put(mortalityTableEntry.ageYearKey, mortalityTableEntry);
            }
        }
        this.table = Collections.unmodifiableMap(tableEntries);
    }

    public AbstractMortalityTable(ConstrainedMultiDimensionalParameter guiTable, Double year) {
        HashMap<AgeYearKey, IMortalityTableEntry> tableEntries = Maps.newHashMap();
            for (int i = 1; i <= guiTable.getValueRowCount(); i++) {
                Double age = (Double) guiTable.getValueAt(i, 0);
                Double rate = (Double) guiTable.getValueAt(i, 1);
                MortalityTableEntry mortalityTableEntry = new MortalityTableEntry(age, year, rate);
                tableEntries.put(mortalityTableEntry.ageYearKey, mortalityTableEntry);
        }
        this.table = Collections.unmodifiableMap(tableEntries);
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        return table.get(new AgeYearKey(age, year));
    }
}
