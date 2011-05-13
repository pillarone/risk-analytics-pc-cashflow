package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.applicable;


import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;

import java.util.*;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ContractApplicableStrategy extends AbstractParameterObject implements IContractApplicableStrategy {

    private ComboBoxTableMultiDimensionalParameter applicableContracts = new ComboBoxTableMultiDimensionalParameter(
        Collections.emptyList(), COLUMN_TITLES, IReinsuranceContractMarker.class);

    public IParameterObjectClassifier getType() {
        return ApplicableStrategyType.CONTRACT;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("applicableContracts", applicableContracts);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getApplicableContracts() {
        return applicableContracts;
    }

    public static final List<String> COLUMN_TITLES = Arrays.asList("Applicable Contracts");
}