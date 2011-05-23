package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPropReinsuranceContract extends IReinsuranceContract {

    void calculateCommission();
}
