package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FactorsPacket extends Packet {

    /**
     * contains absolute factors per date
     */
    TreeMap<DateTime, Double> factorsPerDate = new TreeMap<DateTime, Double>();

    public FactorsPacket() {
    }

    public FactorsPacket(DateTime date, double factor) {
        add(date, factor);
    }

    public void add(DateTime date, double factor) {
        factorsPerDate.put(date, factor);
    }

    public Double getFactorAtDate(DateTime date) {
        Double factor = factorsPerDate.get(date);
        return factor == null ? 1 : factor;
    }

    public Double getFactorFloor(DateTime date) {
        Map.Entry<DateTime, Double> dateTimeDoubleEntry = factorsPerDate.floorEntry(date);
        return dateTimeDoubleEntry == null ? 1 : dateTimeDoubleEntry.getValue();
    }

    public Double getFactorCeiling(DateTime date) {
        Map.Entry<DateTime, Double> dateTimeDoubleEntry = factorsPerDate.ceilingEntry(date);
        return dateTimeDoubleEntry == null ? 1 : dateTimeDoubleEntry.getValue();
    }

    public Double getFactorInterpolated(DateTime date) {
        Map.Entry<DateTime, Double> floorEntry = factorsPerDate.floorEntry(date);
        Map.Entry<DateTime, Double> ceilingEntry = factorsPerDate.ceilingEntry(date);
        if (floorEntry.equals(ceilingEntry)) {
            return floorEntry.getValue();
        } else if (ceilingEntry == null) {
            ceilingEntry = floorEntry;
            floorEntry = factorsPerDate.floorEntry(date.minusDays(1));
        } else if (floorEntry == null) {
            floorEntry = ceilingEntry;
            ceilingEntry = factorsPerDate.higherEntry(date);
        }
        double elapsedTime = Days.daysBetween(floorEntry.getKey(), date).getDays();
        double keyDifference = Days.daysBetween(floorEntry.getKey(), ceilingEntry.getKey()).getDays();
        double factorRatio = ceilingEntry.getValue() / floorEntry.getValue();
        return Math.pow(factorRatio, elapsedTime / keyDifference) * floorEntry.getValue();
    }
}
