package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class DistributionShifted extends AbstractParameterObject implements IDistributionShiftStrategy {

    Double shift

    DistributionShiftType getType() {
        DistributionShiftType.SHIFT
    }

    Double shift() {
        shift
    }

    Map getParameters() {
        [(StochasticIndexStrategy.SHIFT.toString()) : shift]
    }

}
