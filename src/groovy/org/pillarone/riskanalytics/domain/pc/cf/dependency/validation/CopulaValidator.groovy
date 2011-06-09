package org.pillarone.riskanalytics.domain.pc.cf.dependency.validation

import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.core.components.Component

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class CopulaValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(CopulaValidator)
    private static final double EPSILON = 1E-8 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public CopulaValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path                     */
        Map<String, List<Component>> targetComponentsPerCopula = [:]
        /** key: path                     */
        Map<String, IndexStrategyType> strategyPerIndexName = [:]
        /** key: path                     */
        Map<String, ClaimsGeneratorType> strategyPerClaimsGeneratorName = [:]

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof CopulaType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    // todo(jwa): remove if requirement as soon as getRowObjects() is debugged
                    if (!(classifier.equals(CopulaType.NORMAL) || classifier.equals(CopulaType.T))) {
                        List<Component> targets = parameter.getBusinessObject().getTargetComponents()
                        targetComponentsPerCopula[parameter.path] = targets

                        def currentErrors = validationService.validate(classifier, targets)
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                }
                else if (classifier instanceof IndexStrategyType) {
                    strategyPerIndexName[parameter.path[-19..-1] - ':parmIndex'] = classifier
                }
                else if (classifier instanceof ClaimsGeneratorType) {
                    strategyPerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel'] = classifier
                }

                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }


        }
        List<String> listOfCopulaPaths = targetComponentsPerCopula.keySet().toList()
        for (int i = 0; i < listOfCopulaPaths.size() - 1; i++) {
            for (int j = i; j < listOfCopulaPaths; j++) {
                List<Component> targets = targetComponentsPerCopula.get(listOfCopulaPaths[i])
                List<Component> targetsToCompare = targetComponentsPerCopula.get(listOfCopulaPaths[j + 1])
                for (Component target: targets) {
                    for (Component targetToCompare: targetsToCompare) {
                        if (!target.equals(targetToCompare)) continue
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'copula.targets.same.targets.in.different.copulas', [target.getNormalizedName()])
                        error.path = listOfCopulaPaths[j + 1]
                        errors << error
                    }
                }
            }
        }

        for (String copulaPath: targetComponentsPerCopula.keySet()) {
            for (Component target: targetComponentsPerCopula[copulaPath]) {
                IndexStrategyType indexStrategy = strategyPerIndexName[target.getName()]
                ClaimsGeneratorType claimsGeneratorStrategy = strategyPerClaimsGeneratorName[target.getName()]
                if ((indexStrategy == null || indexStrategy.equals(IndexStrategyType.STOCHASTIC)) &&
                        (claimsGeneratorStrategy == null || claimsGeneratorStrategy.equals(ClaimsGeneratorType.ATTRITIONAL) ||
                                claimsGeneratorStrategy.equals(ClaimsGeneratorType.ATTRITIONAL_WITH_DATE))) continue
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'copula.targets.invalid.strategy', [indexStrategy == null ? claimsGeneratorStrategy : indexStrategy])
                error.path = copulaPath
                errors << error
            }
        }

        return errors
    }

    private void registerConstraints() {

        validationService.register(CopulaType) {List type ->
            for (int i = 0; i < type.size() - 1; i++) {
                for (int j = i; j < type.size() - 1; j++) {
                    if (type[i].equals(type[j + 1])) return true
                    [ValidationType.ERROR, "copula.targets.duplicate.reference", type[i]]
                }
            }
        }
    }
}