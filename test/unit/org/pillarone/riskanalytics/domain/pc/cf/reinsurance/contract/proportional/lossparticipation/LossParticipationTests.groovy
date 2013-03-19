package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LossParticipationTests extends GroovyTestCase{

    public static double EPSILON = 1E-9

    void testUsage() {
        ILossParticipation lossParticipation = LossParticipationStrategyType.getStrategy(
                LossParticipationStrategyType.LOSSPARTICIPATION,
                [participation: new ConstrainedMultiDimensionalParameter(
                        [[0d, 0.6d, 0.7d, 0.8d], [1d, 0.5d, 0.5d, 1d]],
                        [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))]).lossParticpation
        assertEquals "50%", 0.5, lossParticipation.lossParticipation(0.5)
        assertEquals "60%", 0.6, lossParticipation.lossParticipation(0.6)
        assertEquals "65%", 0.625, lossParticipation.lossParticipation(0.65)
        assertEquals "70%", 0.65, lossParticipation.lossParticipation(0.70), EPSILON
        assertEquals "80%", 0.7, lossParticipation.lossParticipation(0.8)
        assertEquals "90%", 0.8, lossParticipation.lossParticipation(0.9), EPSILON
        assertEquals "100%", 0.9, lossParticipation.lossParticipation(1), EPSILON
    }

    void testFromLRStartsAboveZero() {
        ILossParticipation lossParticipation = LossParticipationStrategyType.getStrategy(
                LossParticipationStrategyType.LOSSPARTICIPATION,
                [participation: new ConstrainedMultiDimensionalParameter(
                        [[0.2d, 0.6d, 0.7d, 0.8d], [1d, 0.5d, 0.5d, 1d]],
                        [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))]).lossParticpation
        assertEquals "0%", 0.0, lossParticipation.lossParticipation(0)
        assertEquals "10%", 0.0, lossParticipation.lossParticipation(0.1)
        assertEquals "20%", 0.0, lossParticipation.lossParticipation(0.2)
        assertEquals "50%", 0.3, lossParticipation.lossParticipation(0.5)
        assertEquals "60%", 0.4, lossParticipation.lossParticipation(0.6), EPSILON
        assertEquals "65%", 0.425, lossParticipation.lossParticipation(0.65)
        assertEquals "70%", 0.45, lossParticipation.lossParticipation(0.70), EPSILON
        assertEquals "80%", 0.5, lossParticipation.lossParticipation(0.8)
        assertEquals "90%", 0.6, lossParticipation.lossParticipation(0.9), EPSILON
        assertEquals "100%", 0.7, lossParticipation.lossParticipation(1), EPSILON
    }

    void testVoidTable() {
        ILossParticipation lossParticipation = LossParticipationStrategyType.getStrategy(
                LossParticipationStrategyType.LOSSPARTICIPATION,
                [participation: new ConstrainedMultiDimensionalParameter(
                        [[], []],
                        [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))]).lossParticpation
        assertEquals "0%", 0.0, lossParticipation.lossParticipation(0)
        assertEquals "10%", 0.0, lossParticipation.lossParticipation(0.1)
        assertEquals "20%", 0.0, lossParticipation.lossParticipation(0.2)
        assertEquals "50%", 0.0, lossParticipation.lossParticipation(0.5)
        assertEquals "60%", 0.0, lossParticipation.lossParticipation(0.6), EPSILON
        assertEquals "65%", 0.0, lossParticipation.lossParticipation(0.65)
        assertEquals "70%", 0.0, lossParticipation.lossParticipation(0.70), EPSILON
        assertEquals "80%", 0.0, lossParticipation.lossParticipation(0.8)
        assertEquals "90%", 0.0, lossParticipation.lossParticipation(0.9), EPSILON
        assertEquals "100%", 0.0, lossParticipation.lossParticipation(1), EPSILON
    }

    void testOneRow() {
        ILossParticipation lossParticipation = LossParticipationStrategyType.getStrategy(
                LossParticipationStrategyType.LOSSPARTICIPATION,
                [participation: new ConstrainedMultiDimensionalParameter(
                        [[0], [0.5]],
                        [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))]).lossParticpation
        assertEquals "0%", 0.0, lossParticipation.lossParticipation(0)
        assertEquals "10%", 0.05, lossParticipation.lossParticipation(0.1)
        assertEquals "20%", 0.1, lossParticipation.lossParticipation(0.2)
        assertEquals "50%", 0.25, lossParticipation.lossParticipation(0.5)
        assertEquals "60%", 0.3, lossParticipation.lossParticipation(0.6), EPSILON
        assertEquals "100%", 0.5, lossParticipation.lossParticipation(1), EPSILON
    }
}
