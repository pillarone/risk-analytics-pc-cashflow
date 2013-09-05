package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.components.AbstractResource
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ExternalValuesResource extends AbstractResource {

    ConstrainedMultiDimensionalParameter parmValueTable = new ConstrainedMultiDimensionalParameter(
        [[], []], ['iteration', 'value'],
        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
    PeriodApplication parmUsage = PeriodApplication.FIRSTPERIOD

    boolean defaultCalled = false;

    @Override
    void useDefault() {
        defaultCalled = true;
    }
}
