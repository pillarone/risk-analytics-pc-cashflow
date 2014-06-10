package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class DistributionNotShifted extends AbstractParameterObject implements IDistributionShiftStrategy {

    DistributionShiftType getType() {
        DistributionShiftType.NONE
    }

    Double shift() {
        0d
    }

    Map getParameters() {
        [:]
    }

}
