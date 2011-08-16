package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.validation

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
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

        for (ParameterHolder parameter in parameters) {
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
                    underwritingInfoPerClaimsGenerator[parameter.path - ':parmUnderwritingSegments'] = hasSelectedUnderwritingInfo(parameter.value)
                }
            }
        }

        for (String claimsGeneratorPath: associateExposureInfoPerClaimsGenerator.keySet()) {
            RiskAllocatorType allocatorType = associateExposureInfoPerClaimsGenerator[claimsGeneratorPath]
            boolean hasSelectedUnderwritingInfo = underwritingInfoPerClaimsGenerator[claimsGeneratorPath]
            if (!allocatorType.equals(RiskAllocatorType.NONE) && !hasSelectedUnderwritingInfo) {
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'associate.exposure.info.requires.underwriting.info', [allocatorType])
                errors << error
                error.path = claimsGeneratorPath + ':parmAssociateExposureInfo'
                error = new ParameterValidationImpl(ValidationType.ERROR,
                        'associate.exposure.info.requires.underwriting.info', [allocatorType])
                errors << error
                error.path = claimsGeneratorPath + ':parmUnderwritingSegments'
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

