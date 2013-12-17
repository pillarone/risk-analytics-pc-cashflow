package org.pillarone.riskanalytics.domain.pc.cf.pattern.validation

import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.core.simulation.item.parameter.ConstrainedStringParameterHolder
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PeriodsNotIncreasingException

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PatternStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(PatternStrategyValidator)
    private static final double EPSILON = 1E-8 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public PatternStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path                                     */
        Map<String, PatternPacket> payoutPatterns = [:]
        /** key: path                                     */
        Map<String, PatternPacket> reportingPatterns = [:]
        /** key: path                                     */
        Map<String, String> reportingPatternPerClaimsGenerator = [:]
        /** key: path                                     */
        Map<String, String> payoutPatternPerClaimsGenerator = [:]


        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof PatternStrategyType && !parameter.path.contains("patterns:subRecoveryPatterns")) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    if (parameter.path.contains("patterns:subPayoutPatterns:")) {
                        payoutPatterns[parameter.path - 'patterns:subPayoutPatterns:' - ':parmPattern'] = getPattern(parameter)
                    }
                    if (parameter.path.contains("patterns:subReportingPatterns:")) {
                        reportingPatterns[parameter.path - 'patterns:subReportingPatterns:' - ':parmPattern'] = getPattern(parameter)
                    }

                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                } else if (classifier instanceof PayoutReportingCombinedPatternStrategyType) {
                    payoutPatterns[parameter.path - 'patterns:subPayoutAndReportingPatterns:' - ':parmPattern'] = getPayoutPattern(parameter)
                    reportingPatterns[parameter.path - 'patterns:subPayoutAndReportingPatterns:' - ':parmPattern'] = getReportingPattern(parameter)
                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            } else if (parameter instanceof ConstrainedStringParameterHolder && parameter.path.contains("claimsGenerators")) {
                if (parameter.path.contains("parmReportingPattern")) {
                    reportingPatternPerClaimsGenerator[parameter.path - ':parmReportingPattern'] = parameter.value.getStringValue()
                } else if (parameter.path.contains("parmPayoutPattern")) {
                    payoutPatternPerClaimsGenerator[parameter.path - ':parmPayoutPattern'] = parameter.value.getStringValue()
                }
            }
        }

        for (String claimsGeneratorPath : reportingPatternPerClaimsGenerator.keySet()) {
            PatternPacket reportingPattern = reportingPatterns[reportingPatternPerClaimsGenerator[claimsGeneratorPath]]
            PatternPacket payoutPattern = payoutPatterns[payoutPatternPerClaimsGenerator[claimsGeneratorPath]]
            TreeMap<Integer, Double> reportingValuesPerMonth = getCumulativeValuePerMonth(reportingPattern)
            TreeMap<Integer, Double> payoutValuesPerMonth = getCumulativeValuePerMonth(payoutPattern)
            for (Map.Entry<Integer, Double> payoutEntry : payoutValuesPerMonth?.entrySet()) {
                if (payoutEntry.value <= reportingValuesPerMonth.floorEntry(payoutEntry.key).value + EPSILON) continue
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'claims.generator.reporting.pattern.smaller.than.payout.pattern',
                        [payoutEntry.key, payoutEntry.value, reportingValuesPerMonth.floorEntry(payoutEntry.key).value])
                error.path = claimsGeneratorPath + ':parmPayoutPattern'
                errors << error
                // next error only for inking all of the associated paths
                error = new ParameterValidationImpl(ValidationType.ERROR,
                        'claims.generator.reporting.pattern.smaller.than.payout.pattern',
                        [payoutEntry.key, payoutEntry.value, reportingValuesPerMonth.floorEntry(payoutEntry.key).value])
                error.path = claimsGeneratorPath + ':parmReportingPattern'
                errors << error
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(PatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return [ValidationType.ERROR, "incremental.pattern.error.incremental.values.empty"]
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.empty"]
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return [ValidationType.ERROR, "age.to.age.pattern.error.ratios.empty"]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return [ValidationType.ERROR, "incremental.combined.pattern.error.incremental.payout.values.empty"]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.payout.values.empty"]
        }

        validationService.register(PatternStrategyType.INCREMENTAL) { Map type ->
            if (type.incrementalPattern.isEmpty()) return
            Double[] values = type.incrementalPattern.getColumnByName(PatternStrategyType.INCREMENTS)
            Double sum = GroovyCollections.sum(values) as Double
            if (sum == null || sum >= 1.0 - EPSILON && sum <= 1.0 + EPSILON) return true
            [ValidationType.ERROR, "incremental.pattern.error.sum.not.one", sum]
        }

        validationService.register(PatternStrategyType.INCREMENTAL) { Map type ->
            if (type.incrementalPattern.isEmpty()) return
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

        validationService.register(PatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)
                if (months.length > 0 && months[0] < 0) {
                    return [ValidationType.ERROR, "incremental.pattern.error.cumulative.months.not.non-negative", months[0]]
                }
                return true
        }

        validationService.register(PatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)
                for (int i = 0; i < months.length - 1; i++) {
                    if (months[i + 1] <= months[i]) {
                        return [ValidationType.ERROR, "incremental.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                    }
                }
                return true
        }


        validationService.register(PatternStrategyType.CUMULATIVE) { Map type ->
            if (type.cumulativePattern.isEmpty()) return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.empty"]
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            if (values.length == 0) {
                return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.empty", values]
            }
            if (values[0] < 0) {
                return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.negative", values[0]]
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) { Map type ->
            if (type.cumulativePattern.isEmpty()) return
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            if (values.length == 0) return
            if (values[values.length - 1] == 1) return true
            [ValidationType.ERROR, "cumulative.pattern.error.last.value.not.one", values[values.length - 1]]
        }

        validationService.register(PatternStrategyType.CUMULATIVE) { Map type ->
            if (type.cumulativePattern.isEmpty()) return
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            for (int i = 0; i < values.length - 1; i++) {
                if (values[i + 1] < values[i]) {
                    return [ValidationType.HINT, "cumulative.pattern.error.cumulative.values.not.increasing", i + 1, values[i], values[i + 1]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) { Map type ->
            if (type.ageToAgePattern.isEmpty()) return
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

        validationService.register(PatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] values = type.ageToAgePattern.getColumnByName(PatternStrategyType.LINK_RATIOS)
                if (values.length == 0) return
                if (values[values.length - 1] == 1) return true
                [ValidationType.ERROR, "age.to.age.pattern.error.last.ratio.not.one", values[values.length - 1]]
        }


        validationService.register(PatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
                if (months[0] < 0) {
                    return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.months.not.non-negative", months[0]]
                }
                return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
                for (int i = 0; i < months.length - 1; i++) {
                    if (months[i + 1] <= months[i]) {
                        return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                    }
                }
                return true
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)
                if (months[0] < 0) {
                    return [ValidationType.ERROR, "age.to.age.pattern.error.cumulative.months.not.non-negative", months[0]]
                }
                return true
        }

        validationService.register(PatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)
                for (int i = 0; i < months.length - 1; i++) {
                    if (months[i + 1] <= months[i]) {
                        return [ValidationType.ERROR, "age.to.age.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] payoutValues = type.incrementalPattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.INCREMENTS_PAYOUT)
                Double sum = GroovyCollections.sum(payoutValues)
                if (sum >= 1.0 - EPSILON && sum <= 1.0 + EPSILON) return true
                [ValidationType.ERROR, "incremental.combined.pattern.payout.error.sum.not.one", sum]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] reportedValues = type.incrementalPattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.INCREMENTS_REPORTED)
                Double sum = GroovyCollections.sum(reportedValues)
                if (sum >= 1.0 - EPSILON && sum <= 1.0 + EPSILON) return true
                [ValidationType.ERROR, "incremental.combined.pattern.reported.error.sum.not.one", sum]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] payoutValues = type.incrementalPattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.INCREMENTS_PAYOUT)
                if (payoutValues.length == 0) {
                    return [ValidationType.ERROR, "incremental.combined.pattern.error.incremental.payout.values.empty", payoutValues]
                }

                double sum = 0
                for (int i = 0; i < payoutValues.length; i++) {
                    sum += payoutValues[i]
                    if (sum < -EPSILON) {
                        return [ValidationType.ERROR, "incremental.combined.pattern.error.cumulated.payout.increments.negative", i + 1, payoutValues[i], sum]
                    }
                }

                for (int i = 0; i < payoutValues.length; i++) {
                    if (payoutValues[i] < 0 || payoutValues[i] > 1) {
                        return [ValidationType.HINT, "incremental.combined.pattern.error.incremental.payout.values.not.in.unity.interval", i + 1, payoutValues[i]]
                    }
                }

                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] reportedValues = type.incrementalPattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.INCREMENTS_REPORTED)
                if (reportedValues.length == 0) {
                    return [ValidationType.ERROR, "incremental.combined.pattern.error.incremental.reported.values.empty", reportedValues]
                }

                double sum = 0
                for (int i = 0; i < reportedValues.length; i++) {
                    sum += reportedValues[i]
                    if (sum < -EPSILON) {
                        return [ValidationType.ERROR, "incremental.combined.pattern.error.cumulated.reported.increments.negative", i + 1, reportedValues[i], sum]
                    }
                }

                for (int i = 0; i < reportedValues.length; i++) {
                    if (reportedValues[i] < 0 || reportedValues[i] > 1) {
                        return [ValidationType.HINT, "incremental.combined.pattern.error.incremental.reported.values.not.in.unity.interval", i + 1, reportedValues[i]]
                    }
                }

                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] reportedValues = type.incrementalPattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.INCREMENTS_REPORTED)
                double[] payoutValues = type.incrementalPattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.INCREMENTS_PAYOUT)
                double cumulativeReported = 0
                double cumulativePayout = 0
                for (int i = 0; i < reportedValues.length; i++) {
                    cumulativeReported += reportedValues[i]
                    cumulativePayout += payoutValues[i]
                    if (cumulativeReported < cumulativePayout - EPSILON) {
                        return [ValidationType.ERROR, "incremental.combined.pattern.error.reported.smaller.than.payout", i + 1, reportedValues[i], payoutValues[i]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] payoutValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_PAYOUT)
                if (payoutValues.length == 0) {
                    return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.payout.values.empty", payoutValues]
                }
                if (payoutValues[0] < 0) {
                    return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.payout.values.negative", payoutValues[0]]
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] reportedValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_REPORTED)
                if (reportedValues.length == 0) {
                    return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.reported.values.empty", reportedValues]
                }
                if (reportedValues[0] < 0) {
                    return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.reported.values.negative", reportedValues[0]]
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] payoutValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_PAYOUT)
                if (payoutValues.length == 0) return
                if (payoutValues[payoutValues.length - 1] == 1) return true
                [ValidationType.ERROR, "cumulative.combined.pattern.payout.error.last.value.not.one", payoutValues[payoutValues.length - 1]]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] reportedValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_REPORTED)
                if (reportedValues.length == 0) return
                if (reportedValues[reportedValues.length - 1] == 1) return true
                [ValidationType.ERROR, "cumulative.combined.pattern.reported.error.last.value.not.one", reportedValues[reportedValues.length - 1]]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] payoutValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_PAYOUT)
                for (int i = 0; i < payoutValues.length - 1; i++) {
                    if (payoutValues[i + 1] < payoutValues[i]) {
                        return [ValidationType.HINT, "cumulative.combined.pattern.error.cumulative.payout.values.not.increasing", i + 1, i + 2, payoutValues[i], payoutValues[i + 1]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] reportedValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_REPORTED)
                for (int i = 0; i < reportedValues.length - 1; i++) {
                    if (reportedValues[i + 1] < reportedValues[i]) {
                        return [ValidationType.HINT, "cumulative.combined.pattern.error.cumulative.reported.values.not.increasing", i + 1, i + 2, reportedValues[i], reportedValues[i + 1]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] reportedValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_REPORTED)
                double[] payoutValues = type.cumulativePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.CUMULATIVE_PAYOUT)
                for (int i = 0; i < reportedValues.length; i++) {
                    if (reportedValues[i] < payoutValues[i]) {
                        return [ValidationType.ERROR, "cumulative.combined.pattern.error.reported.smaller.than.payout", i + 1, reportedValues[i], payoutValues[i]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] reportedValues = type.ageToAgePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_REPORTED)
                if (reportedValues.length == 0) {
                    return [ValidationType.ERROR, "age.to.age.combined.pattern.error.reported.ratios.empty", reportedValues]
                }

                for (int i = 0; i < reportedValues.length; i++) {
                    if (reportedValues[i] <= 0) {
                        return [ValidationType.ERROR, "age.to.age.combined.pattern.error.reported.ratios.non.positive", i + 1, reportedValues[i]]
                    }
                }

                for (int i = 0; i < reportedValues.length; i++) {
                    if (reportedValues[i] < 1) {
                        return [ValidationType.HINT, "age.to.age.combined.pattern.error.reported.ratios.smaller.one", i + 1, reportedValues[i]]
                    }
                }

                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] payoutValues = type.ageToAgePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_PAYOUT)
                if (payoutValues.length == 0) {
                    return [ValidationType.ERROR, "age.to.age.combined.pattern.error.payout.ratios.empty", payoutValues]
                }

                for (int i = 0; i < payoutValues.length; i++) {
                    if (payoutValues[i] <= 0) {
                        return [ValidationType.ERROR, "age.to.age.combined.pattern.error.payout.ratios.non.positive", i + 1, payoutValues[i]]
                    }
                }

                for (int i = 0; i < payoutValues.length; i++) {
                    if (payoutValues[i] < 1) {
                        return [ValidationType.HINT, "age.to.age.combined.pattern.error.payout.ratios.smaller.one", i + 1, payoutValues[i]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] reportedValues = type.ageToAgePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_REPORTED)
                if (reportedValues.length == 0) return
                if (reportedValues[reportedValues.length - 1] == 1) return true
                [ValidationType.ERROR, "age.to.age.combined.pattern.error.last.reported.ratio.not.one", reportedValues[reportedValues.length - 1]]
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] payoutValues = type.ageToAgePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_PAYOUT)
                if (payoutValues.length == 0) return
                if (payoutValues[payoutValues.length - 1] == 1) return true
                [ValidationType.ERROR, "age.to.age.combined.pattern.error.last.payout.ratio.not.one", payoutValues[payoutValues.length - 1]]
        }


        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] reportedValues = type.ageToAgePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_REPORTED)
                double[] payoutValues = type.ageToAgePattern.getColumnByName(PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_PAYOUT)
                double reportedProduct = 1.0
                double payoutProduct = 1.0
                for (double value in reportedValues) {
                    reportedProduct *= value
                }
                for (double value in payoutValues) {
                    payoutProduct *= value
                }
                double cumulativeReported = 1.0 / reportedProduct
                double cumulativePayout = 1.0 / payoutProduct
                for (int i = 0; i < reportedValues.length - 1; i++) {
                    if (cumulativeReported < cumulativePayout - EPSILON) {
                        return [ValidationType.ERROR, "age.to.age.combined.pattern.error.reported.smaller.than.payout", i + 1, cumulativeReported, cumulativePayout]
                    }
                    cumulativeReported *= reportedValues[i]
                    cumulativePayout *= payoutValues[i]
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
                if (months.length == 0) return
                if (months[0] < 0) {
                    return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.months.not.non-negative", months[0]]
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.CUMULATIVE) {
            Map type ->
                if (type.cumulativePattern.isEmpty()) return
                double[] months = type.cumulativePattern.getColumnByName(PatternTableConstraints.MONTHS)
                for (int i = 0; i < months.length - 1; i++) {
                    if (months[i + 1] <= months[i]) {
                        return [ValidationType.ERROR, "cumulative.combined.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)
                if (months.length == 0) return
                if (months[0] < 0) {
                    return [ValidationType.ERROR, "incremental.combined.pattern.error.cumulative.months.not.non-negative", months[0]]
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.INCREMENTAL) {
            Map type ->
                if (type.incrementalPattern.isEmpty()) return
                double[] months = type.incrementalPattern.getColumnByName(PatternTableConstraints.MONTHS)
                for (int i = 0; i < months.length - 1; i++) {
                    if (months[i + 1] <= months[i]) {
                        return [ValidationType.ERROR, "incremental.combined.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                    }
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)
                if (months.length == 0) return
                if (months[0] < 0) {
                    return [ValidationType.ERROR, "age.to.age.combined.pattern.error.cumulative.months.not.non-negative", months[0]]
                }
                return true
        }

        validationService.register(PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE) {
            Map type ->
                if (type.ageToAgePattern.isEmpty()) return
                double[] months = type.ageToAgePattern.getColumnByName(PatternTableConstraints.MONTHS)
                for (int i = 0; i < months.length - 1; i++) {
                    if (months[i + 1] <= months[i]) {
                        return [ValidationType.ERROR, "age.to.age.combined.pattern.error.cumulative.months.not.strictly.increasing", i + 2, months[i], months[i + 1]]
                    }
                }
                return true
        }
    }

    private static TreeMap<Integer, Double> getCumulativeValuePerMonth(PatternPacket pattern) {
        TreeMap<Integer, Double> valuePerMonth = new TreeMap<Integer, Double>();
        if (pattern == null) {
            valuePerMonth.put(0, 1d)
            return valuePerMonth
        }
        List<Period> periods = pattern.getCumulativePeriods()
        List<Double> values = pattern.getCumulativeValues()
        if (periods == null) return
        for (int i = 0; i < periods.size(); i++) {
            valuePerMonth.put(periods[i].months, values[i])
        }
        if (valuePerMonth.firstKey() > 0) {
            valuePerMonth.put(0, 0)
        }
        return valuePerMonth
    }

    private static PatternPacket getPattern(ParameterHolder parameter) {
        try {
            return parameter.getBusinessObject().getPattern()
        }
        catch (PeriodsNotIncreasingException ex) {
            // this exception is treated by the validator in a different manner than during runtime of a simulation
        }
    }

    private PatternPacket getPayoutPattern(ParameterHolder parameter) {
        try {
            return parameter.getBusinessObject().getPayoutPattern()
        }
        catch (PeriodsNotIncreasingException ex) {
            // this exception is treated by the validator in a different manner than during runtime of a simulation
        }
    }

    private PatternPacket getReportingPattern(ParameterHolder parameter) {
        try {
            return parameter.getBusinessObject().getReportingPattern()
        }
        catch (PeriodsNotIncreasingException ex) {
            // this exception is treated by the validator in a different manner than during runtime of a simulation
        }
    }

}

