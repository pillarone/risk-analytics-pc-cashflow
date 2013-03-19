package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation

import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.*

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LossParticipationStrategyType extends AbstractParameterObjectClassifier {

    public static final LossParticipationStrategyType NOPARTICIPATION = new LossParticipationStrategyType("no participation", "NOPARTICIPATION", [:])
    public static final LossParticipationStrategyType LOSSPARTICIPATION = new LossParticipationStrategyType("loss participation", "LOSSPARTICIPATION",
            ['participation': new ConstrainedMultiDimensionalParameter(
                    [[0d], [0d]],
                    [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                    ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])

    public static final all = [NOPARTICIPATION, LOSSPARTICIPATION]

    protected static Map types = [:]
    static {
        LossParticipationStrategyType.all.each {
            LossParticipationStrategyType.types[it.toString()] = it
        }
    }

    private LossParticipationStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static LossParticipationStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        getStrategy(this, parameters)
    }

    public static ILossParticipationStrategy getNoParticipation() {
        getStrategy(LossParticipationStrategyType.NOPARTICIPATION, [:])
    }

    public static ILossParticipationStrategy getStrategy(LossParticipationStrategyType type, Map parameters) {
        switch (type) {
            case LossParticipationStrategyType.NOPARTICIPATION:
                return new NoParticipationStrategy()
            case LossParticipationStrategyType.LOSSPARTICIPATION:
                return new LossParticipationStrategy(
                    participation: (ConstrainedMultiDimensionalParameter) parameters['participation']
                )
            default: throw new IllegalArgumentException("$type not implemented")
        }
    }
}
