package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPeriodDependingThresholdStore {

    void initPeriod(int period);

    double get(BasedOnClaimProperty claimProperty, int occurrencePeriod);
    double get(BasedOnClaimProperty claimProperty, double stabilizationFactor, int occurrencePeriod);

    void set(double threshold, BasedOnClaimProperty claimProperty, int occurrencePeriod);
    void plus(double summand, BasedOnClaimProperty claimProperty, int occurrencePeriod);
}
