package org.pillarone.riskanalytics.life.longevity;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

/**
 * author simon.parten @ art-allianz . com
 */
public class ActualMortalityTable extends AbstractMortalityTable {
    public ActualMortalityTable(ConstrainedMultiDimensionalParameter guiTable, Double year, String name) {
        super(guiTable, year, name);
    }
}
