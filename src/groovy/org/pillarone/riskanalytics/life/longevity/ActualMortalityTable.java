package org.pillarone.riskanalytics.life.longevity;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;

/**
 * author simon.parten @ art-allianz . com
 */
public class ActualMortalityTable extends AbstractMortalityTable {
    public ActualMortalityTable(ConstrainedMultiDimensionalParameter guiTable, Double year) {
        super(guiTable, year);
    }
}
