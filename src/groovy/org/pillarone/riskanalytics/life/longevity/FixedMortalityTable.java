package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class FixedMortalityTable implements IIndexedMortalityTable {

    /* Year :: Rate*/
    final Map<Double, Double> year;
    final String name;

    public FixedMortalityTable(String name) {
        this.name = name;
        year = Maps.newHashMap();
    }

    public void addIndexValue(Double year, Double rate ) {
        this.year.put(year, rate);
    }


    public IMortalityTableEntry getMortalityObject(Double age, Double year) {
        if(this.year.containsKey(year)) {
            return new MortalityTableEntry(age, year, this.year.get(year));
        }
        throw new SimulationException("Year provided to fixed table; " + year + ", is not initialised.");
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name :" + name + "Rates :" + year.toString() + super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
