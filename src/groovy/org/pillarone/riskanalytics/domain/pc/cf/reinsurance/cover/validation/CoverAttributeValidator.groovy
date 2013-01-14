package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl

class CoverAttributeValidator implements IParameterizationValidator {
    static final String SAME_CONTRACT_SELECTED = 'cannot.choose.same.contract.for.net.or.ceded'
    private Log LOG = LogFactory.getLog(CoverAttributeValidator)

    private AbstractParameterValidationService validationService

    public CoverAttributeValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }


    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder && parameter.classifier instanceof CoverAttributeStrategyType) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug "validating ${parameter.path}"
                }
                def currentErrors = validationService.validate(parameter.classifier, [currentContractPath: parameter.path - ':parmCover'] + parameter.getParameterMap())
                currentErrors*.path = parameter.path
                errors.addAll(currentErrors)

            }
        }
        return errors
    }

    private void registerConstraints() {
        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            String currentContractPath = parameters.currentContractPath
            ConstrainedMultiDimensionalParameter coverParameter = parameters.flexibleCover
            return checkContracts(coverParameter.getValuesAsObjects(CoverMap.CONTRACT_NET_OF_COLUMN_INDEX), coverParameter.getValuesAsObjects(CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX), currentContractPath)
        }
    }

    private List checkContracts(List<ReinsuranceContract> netContracts, List<ReinsuranceContract> cededContracts, String currentContractPath) {
        ReinsuranceContract conflictingContract = null
        netContracts?.each { ReinsuranceContract contract ->
            if (currentContractPath.endsWith(contract.name)) {
                conflictingContract = contract
            }
        }
        cededContracts?.each { ReinsuranceContract contract ->
            if (currentContractPath.endsWith(contract.name)) {
                conflictingContract = contract
            }
        }
        return conflictingContract ? [ValidationType.ERROR, SAME_CONTRACT_SELECTED, conflictingContract.normalizedName] : null
    }
}