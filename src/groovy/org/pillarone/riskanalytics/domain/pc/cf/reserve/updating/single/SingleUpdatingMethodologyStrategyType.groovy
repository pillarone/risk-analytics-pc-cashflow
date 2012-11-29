package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;


import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker
import org.apache.commons.lang.NotImplementedException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SingleUpdatingMethodologyStrategyType extends AbstractParameterObjectClassifier {

    public static final SingleUpdatingMethodologyStrategyType NONE = new SingleUpdatingMethodologyStrategyType('None', 'NONE', [:])
    public static final SingleUpdatingMethodologyStrategyType SINGLE = new SingleUpdatingMethodologyStrategyType('Single', 'SINGLE',
            [updatingPattern: new ConstrainedString(IUpdatingPatternMarker.class, ""),
             methodology: SingleUpdatingMethod.ORIGINAL_ULTIMATE])

    public static final all = [NONE, SINGLE]

    protected static Map types = [:]
    static {
        SingleUpdatingMethodologyStrategyType.all.each {
            SingleUpdatingMethodologyStrategyType.types[it.toString()] = it
        }
    }

    private SingleUpdatingMethodologyStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static SingleUpdatingMethodologyStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    static ISingleUpdatingMethodologyStrategy getStrategy(SingleUpdatingMethodologyStrategyType type, Map parameters) {
        switch (type) {
            case SingleUpdatingMethodologyStrategyType.SINGLE:
                return new SingleUpdatingMethodology(
                        updatingPattern: parameters['updatingPattern'],
                        methodology: parameters['methodology'])
            case SingleUpdatingMethodologyStrategyType.NONE:
                return new NoUpdatingMethodology()
            default: throw new NotImplementedException(type.toString() + " is not implemented")
        }

    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static ISingleUpdatingMethodologyStrategy getDefault() {
        return new NoUpdatingMethodology()
    }
}
