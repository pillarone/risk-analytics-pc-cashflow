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
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.IClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.FrequencySeverityClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.OccurrenceFrequencySeverityClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.FrequencyAverageAttritionalClaimsGeneratorStrategy
import umontreal.iro.lecuyer.probdist.Distribution

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

        /** key: path                                              */
        Map<String, List<Component>> targetComponentsPerCopula = [:]
        /** key: name of component                                              */
        Map<String, IClaimsGeneratorStrategy> claimsGeneratorStrategyPerClaimsGeneratorName = [:]
        /** key: path of copula                                             */
        Map<String, RandomFrequencyDistribution> frequencyDistributionPerCopula = [:]

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
                    claimsGeneratorStrategyPerClaimsGeneratorName[parameter.path - 'claimsGenerators:' - ':parmClaimsModel'] = (IClaimsGeneratorStrategy) parameter.getBusinessObject()
                }
                else if (classifier instanceof FrequencyDistributionType && parameter.path.contains('eventGenerators:sub')) {
                    frequencyDistributionPerCopula[parameter.path - 'parmFrequencyDistribution' + 'parmCopulaStrategy'] = (RandomFrequencyDistribution) parameter.getBusinessObject()
                }

                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }

        for (String copulaPath: targetComponentsPerCopula.keySet()) {
            for (Component target: targetComponentsPerCopula[copulaPath]) {
                String claimsGeneratorName = target.getName()
                IClaimsGeneratorStrategy strategy = claimsGeneratorStrategyPerClaimsGeneratorName[claimsGeneratorName]
                if (strategy instanceof FrequencySeverityClaimsGeneratorStrategy || strategy instanceof OccurrenceFrequencySeverityClaimsGeneratorStrategy
                        || strategy instanceof FrequencyAverageAttritionalClaimsGeneratorStrategy) {
                    RandomFrequencyDistribution frequencyDistributionTotal = strategy.frequencyDistribution
                    RandomFrequencyDistribution frequencyDistributionSystematic = frequencyDistributionPerCopula[copulaPath]

                    if (!frequencyDistributionTotal.getType().equals(frequencyDistributionSystematic.getType())) {
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.distribution',
                                [frequencyDistributionTotal.getType().toString(), frequencyDistributionSystematic.getType().toString()])
                        error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution'
                        errors << error

                        error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.distribution',
                                [frequencyDistributionTotal.getType().toString(), frequencyDistributionSystematic.getType().toString()])
                        error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution'
                        errors << error
                    }
                    else {
                        switch (frequencyDistributionTotal.getType()) {
                            case FrequencyDistributionType.CONSTANT:
                                if (frequencyDistributionTotal.distribution.constant < frequencyDistributionSystematic.distribution.constant) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.frequency.distribution.total.constant.smaller.than.systematic.constant',
                                            [frequencyDistributionTotal.distribution.constant, frequencyDistributionSystematic.distribution.constant])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:constant'
                                    errors << error
                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.frequency.distribution.total.constant.smaller.than.systematic.constant',
                                            [frequencyDistributionTotal.distribution.constant, frequencyDistributionSystematic.distribution.constant])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:constant'
                                    errors << error
                                }
                                break
                            case FrequencyDistributionType.CONSTANTS:
                                if (frequencyDistributionTotal.distribution.sortedValues[0] < frequencyDistributionSystematic.distribution.sortedValues[-1]) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.frequency.distribution.total.min.observation.smaller.than.systematic.max.observation',
                                            [frequencyDistributionTotal.distribution.sortedValues[0], frequencyDistributionSystematic.distribution.sortedValues[-1]])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:constants'
                                    errors << error

                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.frequency.distribution.total.min.observation.smaller.than.systematic.max.observation',
                                            [frequencyDistributionTotal.distribution.sortedValues[0], frequencyDistributionSystematic.distribution.sortedValues[-1]])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:constants'
                                    errors << error
                                }
                                break
                            case FrequencyDistributionType.BINOMIALDIST:
                                if (frequencyDistributionTotal.distribution.p != frequencyDistributionSystematic.distribution.p) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.binomial.distribution.total.p.not.equal.to.systematic.p',
                                            [frequencyDistributionTotal.distribution.p, frequencyDistributionSystematic.distribution.p])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:p'
                                    errors << error
                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.binomial.distribution.total.p.not.equal.to.systematic.p',
                                            [frequencyDistributionTotal.distribution.p, frequencyDistributionSystematic.distribution.p])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:p'
                                    errors << error
                                }

                                if (frequencyDistributionTotal.distribution.n < frequencyDistributionSystematic.distribution.n) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.binomial.distribution.total.n.smaller.than.systematic.n',
                                            [frequencyDistributionTotal.distribution.n, frequencyDistributionSystematic.distribution.n])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:n'
                                    errors << error

                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.binomial.distribution.total.n.smaller.than.systematic.n',
                                            [frequencyDistributionTotal.distribution.n, frequencyDistributionSystematic.distribution.n])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:n'
                                    errors << error

                                }
                                break
                            case FrequencyDistributionType.NEGATIVEBINOMIAL:
                                if (frequencyDistributionTotal.distribution.p != frequencyDistributionSystematic.distribution.p) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.negative.binomial.distribution.total.p.not.equal.to.systematic.p',
                                            [frequencyDistributionTotal.distribution.p, frequencyDistributionSystematic.distribution.p])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:p'
                                    errors << error

                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.negative.binomial.distribution.total.p.not.equal.to.systematic.p',
                                            [frequencyDistributionTotal.distribution.p, frequencyDistributionSystematic.distribution.p])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:p'
                                    errors << error
                                }
                                if (frequencyDistributionTotal.distribution.gamma < frequencyDistributionSystematic.distribution.gamma) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.negative.binomial.distribution.total.gamma.smaller.than.systematic.gamma',
                                            [frequencyDistributionTotal.distribution.gamma, frequencyDistributionSystematic.distribution.gamma])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:gamma'
                                    errors << error
                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.negative.binomial.distribution.total.gamma.smaller.than.systematic.gamma',
                                            [frequencyDistributionTotal.distribution.gamma, frequencyDistributionSystematic.distribution.gamma])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:gamma'
                                    errors << error
                                }

                                break
                            case FrequencyDistributionType.POISSON:
                                if (frequencyDistributionTotal.distribution.lambda < frequencyDistributionSystematic.distribution.lambda) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.poisson.distribution.total.lambda.smaller.than.systematic.lambda',
                                            [frequencyDistributionTotal.distribution.lambda, frequencyDistributionSystematic.distribution.lambda])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:lambda'
                                    errors << error
                                     error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.poisson.distribution.total.lambda.smaller.than.systematic.lambda',
                                            [frequencyDistributionTotal.distribution.lambda, frequencyDistributionSystematic.distribution.lambda])
                                    error.path =  'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:lambda'
                                    errors << error
                                }
                                break
                            case FrequencyDistributionType.DISCRETEEMPIRICAL:
                                List<Double> obsTotal = new ArrayList<Double>()
                                for (int i = 0; i < frequencyDistributionTotal.distribution.pr.length; i++) {
                                    if (frequencyDistributionTotal.distribution.pr[i] > 0.0) {
                                        obsTotal.add(frequencyDistributionTotal.distribution.obs[i])
                                    }
                                }
                                List<Double> obsSystematic = new ArrayList<Double>()
                                for (int i = 0; i < frequencyDistributionSystematic.distribution.pr.length; i++) {
                                    if (frequencyDistributionSystematic.distribution.pr[i] > 0.0) {
                                        obsSystematic.add(frequencyDistributionSystematic.distribution.obs[i])
                                    }
                                }

                                if (obsTotal.min() < obsSystematic.max()) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.dicrete.empirical.distribution.total.min.observation.smaller.than.systematic.max.observation',
                                            [obsTotal.min(), obsSystematic.max()])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:discreteEmpiricalValues'
                                    errors << error
                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.dicrete.empirical.distribution.total.min.observation.smaller.than.systematic.max.observation',
                                            [obsTotal.min(), obsSystematic.max()])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:discreteEmpiricalValues'
                                    errors << error
                                }
                                break
                            case FrequencyDistributionType.DISCRETEEMPIRICALCUMULATIVE:
                                List<Double> obsTotal = new ArrayList<Double>()
                                for (int i = 0; i < frequencyDistributionTotal.distribution.pr.length; i++) {
                                    if (frequencyDistributionTotal.distribution.pr[i] > 0.0) {
                                        obsTotal.add(frequencyDistributionTotal.distribution.obs[i])
                                    }
                                }
                                List<Double> obsSystematic = new ArrayList<Double>()
                                for (int i = 0; i < frequencyDistributionSystematic.distribution.pr.length; i++) {
                                    if (frequencyDistributionSystematic.distribution.pr[i] > 0.0) {
                                        obsSystematic.add(frequencyDistributionSystematic.distribution.obs[i])
                                    }
                                }

                                if (obsTotal.min() < obsSystematic.max()) {
                                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.discrete.empirical.cumulative.distribution.total.min.observation.smaller.than.systematic.max.observation',
                                            [obsTotal.min(), obsSystematic.max()])
                                    error.path = copulaPath - 'parmCopulaStrategy' + 'parmFrequencyDistribution:discreteEmpiricalCumulativeValues'
                                    errors << error
                                    error = new ParameterValidationImpl(ValidationType.ERROR,
                                            'event.generators.discrete.empirical.cumulative.distribution.total.min.observation.smaller.than.systematic.max.observation',
                                            [obsTotal.min(), obsSystematic.max()])
                                    error.path = 'claimsGenerators:' + claimsGeneratorName + ':parmClaimsModel:frequencyDistribution:discreteEmpiricalCumulativeValues'
                                    errors << error
                                }
                                break
                        }
                    }
                    if (!strategy.frequencyBase.equals(FrequencyBase.ABSOLUTE)) {
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.base', [strategy.frequencyBase.toString()])
                        error.path = copulaPath // todo(jwa): also inking of claims generator path
                        errors << error
                    }
                    if (!strategy.frequencyModification.type.equals(DistributionModifier.NONE)) {
                        ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'event.generators.copula.targets.invalid.frequency.modifier', [strategy.frequencyModification.type.toString()])
                        error.path = copulaPath // todo(jwa): also inking of claims generator path
                        errors << error
                    }
                }
                else {
                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                            'event.generators.copula.targets.invalid.strategy', [strategy.toString()])
                    error.path = copulaPath // todo(jwa): inking of claims generator path
                    errors << error
                }
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