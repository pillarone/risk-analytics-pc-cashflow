package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.components.AbstractResource
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ExternalValuesExtendedResource extends AbstractResource {

    ConstrainedMultiDimensionalParameter parmValueTable = new ConstrainedMultiDimensionalParameter(
        [[], [], []], ['iteration', 'date', 'value'],
        ConstraintsFactory.getConstraints(IntDateTimeDoubleConstraints.IDENTIFIER))

    boolean defaultCalled = false

    @Override
    void useDefault() {
        defaultCalled = true
    }
}
