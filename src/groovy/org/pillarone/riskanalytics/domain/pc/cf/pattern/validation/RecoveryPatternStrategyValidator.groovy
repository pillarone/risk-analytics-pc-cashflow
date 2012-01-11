package org.pillarone.riskanalytics.domain.pc.cf.pattern.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl

/**
 * Compared with the PatternStrategyValidator this validator does not ensure that the final value is 1 but restricts
 * currently the pattern length to one and allows no delay as default and recovery logic in ReinsuranceContract can't
 * cope with it.
 *
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class RecoveryPatternStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(RecoveryPatternStrategyValidator)
    private static final double EPSILON = 1E-8 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public RecoveryPatternStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof PatternStrategyType && parameter.path.contains("patterns:subRecoveryPatterns")) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(PatternStrategyType.INCREMENTAL) {Map type ->
            double[] values = type.incrementalPattern.getColumnByName(PatternStrategyType.INCREMENTS)
            double sum = (double) GroovyCollections.sum(values)
            if (sum <= 1.0 + EPSILON) return true
            [ValidationType.HINT, "incremental.pattern.error.sum.greater.than.one", sum]
        }

        validationService.register(PatternStrategyType.INCREMENTAL) {Map type ->
            double[] values = type.incrementalPattern.getColumnByName(PatternStrategyType.INCREMENTS)
            if (values.length == 0) {
                return [ValidationType.ERROR, "incremental.pattern.error.incremental.values.empty", values]
            }

            double sum = 0
            for (int i = 0; i < values.length; i++) {
                sum += values[i]
                if (sum < -EPSILON) {
                    return [ValidationType.ERROR, "incremental.pattern.error.cumulated.increments.negative", i + 1, values[i], sum]
                }
            }

            for (int i = 0; i < values.length; i++) {
                if (values[i] < 0 || values[i] > 1) {
                    return [ValidationType.HINT, "incremental.pattern.error.incremental.values.not.in.unity.interval", i + 1, values[i]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            if (values.length == 0) {
                return [ValidationType.HINT, "cumulative.pattern.error.cumulative.values.empty", values]
            }
            if (values[0] < 0) {
                return [ValidationType.HINT, "cumulative.pattern.error.cumulative.values.negative", values[0]]
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)

            if (values[values.size() - 1] > 1) {
                return [ValidationType.HINT, "cumulative.pattern.error.cumulative.values.greater.than.one", values[values.size() - 1]]
            }
            return true
        }


        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            for (int i = 0; i < values.length - 1; i++) {
                if (values[i + 1] < values[i]) {
                    return [ValidationType.HINT, "cumulative.pattern.error.cumulative.values.not.increasing", i + 1, values[i], values[i + 1]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
            if (months[0] < 0) {
                return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.months.not.non-negative", months[0]]
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
            for (int i = 0; i < months.length - 1; i++) {
                if (months[i + 1] <= months[i]) {
                    return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.INCREMENTAL) {Map type ->
            double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)

            if (months[0] < 0) {
                return [ValidationType.ERROR, "incremental.pattern.error.cumulative.months.not.non-negative", months[0]]
            }
            return true
        }

        validationService.register(PatternStrategyType.INCREMENTAL) {Map type ->
            double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)
            for (int i = 0; i < months.length - 1; i++) {
                if (months[i + 1] <= months[i]) {
                    return [ValidationType.ERROR, "incremental.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) {Map type ->
            double[] values = type.ageToAgePattern.getColumnByName(PatternStrategyType.LINK_RATIOS)
            if (values.length == 0) {
                return [ValidationType.ERROR, "age.to.age.pattern.error.ratios.empty", values]
            }

            for (int i = 0; i < values.length; i++) {
                if (values[i] <= 0) {
                    return [ValidationType.ERROR, "age.to.age.pattern.error.ratios.non.positive", i + 1, values[i]]
                }
            }

            for (int i = 0; i < values.length; i++) {
                if (values[i] < 1) {
                    return [ValidationType.HINT, "age.to.age.pattern.error.ratios.smaller.one", i + 1, values[i]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) {Map type ->
            double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)

            if (months[0] < 0) {
                return [ValidationType.ERROR, "age.to.age.pattern.error.cumulative.months.not.non-negative", months[0]]
            }
            return true
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) {Map type ->
            double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)
            for (int i = 0; i < months.length - 1; i++) {
                if (months[i + 1] <= months[i]) {
                    return [ValidationType.ERROR, "age.to.age.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                }
            }
            return true
        }
        
        validationService.register(PatternStrategyType.INCREMENTAL) { Map type ->
            double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)
            if (months.size() > 1 || (months.size() > 0 && months[0] != 0)) {
                return [ValidationType.ERROR, "default.and.recovery.implementation.allows.currently.one.undelayed.recovery.only"]
            }
        }

        validationService.register(PatternStrategyType.CUMULATIVE) { Map type ->
            double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
            if (months.size() > 1 || (months.size() > 0 && months[0] != 0)) {
                return [ValidationType.ERROR, "default.and.recovery.implementation.allows.currently.one.undelayed.recovery.only"]
            }
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) { Map type ->
            double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)
            if (months.size() > 1 || (months.size() > 0 && months[0] != 0)) {
                return [ValidationType.ERROR, "default.and.recovery.implementation.allows.currently.one.undelayed.recovery.only"]
            }
        }
    }
}

