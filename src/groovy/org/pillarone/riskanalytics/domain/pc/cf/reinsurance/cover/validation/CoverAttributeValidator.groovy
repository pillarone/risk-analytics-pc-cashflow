package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation

import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.structure.StructuringType
import org.pillarone.riskanalytics.domain.utils.validation.AbstractParameterizationValidator

class CoverAttributeValidator extends AbstractParameterizationValidator {
    static final String SAME_CONTRACT_SELECTED = 'cannot.choose.same.contract.for.net.or.ceded'
    static final String IDENTICAL_FILTER = 'multiple.identical.filters.specified'
    static final String IDENTICAL_NET_AND_CEDED_CONTRACTS = 'same.contract.for.net.and.ceded'
    static final String CONTRACT_BENEFITS_ITSELF = 'contract.benefits.itself'

    @Override
    void registerConstraints(AbstractParameterValidationService validationService) {
        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            String currentContractPath = parameters.currentContractPath
            return checkContracts(parameters.flexibleCover, currentContractPath)
        }

        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            return multipleIdenticalFilters(parameters.flexibleCover, false)
        }

        validationService.register(StructuringType.MATRIX) { Map parameters ->
            return multipleIdenticalFilters(parameters.flexibleCover, true)
        }

        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            return checkIdenticalContracts(parameters.flexibleCover)
        }

        validationService.register(CoverAttributeStrategyType.MATRIX) { Map parameters ->
            return checkBenefitContracts(parameters)
        }
    }

    private List checkBenefitContracts(Map parameters) {
        for (int row = parameters?.benefitContracts?.getTitleRowCount(); row < parameters?.benefitContracts?.getRowCount(); row++) {
            if (parameters.currentContractPath.endsWith(parameters.benefitContracts.getValueAt(row,0))) {
                return [ValidationType.ERROR, CONTRACT_BENEFITS_ITSELF]
            }
        }
    }

    @Override
    ParameterObjectParameterHolder verifyParameter(ParameterHolder parameter) {
        if (parameter instanceof ParameterObjectParameterHolder && (parameter.classifier instanceof CoverAttributeStrategyType || parameter.classifier instanceof StructuringType)) {
            return parameter
        }
        return null
    }

    @Override
    protected String getErrorPath(ParameterObjectParameterHolder parameter) {
        super.getErrorPath(parameter) + ':flexibleCover'
    }

    @Override
    protected void setErrorPaths(ParameterObjectParameterHolder parameterToVerify, List<ParameterValidation> currentErrors) {
        currentErrors.each {ParameterValidation error ->
            if (error.msg == CONTRACT_BENEFITS_ITSELF){
                error.path = super.getErrorPath(parameterToVerify) + ':benefitContracts'
            }else {
                error.path = getErrorPath(parameterToVerify)
            }
        }
    }

    @Override
    protected Map getParameterMap(ParameterObjectParameterHolder parameterToVerify) {
        super.getParameterMap(parameterToVerify) + [currentContractPath: parameterToVerify.path - ':parmCover']
    }

    private List checkIdenticalContracts(ConstrainedMultiDimensionalParameter parameter) {
        for (int row = parameter.getTitleRowCount(); row < parameter.getRowCount(); row++) {
            CoverAttributeValidationRow filterRow = new CoverAttributeValidationRow(row, false, parameter)
            if (filterRow.cededContractName && filterRow.netContractName && filterRow.cededContractName == filterRow.netContractName) {
                return [ValidationType.ERROR, IDENTICAL_NET_AND_CEDED_CONTRACTS, normalizeName(filterRow.netContractName)]
            }
        }
        return null
    }

    private List multipleIdenticalFilters(ConstrainedMultiDimensionalParameter parameter, boolean alternativeAggregation) {
        Set<CoverAttributeValidationRow> filters = new HashSet<CoverAttributeValidationRow>();
        for (int row = parameter.getTitleRowCount(); row < parameter.getRowCount(); row++) {
            CoverAttributeValidationRow coverAttributeRow = new CoverAttributeValidationRow(row, alternativeAggregation, parameter)
            if (!filters.add(coverAttributeRow)) {
                return [ValidationType.ERROR, IDENTICAL_FILTER]
            }
        }
        return null

    }

    private List checkContracts(ConstrainedMultiDimensionalParameter parameter, String currentContractPath) {
        if (parameter.getValues().size() < parameter.getColumnCount()) return
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