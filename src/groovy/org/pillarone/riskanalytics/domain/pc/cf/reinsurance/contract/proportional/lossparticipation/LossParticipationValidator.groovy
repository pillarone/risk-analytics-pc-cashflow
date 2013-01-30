package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.validation.AbstractParameterizationValidator

class LossParticipationValidator extends AbstractParameterizationValidator {
    private static String VALUES_DO_NOT_INREASE_STRICTLY = 'values.do.not.increase.strictly'
    private static String VALUES_MUST_NOT_BE_SMALLER_THAN_ZERO = 'values.must.not.be.smaller.than.zero'
    private static String PART_MUST_NOT_BE_GREATER_THAN_HUNDRET = 'loss.parts.must.not.be.greater.than.hundret'
    private static final double MINSIZE = 0d
    private static final double MAXSIZE = 100d

    @Override
    void registerConstraints(AbstractParameterValidationService validationService) {
        validationService.register(LossParticipationStrategyType.LOSSPARTICIPATION) { Map parameters ->
            verifyMinSize(getStrategy(parameters).lossRatios)
        }
        validationService.register(LossParticipationStrategyType.LOSSPARTICIPATION) { Map parameters ->
            verifyMinSize(getStrategy(parameters).lossParts)
        }


        validationService.register(LossParticipationStrategyType.LOSSPARTICIPATION) { Map parameters ->
            LossParticipationStrategy lossParticipationStrategy = getStrategy(parameters)
            Double[] ratios = lossParticipationStrategy.lossRatios
            Double previousRatio
            for (Double ratio : ratios) {
                if (previousRatio && ratio <= previousRatio) {
                    return [ValidationType.ERROR, VALUES_DO_NOT_INREASE_STRICTLY]
                }
                previousRatio = ratio
            }

        }

        validationService.register(LossParticipationStrategyType.LOSSPARTICIPATION) { Map parameters ->
            LossParticipationStrategy lossParticipationStrategy = getStrategy(parameters)
            Double[] lossParts = lossParticipationStrategy.lossParts
            for (Double lossPart : lossParts) {
                if (lossPart > MAXSIZE) {
                    return [ValidationType.ERROR, PART_MUST_NOT_BE_GREATER_THAN_HUNDRET]
                }
            }

        }
    }

    @Override
    protected String getErrorPath(ParameterObjectParameterHolder parameter) {
        return super.getErrorPath(parameter) + ':participation'
    }

    @Override
    ParameterObjectParameterHolder verifyParameter(ParameterHolder parameter) {
        if (parameter instanceof ParameterObjectParameterHolder && (parameter.classifier == ReinsuranceContractType.QUOTASHARE)) {
            ParameterObjectParameterHolder lossParticipation = parameter?.classifierParameters?.lossParticipation
            if (lossParticipation && lossParticipation.classifier == LossParticipationStrategyType.LOSSPARTICIPATION) {
                return lossParticipation
            }
        }
        return null
    }

    private def verifyMinSize(Double[] ratios) {
        for (Double ratio : ratios) {
            if (ratio < MINSIZE) {
                return [ValidationType.ERROR, VALUES_MUST_NOT_BE_SMALLER_THAN_ZERO]
            }
        }
    }

    private LossParticipationStrategy getStrategy(Map parameters) {
        return LossParticipationStrategyType.getStrategy(LossParticipationStrategyType.LOSSPARTICIPATION, parameters)
    }
}
