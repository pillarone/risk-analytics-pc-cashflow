package org.pillarone.riskanalytics.life.longevity;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;

/**
 * author simon.parten @ art-allianz . com
 */
public class IdentityTable implements IMortalityTable {

    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        return new MortalityTableEntry(age, year, 1d);
    }
}
