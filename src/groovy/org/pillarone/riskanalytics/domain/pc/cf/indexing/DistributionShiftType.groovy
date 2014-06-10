package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class DistributionShiftType extends AbstractParameterObjectClassifier {


    public static final DistributionShiftType NONE = new DistributionShiftType(
        "none", "NONE", [:])
    public static final DistributionShiftType SHIFT = new DistributionShiftType(
        "shift", "SHIFT", ["shift": 0d])

    public static final all = [NONE, SHIFT]

    protected static Map types = [:]

    static {
        DistributionShiftType.all.each {
            DistributionShiftType.types[it.toString()] = it
        }
    }
    private DistributionShiftType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static DistributionShiftType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IDistributionShiftStrategy getDefault() {
        return getStrategy(DistributionShiftType.NONE, [:]);
    }

    static IDistributionShiftStrategy getStrategy(DistributionShiftType modifier, Map parameters) {
        switch (modifier) {
            case DistributionShiftType.NONE:
                return new DistributionNotShifted()
            case DistributionShiftType.SHIFT:
                return new DistributionShifted(shift: parameters[StochasticIndexStrategy.SHIFT])
            default:
                throw new InvalidParameterException("['DistributionShift.notImplemented','" + modifier.typeName + "']")
                break
        }
    }
}
