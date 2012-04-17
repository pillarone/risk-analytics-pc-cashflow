package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.apache.commons.lang.NotImplementedException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateActualClaimsStrategyType extends AbstractParameterObjectClassifier {

    public static final AggregateActualClaimsStrategyType NONE = new AggregateActualClaimsStrategyType('none', 'NONE', [:])
    public static final AggregateActualClaimsStrategyType AGGREGATE = new AggregateActualClaimsStrategyType('aggregate', 'AGGREGATE',
            [history : AggregateActualClaimsStrategy.getDefaultHistory()])

    public static final all = [NONE, AGGREGATE]

    protected static Map types = [:]
    static {
        AggregateActualClaimsStrategyType.all.each {
            AggregateActualClaimsStrategyType.types[it.toString()] = it
        }
    }

    private AggregateActualClaimsStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static AggregateActualClaimsStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IAggregateActualClaimsStrategy getDefault() {
        return new NoAggregateActualClaimsStrategy()
    }

    static IAggregateActualClaimsStrategy getStrategy(AggregateActualClaimsStrategyType type, Map parameters) {
        switch (type) {
            case AggregateActualClaimsStrategyType.NONE:
                return new NoAggregateActualClaimsStrategy()
            case AggregateActualClaimsStrategyType.AGGREGATE:
                return new AggregateActualClaimsStrategy(
                        history: (ConstrainedMultiDimensionalParameter) parameters['history'])
        }
        throw new NotImplementedException(type.toString() + " is not implemented")
    }
}
