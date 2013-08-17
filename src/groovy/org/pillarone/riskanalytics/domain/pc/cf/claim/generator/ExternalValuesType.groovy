package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ExternalValuesType extends AbstractParameterObjectClassifier {

public static final ExternalValuesType BY_ITERATION_AND_DATE = new ExternalValuesType("by iteration and date", "BY_ITERATION_AND_DATE", [
        valueTableExtended: new ConstrainedMultiDimensionalParameter([[0], [], [0d]], ["iteration", "date", "value"],
            ConstraintsFactory.getConstraints(IntDateTimeDoubleConstraints.IDENTIFIER))])
    public static final ExternalValuesType BY_ITERATION = new ExternalValuesType("by iteration", "BY_ITERATION", [
        valueTable: new ConstrainedMultiDimensionalParameter([[0], [0d]], ["iteration", "value"],
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
        usage: PeriodApplication.FIRSTPERIOD])

    public static final all = [BY_ITERATION_AND_DATE, BY_ITERATION]

    protected static Map types = [:]
    static {
        ExternalValuesType.all.each {
            ExternalValuesType.types[it.toString()] = it
        }
    }

    private ExternalValuesType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ExternalValuesType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IExternalValuesStrategy getDefault() {
        return new ExternalValuesByIterationStrategy(
            valueTable: new ConstrainedMultiDimensionalParameter([[0], [0d]], ["iteration", "value"],
                ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
            usage: PeriodApplication.ALLPERIODS)
    }

    static IExternalValuesStrategy getStrategy(ExternalValuesType type, Map parameters) {
        switch (type) {
            case ExternalValuesType.BY_ITERATION_AND_DATE:
                return new ExternalValuesByIterationAndDateStrategy(
                    valueTableExtended: (ConstrainedMultiDimensionalParameter) parameters.get("valueTableExtended"))
            case ExternalValuesType.BY_ITERATION:
                return new ExternalValuesByIterationStrategy(
                    valueTable: (ConstrainedMultiDimensionalParameter) parameters.get("valueTable"),
                    usage:  parameters.get("usage"))
            default:
                throw new NotImplementedException(type.toString())
        }
    }
}
