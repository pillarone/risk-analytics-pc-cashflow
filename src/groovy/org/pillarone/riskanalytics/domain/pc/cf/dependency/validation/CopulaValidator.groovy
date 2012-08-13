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

        /** key: path                                   */
        Map<String, List<Component>> targetComponentsPerCopula = [:]
        /** key: path                                   */
        Map<String, IndexStrategyType> strategyPerIndexName = [:]
        /** key: path                                   */
        Map<String, ClaimsGeneratorType> strategyPerClaimsGeneratorName = [:]

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof CopulaType && parameter.path.contains('dependencies:sub')) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    List<Component> targets = parameter.getBusinessObject().getTargetComponents()
                    targetComponentsPerCopula[parameter.path] = targets

                    def currentErrors = validationService.validate(classifier, targets)
                    currentErrors*.path = parameter.path + pathExtension(classifier)
                    errors.addAll(currentErrors)
                }
                else if (classifier instanceof IndexStrategyType) {
                    if (parameter.path.contains('indices:subFrequencyIndices:')) {
                        strategyPerIndexName[parameter.path - 'indices:subFrequencyIndices:' - ':parmIndex'] = classifier
                    }
                    else if (parameter.path.contains('indices:subRunOffIndices:')) {
                        strategyPerIndexName[parameter.path - 'indices:subRunOffIndices:' - ':parmIndex'] = classifier
                    }

                    else if (parameter.path.contains('indices:subPolicyIndices:')) {
                        strategyPerIndexName[parameter.path - 'indices:subPolicyIndices:' - ':parmIndex'] = classifier
                    }
                    else if (parameter.path.contains('indices:subPremiumIndices:')) {
                        strategyPerIndexName[parameter.path - 'indices:subPremiumyIndices:' - ':parmIndex'] = classifier
                    }

                    else if (parameter.path.contains('indices:subReservesIndices:')) {
                        strategyPerIndexName[parameter.path - 'indices:subReservesIndices:' - ':parmIndex'] = classifier
                    }

                    else if (parameter.path.contains('discountings:sub')) {
                        strategyPerIndexName[parameter.path - 'discountings:' - ':parmIndex'] = classifier
                    }
                }
                else if (classifier instanceof ClaimsGeneratorType) {
                    strategyPerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel'] = classifier
                }

                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }


        }

        List<String> listOfCopulaPaths = targetComponentsPerCopula.keySet().toList()
        for (int i = 0; i < listOfCopulaPaths.size() - 1; i++) {
            for (int j = i; j < listOfCopulaPaths.size(); j++) {
                List<Component> targets = targetComponentsPerCopula.get(listOfCopulaPaths[i])
                List<Component> targetsToCompare = targetComponentsPerCopula.get(listOfCopulaPaths[j + 1])
                for (Component target: targets) {
                    for (Component targetToCompare: targetsToCompare) {
                        if (!target.equals(targetToCompare)) continue
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'dependencies.copula.same.targets.in.different.copulas', [target.getNormalizedName()])
                        error.path = listOfCopulaPaths[i]
                        errors << error
                        error = new ParameterValidationImpl(ValidationType.ERROR,
                                'dependencies.copula.same.targets.in.different.copulas', [target.getNormalizedName()])
                        error.path = listOfCopulaPaths[j + 1]
                        errors << error
                    }
                }
            }
        }

        for (String copulaPath: targetComponentsPerCopula.keySet()) {
            for (Component target: targetComponentsPerCopula[copulaPath]) {
                IndexStrategyType indexType = strategyPerIndexName[target.getName()]
                ClaimsGeneratorType claimsGeneratorType = strategyPerClaimsGeneratorName[target.getName()]
                if ((indexType == null || indexType.equals(IndexStrategyType.STOCHASTIC)) &&
                        (claimsGeneratorType == null || claimsGeneratorType.equals(ClaimsGeneratorType.ATTRITIONAL) ||
                                claimsGeneratorType.equals(ClaimsGeneratorType.ATTRITIONAL_WITH_DATE))) {
                    continue
                }
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'dependencies.copula.targets.invalid.strategy', [target.getNormalizedName(),
                                indexType == null ? claimsGeneratorType.toString() : indexType.toString()])
                error.path = copulaPath + ':type'
                errors << error
            }
        }

        return errors
    }

    private void registerConstraints() {

        validationService.register(CopulaType) {List type ->
            for (int i = 0; i < type.size() - 1; i++) {
                for (int j = i; j < type.size() - 1; j++) {
                    if (type[i].equals(type[j + 1])) {
                        return [ValidationType.ERROR, "dependencies.copula.targets.duplicate.reference", type[i].normalizedName]
                    }
                }
            }
            return true
        }
    }

    private String pathExtension(IParameterObjectClassifier classifier) {
        if (classifier.equals(CopulaType.NORMAL) || classifier.equals(CopulaType.T)) {
            return ':dependencyMatrix'
        }
        else {
            return ':targets'
        }
    }
}