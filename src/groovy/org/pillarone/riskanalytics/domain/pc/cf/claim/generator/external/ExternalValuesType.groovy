package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.components.ResourceHolder
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external.PeriodApplication
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ExternalValuesType extends AbstractParameterObjectClassifier {

    public static final ExternalValuesType BY_ITERATION_AND_DATE = new ExternalValuesType("by iteration and date", "BY_ITERATION_AND_DATE", [
        valueDateTable: new ConstrainedMultiDimensionalParameter([[0], [], [0d]], ["iteration", "date", "value"],
            ConstraintsFactory.getConstraints(IntDateTimeDoubleConstraints.IDENTIFIER))])
    public static final ExternalValuesType BY_ITERATION = new ExternalValuesType("by iteration", "BY_ITERATION", [
        valueTable: new ConstrainedMultiDimensionalParameter([[0], [0d]], ["iteration", "value"],
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
        usage: PeriodApplication.FIRSTPERIOD])
    public static final ExternalValuesType BY_ITERATION_RESOURCE = new ExternalValuesType('by iteration (resource)', 'BY_ITERATION_RESOURCE', [
        referencedIterationData : new ResourceHolder<ExternalValuesResource>(ExternalValuesResource)])
    public static final ExternalValuesType BY_ITERATION_AND_DATE_RESOURCE = new ExternalValuesType('by iteration and date (resource)', 'BY_ITERATION_AND_DATE_RESOURCE', [
        referencedIterationDateData : new ResourceHolder<ExternalValuesExtendedResource>(ExternalValuesExtendedResource)])

    public static final all = [BY_ITERATION_AND_DATE, BY_ITERATION, BY_ITERATION_RESOURCE, BY_ITERATION_AND_DATE_RESOURCE]

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
                    valueDateTable: (ConstrainedMultiDimensionalParameter) parameters.get("valueDateTable"))
            case ExternalValuesType.BY_ITERATION:
                return new ExternalValuesByIterationStrategy(
                    valueTable: (ConstrainedMultiDimensionalParameter) parameters.get("valueTable"),
                    usage:  parameters.get("usage"))
            case ExternalValuesType.BY_ITERATION_RESOURCE:
                return new ResourceExternalValuesByIterationStrategy(
                    referencedIterationData: (ResourceHolder<ExternalValuesResource>) parameters.get('referencedIterationData'))
            case ExternalValuesType.BY_ITERATION_AND_DATE_RESOURCE:
                return new ResourceExternalValuesByIterationAndDateStrategy(
                    referencedIterationDateData: (ResourceHolder<ExternalValuesExtendedResource>) parameters.get('referencedIterationDateData'))
            default:
                throw new NotImplementedException(type.toString())
        }
    }
}
