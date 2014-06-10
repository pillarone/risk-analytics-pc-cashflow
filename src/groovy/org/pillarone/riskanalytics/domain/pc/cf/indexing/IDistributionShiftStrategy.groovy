package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
interface IDistributionShiftStrategy extends IParameterObject {

    Double shift();

}
