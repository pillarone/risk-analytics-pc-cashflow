package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;

/**
 * Helper class
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DateFactors {

    private DateTime date;
    private double factorIncremental;
    private double factorCumulated;

    public DateFactors(DateTime date, double factorIncremental, double factorCumulated) {
        this.date = date;
        this.factorIncremental = factorIncremental;
        this.factorCumulated = factorCumulated;
    }

    public DateTime getDate() {
        return date;
    }

    public double getFactorIncremental() {
        return factorIncremental;
    }

    public double getFactorCumulated() {
        return factorCumulated;
    }
}
