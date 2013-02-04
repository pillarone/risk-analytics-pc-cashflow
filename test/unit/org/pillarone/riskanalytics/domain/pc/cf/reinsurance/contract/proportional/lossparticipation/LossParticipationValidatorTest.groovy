package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation

import grails.test.GrailsUnitTestCase
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

class LossParticipationValidatorTest extends GrailsUnitTestCase {
    LossParticipationValidator validator
    List<ParameterHolder> parameters

    @Override
    protected void setUp() {
        super.setUp()
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
        validator = new LossParticipationValidator()
        parameters = []
    }

    private void setupLossParticipationValidator(LossParticipationStrategyType lossParticipationType, def lossRatio, def losPartByCedant) {
        ConstrainedMultiDimensionalParameter participation = new ConstrainedMultiDimensionalParameter([lossRatio, losPartByCedant], [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        def lossParticipation = LossParticipationStrategyType.getStrategy(lossParticipationType, [participation: participation])
        parameters << new ParameterObjectParameterHolder(
                'quota', 1,
                ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, [
                        quotaShare: 0d,
                        lossParticipation: lossParticipation,
                        limit: LimitStrategyType.getDefault(),
                        commission: CommissionStrategyType.getNoCommission()]),

        )
    }

    void testValidator_novalidation() {
        validator.validate(parameters)
    }

    void testValidator_quotaWithoutParticipation() {
        setupLossParticipationValidator(LossParticipationStrategyType.NOPARTICIPATION,[],[])
        validator.validate(parameters)
    }

    void testValidator_minSize() {
        setupLossParticipationValidator(LossParticipationStrategyType.LOSSPARTICIPATION, [-1d], [-10d])
        List<ParameterValidation> result = validator.validate(parameters)
        assert 2 == result.size()
        ParameterValidation error = result[0]
        assert ValidationType.ERROR == error.validationType
        assert 'values.must.not.be.smaller.than.zero' == error.msg
        assert ValidationType.ERROR == result[1].validationType
        assert 'values.must.not.be.smaller.than.zero' == error.msg
    }

    void testValidator_maxSize() {
        setupLossParticipationValidator(LossParticipationStrategyType.LOSSPARTICIPATION, [0d], [101d])
        List<ParameterValidation> result = validator.validate(parameters)
        assert 1 == result.size()
        ParameterValidation error = result[0]
        assert ValidationType.ERROR == error.validationType
        assert 'loss.parts.must.not.be.greater.than.hundret' == error.msg
        assert 'quota:lossParticipation:participation' == error.path
    }

    void testValidator_lossRatiosMustIncreaseStrictly() {
        setupLossParticipationValidator(LossParticipationStrategyType.LOSSPARTICIPATION, [0d, 1d, 2d, 2d], [0d, 0d, 0d, 0d])
        List<ParameterValidation> result = validator.validate(parameters)
        assert 1 == result.size()
        ParameterValidation error = result[0]
        assert ValidationType.ERROR == error.validationType
        assert 'values.do.not.increase.strictly' == error.msg
    }
}
