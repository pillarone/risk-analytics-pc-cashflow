package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.IBoundaryIndexStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.StopLossBoundaryIndexApplication
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.StopLossBoundaryIndexType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.StopLossIndexedBoundaryIndexStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.StopLossBase
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
 * This validator checks consistency of limit, aggregate limit and reinstatement premium parameters.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ContractStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(ContractStrategyValidator)

    private AbstractParameterValidationService validationService

    public ContractStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ReinsuranceContractType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    try {
                        def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                    catch (IllegalArgumentException ex) {
                    }
                }
            }
        }
        return errors
    }

    private void registerConstraints() {
        validationService.register(ReinsuranceContractType.CXL) {Map type ->
            return correctAggregateLimit(type, 'limit', 'aggregateLimit')
        }
        validationService.register(ReinsuranceContractType.CXL) {Map type ->
            return correctReinstatementPremium(type)
        }
        validationService.register(ReinsuranceContractType.WXL) {Map type ->
            return correctAggregateLimit(type, 'limit', 'aggregateLimit')
        }
        validationService.register(ReinsuranceContractType.WXL) {Map type ->
            return correctReinstatementPremium(type)
        }
        validationService.register(ReinsuranceContractType.WCXL) {Map type ->
            return correctAggregateLimit(type, 'limit', 'aggregateLimit')
        }
        validationService.register(ReinsuranceContractType.WCXL) {Map type ->
            return correctReinstatementPremium(type)
        }
        validationService.register(ReinsuranceContractType.GOLDORAK) {Map type ->
            return correctAggregateLimit(type, 'cxlLimit', 'cxlAggregateLimit')
        }
        validationService.register(ReinsuranceContractType.STOPLOSS) {Map type ->
            if (type['stopLossContractBase'].equals(StopLossBase.GNPI)) {
                IBoundaryIndexStrategy boundaryIndex = type['boundaryIndex']
                if (boundaryIndex instanceof StopLossIndexedBoundaryIndexStrategy) {
                    if (boundaryIndex.getType().equals(StopLossBoundaryIndexType.INDEXED)
                        && ((StopLossIndexedBoundaryIndexStrategy) boundaryIndex).getIndexedValues().equals(StopLossBoundaryIndexApplication.ATTACHMENT_POINT_LIMIT_PREMIUM)) {
                        return [ValidationType.WARNING, "stoploss.gnpi.boundaryIndex.mismatch"]
                    }
                }
            }
        }
    }

    private def correctAggregateLimit(Map type, String limit, String aggregateLimit) {
        if (type.size() == 0 || type[limit] == null || type[aggregateLimit] == null) return
        if (type[limit] > type[aggregateLimit]) {
            return [ValidationType.ERROR, "aggregateLimit.lower.than.limit", type[aggregateLimit], type[limit]]
        }
        else if (type[limit] == 0 && type[aggregateLimit] > 0) {
            return [ValidationType.ERROR, "aggregateLimit.and.limit.zero", type[aggregateLimit], type[limit]]
        }
        return
    }

    private def correctReinstatementPremium(Map type) {
        if (type.size() == 0 || type['reinstatementPremiums'] == null
                || (!(type['reinstatementPremiums'] instanceof ConstrainedMultiDimensionalParameter))) return
        Double aggregateLimitParameter = (Double) type['aggregateLimit']
        Double limit = (Double) type['limit']
        if (aggregateLimitParameter == null || limit == null) return
        ConstrainedMultiDimensionalParameter reinstatementPremiums = (ConstrainedMultiDimensionalParameter) type['reinstatementPremiums']
        int numberOfReinstatementsBasedOnSpecifiedRIPremium = reinstatementPremiums.valueRowCount
        boolean freeReinstatements = numberOfReinstatementsBasedOnSpecifiedRIPremium == 1 && reinstatementPremiums.getValueAt(1, 0) == 0
        if (freeReinstatements) return
        double numberOfReinstatements = !limit ? 0 : aggregateLimitParameter / limit - 1  // -1 as the aggregate limit contains the base layer
        double aggregateLimitCalculated = (numberOfReinstatementsBasedOnSpecifiedRIPremium + 1) * limit
//        int usableReinstatements = (aggregateLimitParameter - limit) / limit
        if (numberOfReinstatementsBasedOnSpecifiedRIPremium < numberOfReinstatements) {
            return [ValidationType.ERROR, "mismatching.reinstatement.premiums.and.aggregate.limit.beyond", aggregateLimitParameter, aggregateLimitCalculated]
        }
        else if (numberOfReinstatementsBasedOnSpecifiedRIPremium > Math.ceil(numberOfReinstatements)) {
            return [ValidationType.ERROR, "mismatching.reinstatement.premiums.and.aggregate.limit.below",
                    numberOfReinstatements, numberOfReinstatementsBasedOnSpecifiedRIPremium,
                    aggregateLimitParameter, aggregateLimitCalculated]
        }
        return
    }
}


