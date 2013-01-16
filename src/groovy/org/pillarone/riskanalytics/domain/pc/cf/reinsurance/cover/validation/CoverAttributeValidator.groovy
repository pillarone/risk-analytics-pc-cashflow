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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeRow
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl

class CoverAttributeValidator implements IParameterizationValidator {
    static final String SAME_CONTRACT_SELECTED = 'cannot.choose.same.contract.for.net.or.ceded'
    static final String IDENTICAL_FILTER = 'multiple.identical.filters.specified'
    static final String IDENTICAL_NET_AND_CEDED_CONTRACTS = 'same.contract.for.net.and.ceded'
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
            return checkContracts(parameters.flexibleCover, currentContractPath)
        }

        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            return multipleIdenticalFilters(parameters.flexibleCover)
        }

        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            return checkIdenticalContracts(parameters.flexibleCover)
        }
    }

    private List checkIdenticalContracts(ConstrainedMultiDimensionalParameter parameter) {
        for (int row = parameter.getTitleRowCount(); row < parameter.getRowCount(); row++) {
            MatrixCoverAttributeRow filterRow = new MatrixCoverAttributeRow(row, parameter)
            if (filterRow.cededContract && filterRow.netContract && filterRow.cededContract == filterRow.netContract){
                return [ValidationType.ERROR, IDENTICAL_NET_AND_CEDED_CONTRACTS,filterRow.cededContract.normalizedName]
            }
        }
        return null
    }

    private List multipleIdenticalFilters(ConstrainedMultiDimensionalParameter parameter) {
        Set<MatrixCoverAttributeRow> filters = new HashSet<MatrixCoverAttributeRow>();
        for (int row = parameter.getTitleRowCount(); row < parameter.getRowCount(); row++) {
            MatrixCoverAttributeRow coverAttributeRow = new MatrixCoverAttributeRow(row, parameter)
            if (!filters.add(coverAttributeRow)) {
                return [ValidationType.ERROR, IDENTICAL_FILTER]
            }
        }
        return null

    }

    private List checkContracts(ConstrainedMultiDimensionalParameter parameter, String currentContractPath) {
        List<ReinsuranceContract> netContracts = parameter.getValuesAsObjects(CoverMap.CONTRACT_NET_OF_COLUMN_INDEX)
        List<ReinsuranceContract> cededContracts = parameter.getValuesAsObjects(CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX)
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