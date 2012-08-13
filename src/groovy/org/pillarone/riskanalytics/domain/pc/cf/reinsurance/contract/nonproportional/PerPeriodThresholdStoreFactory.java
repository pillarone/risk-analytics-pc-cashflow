package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.apache.commons.lang.NotImplementedException;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class PerPeriodThresholdStoreFactory {

    public static IPeriodDependingThresholdStore getPeriodThresholdStore(double threshold) {
        if (threshold == 0) {
            return new TrivialPeriodDependingThresholdStore();
        }
        else if (threshold > 0) {
            return new EqualUsagePerPeriodThresholdStore(threshold);
        }
        else {
            throw new NotImplementedException("no strategy for negative threshold available");
        }
    }
}
