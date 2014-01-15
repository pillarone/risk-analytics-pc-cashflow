package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.validation

import org.apache.commons.collections.ComparatorUtils
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class RiskAllocationValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(RiskAllocationValidator)
    private AbstractParameterValidationService validationService

    public RiskAllocationValidator() {
        validationService = new ParameterValidationServiceImpl()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []
        Map<String, RiskAllocatorType> associateExposureInfoPerClaimsGenerator = [:]
        Map<String, Boolean> underwritingInfoPerClaimsGenerator = [:]
        Map<String, List<String>> riskBandsByClaimsGenerator = [:]
        List<String> riskBandsWithoutSumInsuredInformation = []

        for (ParameterHolder parameter in parameters) {
            if (parameter.path.contains('underwritingSegments') && parameter.path.contains('parmUnderwritingInformation')) {
                List<Double> maxSumInsured = parameter.value.values[0]
                List<Double> sumInsured = parameter.value.values[1]
                if (maxSumInsured.contains(0d) || sumInsured.contains(0d)) {
                    riskBandsWithoutSumInsuredInformation << parameter.path.split(':')[1]
                }
            }
            if (parameter instanceof ParameterObjectParameterHolder && parameter.classifier instanceof RiskAllocatorType) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug "validating ${parameter.path}"
                }
                associateExposureInfoPerClaimsGenerator[parameter.path - ':parmAssociateExposureInfo'] = (RiskAllocatorType) parameter.classifier
            }
            else if (parameter instanceof MultiDimensionalParameterHolder && parameter.value instanceof ComboBoxTableMultiDimensionalParameter) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug "validating ${parameter.path}"
                }
                if (parameter.path.contains('claimsGenerators:sub') && parameter.value.markerClass.is(IUnderwritingInfoMarker)) {
                    if (hasSelectedUnderwritingInfo(parameter.value)) {
                        underwritingInfoPerClaimsGenerator[parameter.path - ':parmUnderwritingSegments'] = true
                        List<String> selectedRiskBandsNames = ((ComboBoxTableMultiDimensionalParameter) parameter.value).values.get(0)
                        riskBandsByClaimsGenerator[parameter.path - ':parmUnderwritingSegments'] = selectedRiskBandsNames
                    }
                }
            }
        }

        for (String claimsGeneratorPath: associateExposureInfoPerClaimsGenerator.keySet()) {
            RiskAllocatorType allocatorType = associateExposureInfoPerClaimsGenerator[claimsGeneratorPath]
            boolean hasSelectedUnderwritingInfo = underwritingInfoPerClaimsGenerator[claimsGeneratorPath]
            List<String> associatedRiskBands = riskBandsByClaimsGenerator[claimsGeneratorPath]
            if (!allocatorType.equals(RiskAllocatorType.NONE)) {
                if (!hasSelectedUnderwritingInfo) {
                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'associate.exposure.info.requires.underwriting.info', [allocatorType])
                    errors << error
                    error.path = claimsGeneratorPath + ':parmAssociateExposureInfo'
                    error = new ParameterValidationImpl(ValidationType.ERROR,
                        'associate.exposure.info.requires.underwriting.info', [allocatorType])
                    errors << error
                    error.path = claimsGeneratorPath + ':parmUnderwritingSegments'
                } else {
                    for (String riskBandsName : associatedRiskBands) {
                        if (riskBandsWithoutSumInsuredInformation.contains(riskBandsName)) {
                            ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                                'associate.exposure.info.requires.non.trivial.sum.insured', [ComponentUtils.getNormalizedName(riskBandsName)])
                            errors << error
                            error.path = claimsGeneratorPath + ':parmAssociateExposureInfo'
                        }
                    }
                }
            }
        }
        return errors
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

