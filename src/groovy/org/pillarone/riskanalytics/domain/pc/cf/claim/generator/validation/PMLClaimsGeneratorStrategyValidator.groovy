package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PMLClaimsGeneratorStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(PMLClaimsGeneratorStrategyValidator)
    private static final double EPSILON = 1E-6 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public PMLClaimsGeneratorStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ClaimsGeneratorType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    try {
                        def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                    catch (InvalidParameterException ex) {
                        // https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1619
                        LOG.debug("call parameter.getBusinessObject() failed " + ex.toString())
                    }
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }

        return errors
    }

    private void registerConstraints() {

        validationService.register(ClaimsGeneratorType.PML) {Map type ->
            double[] returnPeriods = new double[type.pmlData.getRowCount() - 1];
            int index = type.pmlData.getColumnIndex('return period')

            for (int i = 1; i < type.pmlData.getRowCount(); i++) {
                returnPeriods[i - 1] = InputFormatConverter.getDouble(type.pmlData.getValueAt(i, index))
            }
            if (!returnPeriods) {
                return [ValidationType.ERROR, "pml.strategy.error.returnPeriods.empty"]
            }
            for (int i = 1; i < returnPeriods.length; i++) {
                if (returnPeriods[i - 1] > returnPeriods[i]) {
                    return [ValidationType.ERROR, "pml.strategy.error.returnPeriods.not.increasing", i, returnPeriods[i - 1], returnPeriods[i]]
                }
            }
            for (int i = 0; i < returnPeriods.length; i++) {
                if (returnPeriods[i] <= 0) {
                    return [ValidationType.ERROR, "pml.strategy.error.returnPeriods.strictly.positive", i, returnPeriods[i]]
                }
            }
            return true
        }
        validationService.register(ClaimsGeneratorType.PML) {Map type ->
            double[] claims = new double[type.pmlData.getRowCount() - 1];
            int index = type.pmlData.getColumnIndex('maximum claim')

            for (int i = 1; i < type.pmlData.getRowCount(); i++) {
                claims[i - 1] = InputFormatConverter.getDouble(type.pmlData.getValueAt(i, index))
            }
            if (!claims) {
                return [ValidationType.ERROR, "pml.strategy.error.claims.empty"]
            }
            for (int i = 1; i < claims.length; i++) {
                if (claims[i - 1] > claims[i]) {
                    return [ValidationType.ERROR, "pml.strategy.error.claims.not.increasing", i, claims[i - 1], claims[i]]
                }
            }
            for (int i = 0; i < claims.length; i++) {
                if (claims[i] <= 0) {
                    return [ValidationType.ERROR, "pml.strategy.error.claims.strictly.positive", i, claims[i]]
                }
            }
            return true
        }
    }
}
