package org.pillarone.riskanalytics.domain.pc.cf.dependency.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.core.simulation.item.parameter.EnumParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class MultipleProbabilitiesCopulaValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(MultipleProbabilitiesCopulaValidator)
    private static final double EPSILON = 1E-8 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public MultipleProbabilitiesCopulaValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path                                */
        Map<String, List<Component>> targetComponentsPerCopula = [:]
        /** key: name of component                                */
        Map<String, ClaimsGeneratorType> strategyPerClaimsGeneratorName = [:]
        /** key: path of copula                               */
        Map<String, FrequencyDistributionType> frequencyDistributionPerCopula = [:]
        /** key: name of component                                */
        Map<String, FrequencyDistributionType> frequencyDistributionPerClaimsGeneratorName = [:]
        /** key: name of component                                */
        Map<String, DistributionModifier> frequencyModificationPerClaimsGeneratorName = [:]
        /** key: name of component                                */
        Map<String, FrequencyBase> frequencyBasePerClaimsGeneratorName = [:]

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof CopulaType && parameter.path.contains('eventGenerators:sub')) {
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
                else if (classifier instanceof ClaimsGeneratorType) {
                    strategyPerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel'] = classifier
                }
                else if (classifier instanceof FrequencyDistributionType && parameter.path.contains('eventGenerators:sub')) {
                    frequencyDistributionPerCopula[parameter.path - 'parmFrequencyDistribution' + 'parmCopulaStrategy'] = classifier
                }
                else if (classifier instanceof FrequencyDistributionType && parameter.path.contains('claimsGenerators:sub')) {
                    frequencyDistributionPerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel:frequencyDistribution'] = classifier
                }
                else if (classifier instanceof DistributionModifier && parameter.path.contains('claimsGenerators:sub')) {
                    frequencyModificationPerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel:frequencyModification'] = classifier
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
            else if (parameter instanceof EnumParameterHolder && parameter.value instanceof FrequencyBase) {
                if (parameter.path.contains('claimsGenerators:sub')) {
                    frequencyBasePerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel:frequencyBase'] = parameter.value
                }
            }


        }

        for (String copulaPath: targetComponentsPerCopula.keySet()) {
            for (Component target: targetComponentsPerCopula[copulaPath]) {
                String claimsGeneratorName = target.getName()
                ClaimsGeneratorType claimsGeneratorStrategy = strategyPerClaimsGeneratorName[claimsGeneratorName]
                if (claimsGeneratorStrategy.equals(ClaimsGeneratorType.FREQUENCY_SEVERITY)
                        || claimsGeneratorStrategy.equals(ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY)
                        || claimsGeneratorStrategy.equals(ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL)) {
                    FrequencyDistributionType frequencyDistributionTypeClaims = frequencyDistributionPerClaimsGeneratorName[claimsGeneratorName]
                    FrequencyDistributionType frequencyDistributionTypeEvents = frequencyDistributionPerCopula[copulaPath]
                    DistributionModifier modifier = frequencyModificationPerClaimsGeneratorName[claimsGeneratorName]
                    FrequencyBase base = frequencyBasePerClaimsGeneratorName[claimsGeneratorName]
                    if (!frequencyDistributionTypeClaims.equals(frequencyDistributionTypeEvents)) {
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.distribution', [frequencyDistributionTypeClaims.toString(), frequencyDistributionTypeEvents.toString()])
                        error.path = copulaPath // todo(jwa): probably claims generator path better (additonally?)
                        errors << error
                    } else {
                        // todo(jwa): validate control parameter and mean and difference values, PMO-1605
                    }
                    if (!base.equals(FrequencyBase.ABSOLUTE)){
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.base', [base.toString()])
                        error.path = copulaPath // todo(jwa): probably claims generator path better (additonally?)
                        errors << error
                    }
                    if (!modifier.equals(DistributionModifier.NONE)){
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.modifier', [modifier.toString()])
                        error.path = copulaPath // todo(jwa): probably claims generator path better (additonally?)
                        errors << error
                    }

                    continue
                }
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'event.generators.copula.targets.invalid.strategy', [claimsGeneratorStrategy.toString()])
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
                    [ValidationType.ERROR, "event.generators.copula.targets.duplicate.reference", type[i].getNormalizedName()]
                }
            }
        }
    }

}