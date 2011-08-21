package org.pillarone.riskanalytics.domain.pc.cf.global;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IProjectionPeriodStrategy extends IParameterObject {

    int projectionPeriods();
    boolean periodNumberRestricted();
}
