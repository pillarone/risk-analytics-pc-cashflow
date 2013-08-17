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

    private TreeMap<DateTime, Double> factorsPerDate = new TreeMap<DateTime, Double>();

    public FactorsPacket() {
    }

    public FactorsPacket(DateTime date, double factor) {
        add(date, factor);
    }

    public void add(DateTime date, double factor) {
        getFactorsPerDate().put(date, factor);
    }

    public Double getFactorAtDate(DateTime date) {
        Double factor = getFactorsPerDate().get(date);
        return factor == null ? 1 : factor;
    }

    public Double getFactorFloor(DateTime date) {
        Map.Entry<DateTime, Double> dateTimeDoubleEntry = getFactorsPerDate().floorEntry(date);
        return dateTimeDoubleEntry == null ? getFactorsPerDate().firstEntry().getValue() : dateTimeDoubleEntry.getValue();
    }

    public Double getFactorCeiling(DateTime date) {
        Map.Entry<DateTime, Double> dateTimeDoubleEntry = getFactorsPerDate().ceilingEntry(date);
        return dateTimeDoubleEntry == null ? getFactorsPerDate().lastEntry().getValue() : dateTimeDoubleEntry.getValue();
    }

    public Double getFactorInterpolated(DateTime date) {
        Map.Entry<DateTime, Double> floorEntry = getFactorsPerDate().floorEntry(date);
        Map.Entry<DateTime, Double> ceilingEntry = getFactorsPerDate().ceilingEntry(date);

        if (getFactorsPerDate().size() == 1) {
            if (floorEntry != null) {
                return floorEntry.getValue();
            }
            else if (ceilingEntry != null) {
                return ceilingEntry.getValue();
            }
        }
        if (ceilingEntry == null) {
            ceilingEntry = floorEntry;
            floorEntry = getFactorsPerDate().floorEntry(floorEntry.getKey().minusDays(1));
        }
        else if (floorEntry == null) {
            floorEntry = ceilingEntry;
            ceilingEntry = getFactorsPerDate().ceilingEntry(floorEntry.getKey().plusDays(1));
        }
        else if (floorEntry.equals(ceilingEntry)) {
            return floorEntry.getValue();
        }
        
        double elapsedTime = Days.daysBetween(floorEntry.getKey(), date).getDays();
        double keyDifference = Days.daysBetween(floorEntry.getKey(), ceilingEntry.getKey()).getDays();
        double factorRatio = ceilingEntry.getValue() / floorEntry.getValue();
        if (factorRatio < 0) {
            return -Math.pow(-factorRatio, elapsedTime / keyDifference) * floorEntry.getValue();
        }
        return Math.pow(factorRatio, elapsedTime / keyDifference) * floorEntry.getValue();
    }

    /**
     * contains absolute factors per date
     */
    public TreeMap<DateTime, Double> getFactorsPerDate() {
        return factorsPerDate;
    }

    public void setFactorsPerDate(TreeMap<DateTime, Double> factorsPerDate) {
        this.factorsPerDate = factorsPerDate;
    }
}
