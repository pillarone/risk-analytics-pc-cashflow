package org.pillarone.riskanalytics.domain.pc.cf.global;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RestrictedProjectionPeriodStrategy extends AbstractParameterObject implements IProjectionPeriodStrategy {

    Integer number;

    public IParameterObjectClassifier getType() {
        return ProjectionPeriodType.PERIODS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put(ProjectionPeriodType.NUMBER_OF_PERIODS, number);
        return parameters;
    }

    public int projectionPeriods() {
        return number;
    }

    public boolean periodNumberRestricted() {
        return true;
    }
}
