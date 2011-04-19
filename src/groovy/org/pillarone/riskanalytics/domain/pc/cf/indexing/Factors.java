package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Factors {

    /** contains absolute factors per date */
    TreeMap<DateTime, Double> factorsPerDate = new TreeMap<DateTime, Double>();

    public Factors() {
    }

    public void add(DateTime date, double factor) {
        factorsPerDate.put(date, factor);
    }

    public Double getFactorAtDate(DateTime date) {
        return factorsPerDate.get(date);
    }

    public Double getFactorFloor(DateTime date) {
        return factorsPerDate.floorEntry(date).getValue();
    }

    public Double getFactorCeiling(DateTime date) {
        return factorsPerDate.ceilingEntry(date).getValue();
    }

    public Double getFactorInterpolated(DateTime date) {
        Map.Entry<DateTime, Double> floorEntry = factorsPerDate.floorEntry(date);
        Map.Entry<DateTime, Double> ceilingEntry = factorsPerDate.ceilingEntry(date);
        if (floorEntry.equals(ceilingEntry)) {
            return floorEntry.getValue();
        }
        else {
//            double elapsedTime = date.minus(floorEntry.getKey().getMillis()).getMillis();
//            double keyDifference = ceilingEntry.getKey().minus(floorEntry.getKey().getMillis()).getMillis();
            double elapsedTime = Days.daysBetween(floorEntry.getKey(), date).getDays();
            double keyDifference = Days.daysBetween(floorEntry.getKey(), ceilingEntry.getKey()).getDays();
            double factorRatio = ceilingEntry.getValue() / floorEntry.getValue();
            return Math.pow(factorRatio, elapsedTime / keyDifference) * floorEntry.getValue();
        }
    }
}
