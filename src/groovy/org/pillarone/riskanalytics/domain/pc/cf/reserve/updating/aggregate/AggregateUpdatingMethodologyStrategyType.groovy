package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker
import org.apache.commons.lang.NotImplementedException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateUpdatingMethodologyStrategyType extends AbstractParameterObjectClassifier {

    public static final AggregateUpdatingMethodologyStrategyType PLEASESELECT = new AggregateUpdatingMethodologyStrategyType(
            'Please Select', 'PLEASESELECT', [:])
    public static final AggregateUpdatingMethodologyStrategyType BFREPORTING = new AggregateUpdatingMethodologyStrategyType(
            'BF Reporting', 'BFREPORTING',
            [updatingPattern: new ConstrainedString(IUpdatingPatternMarker.class, "")])
    public static final AggregateUpdatingMethodologyStrategyType ORIGINALULTIMATE = new AggregateUpdatingMethodologyStrategyType(
            'Original Ultimate', 'ORIGINALULTIMATE', [:])

    public static final all = [PLEASESELECT, BFREPORTING, ORIGINALULTIMATE]

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
            case AggregateUpdatingMethodologyStrategyType.BFREPORTING:
                return new AggregateUpdatingBFReportingMethodology(updatingPattern: parameters['updatingPattern'])
            case AggregateUpdatingMethodologyStrategyType.ORIGINALULTIMATE:
                return new AggregateUpdatingOriginalUltimateMethodology()
            case AggregateUpdatingMethodologyStrategyType.PLEASESELECT:
                return new NoUpdatingMethodology()
            default: throw new NotImplementedException(type.toString() + " is not implemented")
        }
    }
}
