package org.pillarone.riskanalytics.life.longevity

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * author simon.parten @ art-allianz . com
 */
@CompileStatic
public class IdentityTableStrategy extends AbstractParameterObject implements IMortalityTable {


    public MortalityTableType getType() {
        return MortalityTableType.IDENTITY;
    }

    public Map getParameters() {
        [:]
    }

    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        return new MortalityTableEntry(age, year, 1d);
    }
}
