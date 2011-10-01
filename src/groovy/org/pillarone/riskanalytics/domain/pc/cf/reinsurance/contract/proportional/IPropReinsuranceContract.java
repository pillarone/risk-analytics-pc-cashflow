package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;

/**
 * This interface is used for decision on GNPI calculations too.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPropReinsuranceContract extends IReinsuranceContract {

    void calculateCommission();
    ProportionalPremiumBase premiumBase();
}
