package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FactorsPacket extends Packet {

    private final TreeMap<DateTime, Double> incrementalFactors;
    private TreeMap<DateTime, Double> cumulativeFactorsPerDate = new TreeMap<DateTime, Double>();

    public FactorsPacket() {
        incrementalFactors = Maps.newTreeMap();
    }

    public FactorsPacket(DateTime date, double factor) {
        incrementalFactors = Maps.newTreeMap();
        add(date, factor);
    }

    public void add(DateTime date, double factor) {
        getFactorsPerDate().put(date, factor);
    }

    public void add(DateTime date, double cumulativeFactor, double incrementalFactor){
        if(incrementalFactors.containsKey(date)) {
            throw new SimulationException("Attempted to add incrementalFactor which already exists at date : " + DateTimeUtilities.formatDate.print(date) + ". Check your index table and Contact development");
        }
        incrementalFactors.put(date, incrementalFactor);
        add(date, cumulativeFactor);
    }

    public Double getIncrementalFactor(DateTime date) {
        Double incFactor = incrementalFactors.get(date);
        if(incFactor == null) {
            throw new SimulationException("Attempted to lookup incremental Factor which does not exist at date : " + DateTimeUtilities.formatDate.print(date) + ". Check your index table and Contact development");
        }
        return incFactor;
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
        return Math.pow(factorRatio, elapsedTime / keyDifference) * floorEntry.getValue();
    }

    /**
     * contains absolute factors per date
     */
    public TreeMap<DateTime, Double> getFactorsPerDate() {
        return cumulativeFactorsPerDate;
    }

    public void setFactorsPerDate(TreeMap<DateTime, Double> factorsPerDate) {
        this.cumulativeFactorsPerDate = factorsPerDate;
    }
}
