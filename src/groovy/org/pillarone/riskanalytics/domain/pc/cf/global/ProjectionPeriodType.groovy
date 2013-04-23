package org.pillarone.riskanalytics.domain.pc.cf.global

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ProjectionPeriodType extends AbstractParameterObjectClassifier {

    public static final String NUMBER_OF_PERIODS = "number of periods"

    public static final ProjectionPeriodType COMPLETEROLLOUT = new ProjectionPeriodType("Complete Roll-out", "COMPLETEROLLOUT",
            [:])
    public static final ProjectionPeriodType PERIODS = new ProjectionPeriodType(
            'Periods', 'PERIODS', ['number of periods': 1])

    public static final all = [COMPLETEROLLOUT, PERIODS]

    protected static Map types = [:]
    static {
        ProjectionPeriodType.all.each {
            ProjectionPeriodType.types[it.toString()] = it
        }
    }

    private ProjectionPeriodType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static ProjectionPeriodType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IProjectionPeriodStrategy getDefault() {
        return new RestrictedProjectionPeriodStrategy(number : 5)
        return new RestrictedProjectionPeriodStrategy(number : 5)
    }

    public static IProjectionPeriodStrategy getStrategy(ProjectionPeriodType type, Map parameters) {
        IProjectionPeriodStrategy strategy;
        switch (type) {
            case ProjectionPeriodType.COMPLETEROLLOUT:
                return new CompleteRolloutProjectionPeriodStrategy()
            case ProjectionPeriodType.PERIODS:
                return new RestrictedProjectionPeriodStrategy(number : parameters[NUMBER_OF_PERIODS])
            default:
                throw new NotImplementedException("ProjectionPeriodType " + type.toString() + " not implemented.");
        }
        return strategy;
    }
}
