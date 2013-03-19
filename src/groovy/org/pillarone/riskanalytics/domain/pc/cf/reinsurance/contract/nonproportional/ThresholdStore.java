package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

/**
 * Usable for shared attachment point and limits (period/term)
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ThresholdStore {

    private double threshold;
    private double thresholdStabilizedCumulated;

    private double thresholdUltimate;
    private double thresholdUltimateIndexed;
    private double thresholdReported;
    private double thresholdPaid;

    public ThresholdStore(double threshold) {
        this.threshold = threshold;
        init();
    }

    public void init() {
        thresholdStabilizedCumulated = threshold;
        thresholdUltimate = threshold;
        thresholdUltimateIndexed = threshold;
        thresholdReported = threshold;
        thresholdPaid = threshold;
    }

    public double get(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return thresholdUltimate;
            case ULTIMATE_INDEXED:
                return thresholdUltimateIndexed;
            case REPORTED:
                return thresholdReported;
            case PAID:
                return thresholdPaid;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double get(BasedOnClaimProperty claimProperty, double stabilizationFactor) {
        if (stabilizationFactor == 1) return get(claimProperty);
        double thresholdStabilized = threshold * stabilizationFactor;
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                thresholdUltimate = thresholdStabilized - (threshold - thresholdUltimate);
                return thresholdUltimate;
            case ULTIMATE_INDEXED:
                thresholdUltimateIndexed = thresholdStabilized - (threshold - thresholdUltimateIndexed);
                return thresholdUltimateIndexed;
            case REPORTED:
                thresholdReported = thresholdStabilized - (threshold - thresholdReported);
                return thresholdReported;
            case PAID:
                double oldThresholdPaid = thresholdPaid;
                thresholdPaid = thresholdStabilized - (threshold - thresholdPaid);
                // updated for paid only as this property is used for reinstatement premium calculation
                thresholdStabilizedCumulated += thresholdPaid - oldThresholdPaid;
                return thresholdPaid;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public void set(double threshold, BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                thresholdUltimate = threshold;
                break;
            case ULTIMATE_INDEXED:
                thresholdUltimateIndexed = threshold;
                break;
            case REPORTED:
                thresholdReported = threshold;
                break;
            case PAID:
                thresholdPaid = threshold;
                break;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public void plus(double summand, BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                thresholdUltimate += summand;
                break;
            case ULTIMATE_INDEXED:
                thresholdUltimateIndexed += summand;
                break;
            case REPORTED:
                thresholdReported += summand;
                break;
            case PAID:
                thresholdPaid += summand;
                break;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double threshold() { return threshold; }
    public double thresholdStabilized() { return thresholdStabilizedCumulated; }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ultimate: ");
        builder.append(thresholdUltimate);
        builder.append(", reported: ");
        builder.append(thresholdReported);
        builder.append(", paid: ");
        builder.append(thresholdPaid);
        return builder.toString();
    }
}
