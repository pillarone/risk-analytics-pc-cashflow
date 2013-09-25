package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl

/**
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnifiedADCLPTStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(UnifiedADCLPTStrategyValidator)
    private static String PROJECTION_START_DATE = "globalParameters:parmProjectionStartDate"
    private static String PATH_SEPARATOR = ':'

    private AbstractParameterValidationService validationService

    public UnifiedADCLPTStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []
        DateTime projectionStartDate
        for (ParameterHolder parameter: parameters) {
            if (LOG.isDebugEnabled()) {
                LOG.debug "validating ${parameter.path}"
            }
            if (parameter.path.equals(PROJECTION_START_DATE)) {
                projectionStartDate = parameter.value
            }
        }

        if (projectionStartDate) {
            List<String> retroContractPaths = []
            for (ParameterHolder parameter : parameters) {
                if (parameter instanceof ParameterObjectParameterHolder && parameter.classifier.equals(ReinsuranceContractType.UNIFIEDADCLPT)) {
                    int lastPathSeparator = parameter.path.lastIndexOf(PATH_SEPARATOR)
                    retroContractPaths << parameter.path.substring(0, lastPathSeparator)
                }
            }

            if (!(retroContractPaths.empty)) {
                for (ParameterHolder parameter in parameters) {
                    if (parameter instanceof ParameterObjectParameterHolder) {
                        IParameterObjectClassifier classifier = parameter.getClassifier()
                        if (classifier instanceof PeriodStrategyType) {
                            int lastPathSeparator = parameter.path.lastIndexOf(PATH_SEPARATOR)
                            if (retroContractPaths.contains(parameter.path.substring(0, lastPathSeparator))) {
                                DateTime endOfCover = ((IPeriodStrategy) parameter.getBusinessObject()).getEndCover()
                                if (endOfCover.isBefore(projectionStartDate)) {
                                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
                                    ParameterValidation error = validationService.createErrorObject(ValidationType.ERROR,
                                        'retro.cover.ends.before.projection.start', [endOfCover.toString(formatter), projectionStartDate.toString(formatter)])
                                    errors << error
                                    error.path = parameter.path
                                }
                            }
                        }
                    }
                }
            }
        }
        return errors
    }
}