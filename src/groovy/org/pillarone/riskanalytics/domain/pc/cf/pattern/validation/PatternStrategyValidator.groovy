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

        /** key: path             */
        Map<String, PatternPacket> payoutPatterns = [:]
        /** key: path             */
        Map<String, PatternPacket> reportingPatterns = [:]
        /** key: path             */
        Map<String, String> reportingPatternPerClaimsGenerator = [:]
        /** key: path             */
        Map<String, String> payoutPatternPerClaimsGenerator = [:]


        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof PatternStrategyType && !parameter.path.contains("patterns:subRecoveryPatterns")) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    if (parameter.path.contains("patterns:subPayoutPatterns:")) {
                        payoutPatterns[parameter.path - 'patterns:subPayoutPatterns:' - ':parmPattern'] = parameter.getBusinessObject().getPattern()
                    }
                    if (parameter.path.contains("patterns:subReportingPatterns:")) {
                        reportingPatterns[parameter.path - 'patterns:subReportingPatterns:' - ':parmPattern'] = parameter.getBusinessObject().getPattern()
                    }

                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }

            else if (parameter instanceof ConstrainedStringParameterHolder && parameter.path.contains("claimsGenerators")) {
                if (parameter.path.contains("parmReportingPattern")) {
                    reportingPatternPerClaimsGenerator[parameter.path - ':parmReportingPattern'] = parameter.value.getStringValue()
                }
                else if (parameter.path.contains("parmPayoutPattern")) {
                    payoutPatternPerClaimsGenerator[parameter.path - ':parmPayoutPattern'] = parameter.value.getStringValue()
                }

            }
        }

        for (String claimsGeneratorPath: reportingPatternPerClaimsGenerator.keySet()) {
            PatternPacket reportingPattern = reportingPatterns[reportingPatternPerClaimsGenerator[claimsGeneratorPath]]
            PatternPacket payoutPattern = payoutPatterns[payoutPatternPerClaimsGenerator[claimsGeneratorPath]]
            TreeMap<Integer, Double> reportingValuesPerMonth = getCumulativeValuePerMonth(reportingPattern)
            TreeMap<Integer, Double> payoutValuesPerMonth = getCumulativeValuePerMonth(payoutPattern)
            for (Map.Entry<Integer, Double> payoutEntry: payoutValuesPerMonth.entrySet()) {
                if (payoutEntry.value <= reportingValuesPerMonth.floorEntry(payoutEntry.key).value) continue
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'claims.generator.reporting.pattern.smaller.than.payout.pattern',
                        [payoutEntry.key, payoutEntry.value, reportingValuesPerMonth.floorEntry(payoutEntry.key).value])
                error.path = claimsGeneratorPath + ':parmPayoutPattern'
                errors << error
            }
        }

        return errors
    }

    private void registerConstraints() {

        validationService.register(PatternStrategyType.INCREMENTAL) {Map type ->
            double[] values = type.incrementalPattern.getColumnByName(PatternStrategyType.INCREMENTS)
            double sum = (double) GroovyCollections.sum(values)
            if (sum >= 1.0 - EPSILON && sum <= 1.0 + EPSILON) return true
            [ValidationType.ERROR, "incremental.pattern.error.sum.not.one", sum]
        }

        validationService.register(PatternStrategyType.INCREMENTAL) {Map type ->
            double[] values = type.incrementalPattern.getColumnByName(PatternStrategyType.INCREMENTS)
            if (values.length == 0) {
                return [ValidationType.ERROR, "incremental.pattern.error.incremental.values.empty", values]
            }

            for (int i = 0; i < values.length; i++) {
                if (values[i] < 0 || values[i] > 1) {
                    return [ValidationType.ERROR, "incremental.pattern.error.incremental.values.not.in.unity.interval", i + 1, values[i]]
                }
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            if (values.length == 0) {
                return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.empty", values]
            }
            if (values[0] < 0) {
                return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.negative", values[0]]
            }
            return true
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            if (values[values.length - 1] == 1) return true
            [ValidationType.ERROR, "cumulative.pattern.error.last.value.not.one", values[values.length - 1]]
        }

        validationService.register(PatternStrategyType.CUMULATIVE) {Map type ->
            double[] values = type.cumulativePattern.getColumnByName(PatternStrategyType.CUMULATIVE2)
            for (int i = 0; i < values.length - 1; i++) {
                if (values[i + 1] < values[i]) {
                    return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.values.not.increasing", i + 1, values[i], values[i + 1]]
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
                    return [ValidationType.ERROR, "cumulative.pattern.error.cumulative.months.not.strictly.increasing", i + 1, months[i], months[i + 1]]
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
                    return [ValidationType.ERROR, "incremental.pattern.error.cumulative.months.not.strictly.increasing", i + 1, months[i], months[i + 1]]
                }
            }
            return true
        }
    }

    private TreeMap<Integer, Double> getCumulativeValuePerMonth(PatternPacket pattern) {
        TreeMap<Integer, Double> valuePerMonth = new TreeMap<Integer, Double>();
        if (pattern == null) {
            valuePerMonth.put(0, 1d)
            return valuePerMonth
        }
        List<Period> periods = pattern.getCumulativePeriods()
        List<Double> values = pattern.getCumulativeValues()
        for (int i = 0; i < periods.size(); i++) {
            valuePerMonth.put(periods[i].months, values[i])
        }
        if (valuePerMonth.firstKey() > 0) {
            valuePerMonth.put(0, 0)
        }
        return valuePerMonth
    }

}

