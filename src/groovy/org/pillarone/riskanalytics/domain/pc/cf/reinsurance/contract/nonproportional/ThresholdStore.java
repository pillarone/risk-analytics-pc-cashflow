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
    private double thresholdReported;
    private double thresholdPaid;

    public ThresholdStore(double threshold) {
        this.threshold = threshold;
        init();
    }

    public void init() {
        thresholdStabilizedCumulated = threshold;
        thresholdUltimate = threshold;
        thresholdReported = threshold;
        thresholdPaid = threshold;
    }

    public double get(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return thresholdUltimate;
            case REPORTED:
                return thresholdReported;
            case PAID:
                return thresholdPaid;
        }
        throw new NotImplementedException(claimProperty.toString());
    }

    public double get(BasedOnClaimProperty claimProperty, double stabilizationFactor) {
        if (stabilizationFactor == 1) return get(claimProperty);
        double thresholdStabilized = threshold * stabilizationFactor;
        switch (claimProperty) {
            case ULTIMATE:
                thresholdUltimate = thresholdStabilized - (threshold - thresholdUltimate);
                return thresholdUltimate;
            case REPORTED:
                thresholdReported = thresholdStabilized - (threshold - thresholdReported);
                return thresholdReported;
            case PAID:
                double oldThresholdPaid = thresholdPaid;
                thresholdPaid = thresholdStabilized - (threshold - thresholdPaid);
                // updated for paid only as this property is used for reinstatement premium calculation
                thresholdStabilizedCumulated += thresholdPaid - oldThresholdPaid;
                return thresholdPaid;
        }
        throw new NotImplementedException(claimProperty.toString());
    }

    public void set(double threshold, BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                thresholdUltimate = threshold;
                break;
            case REPORTED:
                thresholdReported = threshold;
                break;
            case PAID:
                thresholdPaid = threshold;
                break;
        }
    }

    public void plus(double summand, BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                thresholdUltimate += summand;
                break;
            case REPORTED:
                thresholdReported += summand;
                break;
            case PAID:
                thresholdPaid += summand;
                break;
        }
    }

    public double threshold() { return threshold; }
    public double thresholdStabilized() { return thresholdStabilizedCumulated; }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ultimate: ");
        buffer.append(thresholdUltimate);
        buffer.append(", reported: ");
        buffer.append(thresholdReported);
        buffer.append(", paid: ");
        buffer.append(thresholdPaid);
        return buffer.toString();
    }
}
