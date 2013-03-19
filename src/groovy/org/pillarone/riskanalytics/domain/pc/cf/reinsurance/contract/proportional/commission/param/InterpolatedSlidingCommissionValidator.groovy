package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.validation.AbstractParameterizationValidator

class InterpolatedSlidingCommissionValidator extends AbstractParameterizationValidator {
    private static String LOSS_RATIOS_DO_NOT_INREASE = 'loss.ratios.do.not.increase'
    private static final String LOSS_RATIOS_MUST_NOT_BE_SMALLER_THAN_ZERO = 'loss.ratios.must.not.be.smaller.than.zero'
    private static final String COMMISION_RATES_MUST_NOT_BE_SMALLER_THAN_ZERO = 'commission.rates.must.not.be.smaller.than.zero'
    private static final String COMMISION_RATE_MUST_NOT_BE_GREATER_THAN_HUNDRET = 'commission.rates.must.not.be.greater.than.hundret'
    private static final String COMMISION_RATE_DO_NOT_DECREASE = 'commission.rates.do.not.decrease'
    private static final double MINSIZE = 0d
    private static final double MAXSIZE = 100d

    /*
commission.rates.must.not.be.smaller.than.zero=Values for commission rate is smaller than 0.
commission.rates.must.not.be.greater.than.hundret=Commission rate is bigger than 100.
commission.rates.do.not.decrease=Comission rate does not decrease.
     */

    @Override
    void registerConstraints(AbstractParameterValidationService validationService) {
        validationService.register(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION) { Map parameters ->
            for (Double lossRatio : getStrategy(parameters).lossRatios) {
                if (lossRatio < MINSIZE) {
                    return [ValidationType.ERROR, LOSS_RATIOS_MUST_NOT_BE_SMALLER_THAN_ZERO]
                }
            }
        }
        validationService.register(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION) { Map parameters ->
            def lossRatios = getStrategy(parameters).lossRatios
            Double previousRatio = lossRatios[0]
            for (Double ratio : lossRatios) {
                if (ratio < previousRatio) {
                    return [ValidationType.ERROR, LOSS_RATIOS_DO_NOT_INREASE]
                }
                previousRatio = ratio
            }
        }

        validationService.register(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION) { Map parameters ->
            def commisionRates = getStrategy(parameters).commisionRates
            Double previousRatio = commisionRates[0]
            for (Double commissionRate : commisionRates) {
                if (commissionRate > previousRatio) {
                    return [ValidationType.WARNING, COMMISION_RATE_DO_NOT_DECREASE]
                }
                previousRatio = commissionRate
            }
        }
        validationService.register(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION) { Map parameters ->
            for (Double commissionRate : getStrategy(parameters).commisionRates) {
                if (commissionRate < MINSIZE) {
                    return [ValidationType.WARNING, COMMISION_RATES_MUST_NOT_BE_SMALLER_THAN_ZERO]
                }
            }
        }
        validationService.register(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION) { Map parameters ->
            for (Double commissionRate : getStrategy(parameters).commisionRates) {
                if (commissionRate > MAXSIZE) {
                    return [ValidationType.WARNING, COMMISION_RATE_MUST_NOT_BE_GREATER_THAN_HUNDRET]
                }
            }
        }
    }

    @Override
    protected String getErrorPath(ParameterObjectParameterHolder parameter) {
        super.getErrorPath(parameter) + ':commissionBands'
    }

    @Override
    ParameterObjectParameterHolder verifyParameter(ParameterHolder parameter) {
        if (parameter instanceof ParameterObjectParameterHolder && (parameter.classifier == ReinsuranceContractType.QUOTASHARE)){
            ParameterObjectParameterHolder commission = parameter?.classifierParameters?.commission
            if (commission && commission.classifier == CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION) {
                return commission
            }
        }
        return null
    }

    private InterpolatedSlidingCommissionStrategy getStrategy(Map parameters) {
        return CommissionStrategyType.getStrategy(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION, parameters)
    }

}
