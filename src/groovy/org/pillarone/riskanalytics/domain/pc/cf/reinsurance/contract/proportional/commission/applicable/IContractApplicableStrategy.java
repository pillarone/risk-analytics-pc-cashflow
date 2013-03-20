package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.applicable;

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public interface IContractApplicableStrategy extends IApplicableStrategy {
    ComboBoxTableMultiDimensionalParameter getApplicableContracts();
}