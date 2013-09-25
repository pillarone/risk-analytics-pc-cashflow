package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl

/**
 * This validator checks that legal entity cells are not null
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CounterPartyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(CounterPartyValidator)

    private AbstractParameterValidationService validationService

    public CounterPartyValidator() {
        validationService = new ParameterValidationServiceImpl()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                if (parameter.value instanceof ConstrainedMultiDimensionalParameter && parameter.value.constraints instanceof LegalEntityPortionConstraints) {
                    List<ILegalEntityMarker> reinsurers = parameter.value.getColumn(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX)
                    List<Integer> linesWithMissingReinsurer = []
                    for (int i = 0; i < reinsurers.size(); i++) {
                        if (reinsurers[i] == null || reinsurers[i] == "") {
                            linesWithMissingReinsurer << i + 1      // as line numbers start with 1
                        }
                    }
                    if (!(linesWithMissingReinsurer.empty)) {
                        ParameterValidation error = validationService.createErrorObject(ValidationType.ERROR,
                            linesWithMissingReinsurer.size() == 1 ? 'no.reinsurer.defined' : 'no.reinsurers.defined', [linesWithMissingReinsurer.join(', ')])
                        errors << error
                        error.path = parameter.path
                    }
                }
            }
        }
        return errors
    }


}


