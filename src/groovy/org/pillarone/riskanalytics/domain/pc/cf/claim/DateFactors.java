package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;

/**
 * Helper class for PatternPacket users
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 *
 * Compare DateFactors by date.
 */
public class DateFactors implements Comparable<DateFactors> {

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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(date);
        result.append(SEPARATOR);
        result.append(factorIncremental);
        result.append(SEPARATOR);
        result.append(factorCumulated);
        return result.toString();
    }

    public int compareTo(DateFactors o) {
        return o.getDate().compareTo(this.getDate());
    }

    private static final String SEPARATOR = ", ";
}
