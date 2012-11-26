package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.ReserveVolatility

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SingleActualClaimsStrategyType extends AbstractParameterObjectClassifier {

    public static final SingleActualClaimsStrategyType NONE = new SingleActualClaimsStrategyType('none', 'NONE', [:])
    public static final SingleActualClaimsStrategyType SINGLE = new SingleActualClaimsStrategyType('single', 'SINGLE',
            [history : SingleActualClaimsStrategy.getDefaultHistory(),
                    reserveVolatility: ReserveVolatility.NONE,])

    public static final all = [NONE, SINGLE]

    protected static Map types = [:]
    static {
        SingleActualClaimsStrategyType.all.each {
            SingleActualClaimsStrategyType.types[it.toString()] = it
        }
    }

    private SingleActualClaimsStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static SingleActualClaimsStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static ISingleActualClaimsStrategy getDefault() {
        return new NoSingleActualClaimsStrategy()
    }

    static ISingleActualClaimsStrategy getStrategy(SingleActualClaimsStrategyType type, Map parameters) {
        switch (type) {
            case SingleActualClaimsStrategyType.NONE:
                return new NoSingleActualClaimsStrategy()
            case SingleActualClaimsStrategyType.SINGLE:
                return new SingleActualClaimsStrategy(
                    history: parameters['history'],
                    reserveVolatility: parameters['reserveVolatility']
            )
        }
    }
}
