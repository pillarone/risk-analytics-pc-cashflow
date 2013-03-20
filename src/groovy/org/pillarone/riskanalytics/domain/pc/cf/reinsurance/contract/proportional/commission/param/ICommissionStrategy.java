package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * Specific commission strategies are used in two types of components: Commission and ReinsuranceContract.
 * The isAdditive parameter of calculateCommission should be true for commissions, but false for reinsurance contracts.
 *
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public interface ICommissionStrategy extends IParameterObject {

    ICommission getCalculator(DoubleValuePerPeriod lossCarryForward);

    DoubleValuePerPeriod getInitialLossCarriedForward();
}
