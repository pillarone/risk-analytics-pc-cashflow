package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

/**
 * This implementation should be used for threshold parameters equal 0. All implementations are trivial.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialPeriodDependingThresholdStore implements IPeriodDependingThresholdStore {
    @Override
    public void initPeriod(int period) {
    }

    @Override
    public double get(BasedOnClaimProperty claimProperty, int occurrencePeriod) {
        return 0;
    }

    @Override
    public double get(BasedOnClaimProperty claimProperty, double stabilizationFactor, int occurrencePeriod) {
        return 0;
    }

    @Override
    public void set(double threshold, BasedOnClaimProperty claimProperty, int occurrencePeriod) {
    }

    @Override
    public void plus(double summand, BasedOnClaimProperty claimProperty, int occurrencePeriod) {
    }
}
