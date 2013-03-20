package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICommissionParameterStrategy extends IParameterObject {

    /**
     * @param numberOfPeriods normally corresponds to number of covered periods
     * @return key: period
     */
    Map<Integer, ICommissionStrategy> getCommissionPerPeriod(int numberOfPeriods);

    /** this method is used in order to map to the GIRA calculation methods */
    ICommission getCommission(int period, int numberOfPeriods);
}
