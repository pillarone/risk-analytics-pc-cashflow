package org.pillarone.riskanalytics.life.longevity

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.components.Component

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class MortalityTable extends Component implements IMortalityTableMarker {

    IMortalityTable parmType = MortalityTableType.getStrategy(MortalityTableType.IDENTITY, [:])

    @Override
    protected void doCalculation() {
        // intentionally left void
    }

    @Override
    IMortalityTableEntry getMortalityObject(Double age, Double year) {
        parmType.getMortalityObject(age, year)
    }
}
