package org.pillarone.riskanalytics.life.longevity;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

/**
 * author simon.parten @ art-allianz . com
 */
public class SafeActualMortalityTable extends AbstractMortalityTable {
    public SafeActualMortalityTable(ConstrainedMultiDimensionalParameter guiTable, Double year, String name) {
        super(guiTable, year, name);
    }

    @Override
    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        IMortalityTableEntry entry = super.getMortalityObject(age, year);
        if(entry == null) {
            throw new SimulationException("Couldn't find actual mortality entry. Table :" + name + "  Age: " + age + "  , Year : " + year);
        }
        return entry;
    }
}
