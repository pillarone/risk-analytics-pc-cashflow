package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateUpdatingMethodologyStrategyType extends AbstractParameterObjectClassifier {

    public static final AggregateUpdatingMethodologyStrategyType NONE = new AggregateUpdatingMethodologyStrategyType('none', 'NONE', Collections.emptyMap())
    public static final AggregateUpdatingMethodologyStrategyType AGGREGATE = new AggregateUpdatingMethodologyStrategyType(
            'aggregate', 'AGGREGATE',
            [updatingPattern: new ConstrainedString(IUpdatingPatternMarker.class, ""),
                    methodology: AggregateUpdatingMethod.ORIGINAL_ULTIMATE])

    public static final all = [NONE, AGGREGATE]

    protected static Map types = [:]
    static {
        AggregateUpdatingMethodologyStrategyType.all.each {
            AggregateUpdatingMethodologyStrategyType.types[it.toString()] = it
        }
    }

    private AggregateUpdatingMethodologyStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static AggregateUpdatingMethodologyStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IAggregateUpdatingMethodologyStrategy getDefault() {
        return new NoUpdatingMethodology()
    }

    static IAggregateUpdatingMethodologyStrategy getStrategy(AggregateUpdatingMethodologyStrategyType type, Map parameters) {
        switch (type) {
            case AggregateUpdatingMethodologyStrategyType.AGGREGATE:
                return new AggregateUpdatingMethodology(
                        updatingPattern: parameters['updatingPattern'],
                        methodology: parameters['methodology'])
            case AggregateUpdatingMethodologyStrategyType.NONE:
                return new NoUpdatingMethodology()
        }
    }
}
