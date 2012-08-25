package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LimitStrategyType extends AbstractParameterObjectClassifier {

    public static final LimitStrategyType NONE = new LimitStrategyType("none", "NONE", [:])
    public static final LimitStrategyType AAL = new LimitStrategyType("AAL", "AAL", ['aal': 0d])
    public static final LimitStrategyType AAD = new LimitStrategyType("AAD", "AAD", ['aad': 0d])
    public static final LimitStrategyType AALAAD = new LimitStrategyType("AAL, AAD", "AALAAD", ['aal': 0d, 'aad': 0d])
    public static final LimitStrategyType EVENTLIMIT = new LimitStrategyType("event", "EVENTLIMIT", ['eventLimit': 0d])
    public static final LimitStrategyType EVENTLIMITAAL = new LimitStrategyType("event, AAL", "EVENTLIMITAAL", ['aal': 0d, 'eventLimit': 0d])

    public static final all = [NONE, AAL, AAD, AALAAD, EVENTLIMIT, EVENTLIMITAAL]

    protected static Map types = [:]
    static {
        LimitStrategyType.all.each {
            LimitStrategyType.types[it.toString()] = it
        }
    }

    private LimitStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static LimitStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        getStrategy(this, parameters)
    }

    /**
     * @return LimitStrategyType.NONE
     */
    public static ILimitStrategy getDefault() {
        getStrategy(LimitStrategyType.NONE, [:])
    }

    public static ILimitStrategy getStrategy(LimitStrategyType type, Map parameters) {
        switch (type) {
            case LimitStrategyType.NONE:
                return new NoneLimitStrategy()
            case LimitStrategyType.AAL:
                return new AalLimitStrategy(aal: (Double) parameters['aal'])
            case LimitStrategyType.AAD:
                return new AadLimitStrategy(aad: (Double) parameters['aad'])
            case LimitStrategyType.AALAAD:
                return new AalAadLimitStrategy(aal: (Double) parameters['aal'], aad: (Double) parameters['aad'])
            case LimitStrategyType.EVENTLIMIT:
                return new EventLimitStrategy(eventLimit: (Double) parameters['eventLimit'])
            case LimitStrategyType.EVENTLIMITAAL:
                return new EventAalLimitStrategy(eventLimit: (Double) parameters['eventLimit'], aal: (Double) parameters['aal'])
            default:
                throw new IllegalArgumentException("Unknown LimitStrategyType provided " + type.toString())
        }
    }
}
