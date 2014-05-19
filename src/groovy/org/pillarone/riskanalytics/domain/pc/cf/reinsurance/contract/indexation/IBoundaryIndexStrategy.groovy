package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IBoundaryIndexStrategy extends IParameterObject {

    ConstrainedMultiDimensionalParameter getIndex()

}
