package org.pillarone.riskanalytics.domain.pc.cf.global;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CompleteRolloutProjectionPeriodStrategy extends AbstractParameterObject implements IProjectionPeriodStrategy {

    public IParameterObjectClassifier getType() {
        return ProjectionPeriodType.COMPLETEROLLOUT;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public int projectionPeriods() {
        return Integer.MAX_VALUE;
    }

    public boolean periodNumberRestricted() {
        return false;
    }
}
