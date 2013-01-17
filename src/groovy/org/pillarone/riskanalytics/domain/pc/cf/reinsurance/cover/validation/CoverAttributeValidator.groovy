package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.ComponentUtils
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
            CoverAttributeValidationRow filterRow = new CoverAttributeValidationRow(row, parameter)
            if (filterRow.cededContractName && filterRow.netContractName && filterRow.cededContractName == filterRow.netContractName){
                return [ValidationType.ERROR, IDENTICAL_NET_AND_CEDED_CONTRACTS,normalizeName(filterRow.netContractName)]
            }
        }
        return null
    }

    private List multipleIdenticalFilters(ConstrainedMultiDimensionalParameter parameter) {
        Set<CoverAttributeValidationRow> filters = new HashSet<CoverAttributeValidationRow>();
        for (int row = parameter.getTitleRowCount(); row < parameter.getRowCount(); row++) {
            CoverAttributeValidationRow coverAttributeRow = new CoverAttributeValidationRow(row, parameter)
            if (!filters.add(coverAttributeRow)) {
                return [ValidationType.ERROR, IDENTICAL_FILTER]
            }
        }
        return null

    }

    private List checkContracts(ConstrainedMultiDimensionalParameter parameter, String currentContractPath) {
        List<String> netContracts = parameter.getColumn(CoverMap.CONTRACT_NET_OF_COLUMN_INDEX)
        List<String> cededContracts = parameter.getColumn(CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX)
        String conflictingContractName = null
        netContracts?.each { contractName ->
            if (contractName && currentContractPath.endsWith(contractName)) {
                conflictingContractName = contractName
            }
        }
        cededContracts?.each { contractName ->
            if (contractName && currentContractPath.endsWith(contractName)) {
                conflictingContractName = contractName
            }
        }
        return conflictingContractName ? [ValidationType.ERROR, SAME_CONTRACT_SELECTED, normalizeName(conflictingContractName)] : null
    }

    private String normalizeName(String conflictingContractName) {
        ComponentUtils.getNormalizedName(conflictingContractName)
    }
}