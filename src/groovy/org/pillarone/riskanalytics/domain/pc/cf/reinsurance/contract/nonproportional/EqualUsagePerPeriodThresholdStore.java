package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Usable for shared attachment point and limits (period/term). Compared with the ThresholdStore implementation, this
 * specific implementation makes sure, that reported and paid of a specific contract period don't exceed the ultimate of
 * the same contract period.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EqualUsagePerPeriodThresholdStore {

    /** original threshold */
    private double threshold;

    /** Contains the remaining ultimate threshold by contract period. The remaining ultimate threshold is zero for
     *  past period, as it is moved forward to the next period. */
    private List<Double> usedThresholdPerPeriodUltimate = new ArrayList<Double>();
    /** Contains the remaining reported threshold by contract period */
    private List<Double> usedThresholdPerPeriodReported = new ArrayList<Double>();
    /** Contains the remaining paid threshold by contract period */
    private List<Double> usedThresholdPerPeriodPaid = new ArrayList<Double>();

    public EqualUsagePerPeriodThresholdStore(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Elements of usedThresholdPerPeriod* are first set to the remaining ultimate threshold at the beginning of the period.
     * The remaining threshold for reported and paid of the last period is reduced according to the effectively used
     * ultimate threshold of the period.
     * @param period
     */
    public void initPeriod(int period) {
        if (usedThresholdPerPeriodReported.size() <= period) {
            initPeriod(BasedOnClaimProperty.ULTIMATE, period);
            initPeriod(BasedOnClaimProperty.REPORTED, period);
            initPeriod(BasedOnClaimProperty.PAID, period);
            if (period > 0) {
                // remove any remaining ultimate threshold of former periods as it has been moved to following periods
                usedThresholdPerPeriodUltimate.set(period - 1, 0d);
            }
        }
    }

    /**
     * <ul>
     *     <li>re-initialized the private list corresponding to claimProperty for every iteration with the threshold</li>
     *     <li>copy the remaining ultimate of the former period to this period</li>
     *     <li>reduce the reported/paid threshold according to the used ultimate threshold of the former period</li>
     *     <li>initialize the reported/paid period threshold with the ultimate threshold</li>
     * </ul>
     * @param claimProperty
     * @param period
     */
    private void initPeriod(BasedOnClaimProperty claimProperty, int period) {
        // period == 0 is not sufficient for contracts with cover starting after the projection start period
        // use the paid instance of the lists as it is the last to be updated
        if (usedThresholdPerPeriodPaid.size() == 0 || usedThresholdPerPeriodPaid.size() > period) {
            getUsedThreshold(claimProperty).clear();
            for (int i = 0; i < period; i++) {
                getUsedThreshold(claimProperty).add(0d);
            }
            getUsedThreshold(claimProperty).add(threshold);
        }
        else {
            switch (claimProperty) {
                case ULTIMATE:
                    usedThresholdPerPeriodUltimate.add(get(claimProperty, period - 1));
                    break;
                case REPORTED:
                case PAID:
                    if (usedThresholdPerPeriodUltimate.size() - 1 == usedThresholdPerPeriodPaid.size()) {
                        // reduce threshold of the period to avoid reported/paid claims of this contract period exceeding the ultimate
                        plus(-usedThresholdPerPeriodUltimate.get(period - 1), claimProperty, period - 1);
                        // restrict threshold of next period to ultimate threshold of next period
                        getUsedThreshold(claimProperty).add(usedThresholdPerPeriodUltimate.get(period));
                    }
                    else {
                        throw new UnsupportedOperationException("Function has to be called first for ultimate!");
                    }
                    break;
                default:
                    throw new NotImplementedException(claimProperty.toString());
            }
        }
    }

    public double get(BasedOnClaimProperty claimProperty, int occurrencePeriod) {
        return getUsedThreshold(claimProperty).get(occurrencePeriod);
    }
    
    private List<Double> getUsedThreshold(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return usedThresholdPerPeriodUltimate;
            case REPORTED:
                return usedThresholdPerPeriodReported;
            case PAID:
                return usedThresholdPerPeriodPaid;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double get(BasedOnClaimProperty claimProperty, double stabilizationFactor, int period) {
        if (stabilizationFactor == 1) return get(claimProperty, period);
        double thresholdStabilized = threshold * stabilizationFactor;
        double adjustedThreshold = thresholdStabilized - (threshold - getUsedThreshold(claimProperty).get(period));
        getUsedThreshold(claimProperty).set(period, adjustedThreshold);
        return adjustedThreshold;
    }

    public void plus(double summand, BasedOnClaimProperty claimProperty, int period) {
        if (claimProperty.equals(BasedOnClaimProperty.ULTIMATE)) {
            // sanity check
            int last = usedThresholdPerPeriodReported.size() - 1;
            if (period != last) {
                throw new IllegalArgumentException("Updating ultimate threshold of previous periods is not allowed!");
            }
        }
        getUsedThreshold(claimProperty).set(period, getUsedThreshold(claimProperty).get(period) + summand);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ultimate: ");
        builder.append(usedThresholdPerPeriodUltimate);
        builder.append(", reported: ");
        builder.append(usedThresholdPerPeriodReported);
        builder.append(", paid: ");
        builder.append(usedThresholdPerPeriodPaid);
        return builder.toString();
    }
}
