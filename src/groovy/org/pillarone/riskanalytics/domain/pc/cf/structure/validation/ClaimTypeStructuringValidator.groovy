package org.pillarone.riskanalytics.domain.pc.cf.structure.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder

import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.pc.cf.structure.StructuringType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimTypeStructuringValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(ClaimTypeStructuringValidator)
    private AbstractParameterValidationService validationService

    public RiskAllocationValidator() {
        validationService = new ParameterValidationServiceImpl()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []
        List<String> supportedClaimTypes = ['ATTRITIONAL', 'SINGLE', 'AGGREGATED_EVENT', 'AGGREGATED_RESERVES']

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder
                    && parameter.classifier instanceof StructuringType
                    && parameter.classifier.equals(StructuringType.CLAIMTYPES)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug "validating ${parameter.path}"
                }
                for (String claimType : parameter.classifierParameters["claimTypes"].value.values[0]) {
                    if (!supportedClaimTypes.contains(claimType)) {
                        ParameterValidationImpl warning = new ParameterValidationImpl(ValidationType.WARNING,
                            'structure.claimType.not.supported.in.GIRAModel', [claimType.toLowerCase()])
                        warning.path = parameter.getPath()
                        errors << warning
                    }
                }
            }
        }
        return errors
    }
}