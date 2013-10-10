package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.lang.StringBuilder;

/**
 * Similar functionality as in DoubleValue but available value is resetted to initialValue for every new annual period
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DoubleValuePerPeriod implements Serializable{

    public double initialValue;
    private double value;
    public int lastUsedPeriod;

    public DoubleValuePerPeriod() {
    }

    public DoubleValuePerPeriod(double initialValue) {
        this.initialValue = initialValue;
    }

    public void plus(double summand) {
        value += summand;
    }

    public void minus(double summand) {
        value -= summand;
    }

    public double getValue(DateTime updateDate) {
        int period = updateDate.getYear();
        if (period > lastUsedPeriod) {
            value = initialValue;
            lastUsedPeriod = period;
        }
        return value;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("initialValue: ").append(initialValue).append(", value: ").append(value).toString();
    }

}
