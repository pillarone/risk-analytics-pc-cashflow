package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation

import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.IClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractSingleClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.FrequencySeverityClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.FrequencyAverageAttritionalClaimsGeneratorStrategy
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGeneratorStrategy

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class ClaimsGeneratorScalingValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(ClaimsGeneratorScalingValidator)
    private static final double EPSILON = 1E-8 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public ClaimsGeneratorScalingValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path                                              */
        Map<String, IClaimsGeneratorStrategy> claimsGeneratorStrategyPerClaimsGeneratorName = [:]
        /** key:       */
        Map<String, Boolean> underwritingInfoPerClaimsGeneratorName = [:]

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ClaimsGeneratorType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }

                    claimsGeneratorStrategyPerClaimsGeneratorName[parameter.path - ':parmClaimsModel'] = (IClaimsGeneratorStrategy) parameter.getBusinessObject()

                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                }


                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
            else if (parameter instanceof MultiDimensionalParameterHolder && parameter.value instanceof ComboBoxTableMultiDimensionalParameter) {
                if (parameter.path.contains('claimsGenerators:sub') && parameter.value.markerClass.is(IUnderwritingInfoMarker)) {
                    underwritingInfoPerClaimsGeneratorName[parameter.path - ':parmUnderwritingSegments'] = hasSelectedUnderwritingInfo(parameter.value)
                }
            }
        }

        for (String claimsGeneratorName: claimsGeneratorStrategyPerClaimsGeneratorName.keySet()) {

            IClaimsGeneratorStrategy strategy = claimsGeneratorStrategyPerClaimsGeneratorName[claimsGeneratorName]
            boolean hasSelectedUnderwritingInfo = underwritingInfoPerClaimsGeneratorName[claimsGeneratorName]
            if (strategy instanceof FrequencySeverityClaimsGeneratorStrategy
                    || strategy instanceof FrequencyAverageAttritionalClaimsGeneratorStrategy) {
                if (!strategy.frequencyBase.equals(FrequencyBase.ABSOLUTE) && !hasSelectedUnderwritingInfo) {
                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.WARNING,
                            'frequency.base.requires.underwriting.info', [strategy.frequencyBase.toString()])
                    errors << error
                    error.path = claimsGeneratorName + ':parmClaimsModel:frequencyBase'
                    error = new ParameterValidationImpl(ValidationType.WARNING,
                            'frequency.base.requires.underwriting.info', [strategy.frequencyBase.toString()])
                    errors << error
                    error.path = claimsGeneratorName + ':parmUnderwritingSegments'
                }
            }
            if (!strategy.claimsSizeBase.equals(ExposureBase.ABSOLUTE) && !hasSelectedUnderwritingInfo) {
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.WARNING,
                        'claims.size.base.requires.underwriting.info', [strategy.claimsSizeBase.toString()])
                errors << error
                error.path = claimsGeneratorName + ':parmClaimsModel:claimsSizeBase'
                error = new ParameterValidationImpl(ValidationType.WARNING,
                        'claims.size.base.requires.underwriting.info', [strategy.claimsSizeBase.toString()])
                errors << error
                error.path = claimsGeneratorName + ':parmUnderwritingSegments'
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(ClaimsGeneratorType.ATTRITIONAL) {Map type ->
            ExposureBase base = type.claimsSizeBase
            if (!base.equals(ExposureBase.NUMBER_OF_POLICIES)) return true
            [ValidationType.WARNING, "claims.generator.model.attritional.critical.exposure.base", base.toString()]
        }

        validationService.register(ClaimsGeneratorType.ATTRITIONAL_WITH_DATE) {Map type ->
            ExposureBase base = type.claimsSizeBase
            if (!base.equals(ExposureBase.NUMBER_OF_POLICIES)) return true
            [ValidationType.WARNING, "claims.generator.model.attritional.critical.exposure.base", base.toString()]
        }

        validationService.register(ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL) {Map type ->
            ExposureBase base = type.claimsSizeBase
            if (!base.equals(ExposureBase.NUMBER_OF_POLICIES)) return true
            [ValidationType.WARNING, "claims.generator.model.average.attritional.critical.exposure.base", base.toString()]
        }

        validationService.register(ClaimsGeneratorType.FREQUENCY_SEVERITY) {Map type ->
            FrequencySeverityClaimType claimsType = type.produceClaim
            ExposureBase base = type.claimsSizeBase
            FrequencyBase frequencyBase = type.frequencyBase
            if (claimsType.equals(FrequencySeverityClaimType.SINGLE)) {
                if (base.equals(ExposureBase.ABSOLUTE)) return true
                return [ValidationType.ERROR, "claims.generator.model.frequency.severity.claim.type.single.exposure.base.not.absolute", claimsType.toString(), base.toString()]
            }
            if (claimsType.equals(FrequencySeverityClaimType.AGGREGATED_EVENT)) {
                if (frequencyBase.equals(FrequencyBase.ABSOLUTE)) return true
                return [ValidationType.ERROR, "claims.generator.model.frequency.severity.claim.type.event.frequency.base.not.absolute", claimsType.toString(), frequencyBase.toString()]
            }
        }

        validationService.register(ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY) {Map type ->
            FrequencySeverityClaimType claimsType = type.produceClaim
            ExposureBase base = type.claimsSizeBase
            FrequencyBase frequencyBase = type.frequencyBase
            if (claimsType.equals(FrequencySeverityClaimType.SINGLE)) {
                if (base.equals(ExposureBase.ABSOLUTE)) return true
                return [ValidationType.ERROR, "claims.generator.model.frequency.severity.claim.type.single.exposure.base.not.absolute", claimsType.toString(), base.toString()]
            }
            if (claimsType.equals(FrequencySeverityClaimType.AGGREGATED_EVENT)) {
                if (frequencyBase.equals(FrequencyBase.ABSOLUTE)) return true
                return [ValidationType.ERROR, "claims.generator.model.frequency.severity.claim.type.event.frequency.base.not.absolute", claimsType.toString(), frequencyBase.toString()]
            }
        }

        validationService.register(ClaimsGeneratorType.PML) {Map type ->
            FrequencySeverityClaimType claimsType = type.produceClaim
            ExposureBase base = type.claimsSizeBase
            if (claimsType.equals(FrequencySeverityClaimType.SINGLE)) {
                if (base.equals(ExposureBase.ABSOLUTE)) return true
                return [ValidationType.ERROR, "claims.generator.model.pml.claim.type.single.exposure.base.not.absolute", claimsType.toString(), base.toString()]
            }
        }


    }

    boolean hasSelectedUnderwritingInfo(ComboBoxTableMultiDimensionalParameter parameter) {
        if (parameter.values.empty) {
            return false
        }

        List content = parameter.values[0] instanceof List ? parameter.values[0] : parameter.values

        if (content.empty) {
            return false
        }

        return content[0] instanceof String && content[0].length() > 0
    }

}