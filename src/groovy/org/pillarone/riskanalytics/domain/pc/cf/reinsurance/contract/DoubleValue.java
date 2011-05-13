package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DoubleValue {

    public double value;

    public void plus(Double summand) {
        value += summand;
    }

    public void minus(Double summand) {
        value -= summand;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
