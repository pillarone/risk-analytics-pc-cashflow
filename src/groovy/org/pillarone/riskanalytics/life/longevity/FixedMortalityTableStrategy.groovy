package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.simulation.SimulationException
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IIndexMarker

/**
 * author simon.parten @ art-allianz . com
 */
@CompileStatic
public class FixedMortalityTableStrategy extends AbstractParameterObject implements IMortalityTable {

    private ConstrainedString startTable = new ConstrainedString(IMortalityTableMarker.class, '')
    private Double startYear = 2011d
    private ConstrainedString index = new ConstrainedString(IIndexMarker.class, '')

    public MortalityTableType getType() {
        return MortalityTableType.REFERENCE;
    }

    public Map getParameters() {
        ['startTable' : startTable, 'startYear' : startYear, 'index': index]
    }

    /* Year :: Rate*/
    final Map<Double, Double> year;
    final String name;

    public FixedMortalityTableStrategy(String name) {
        this.name = name;
        year = Maps.newHashMap();
    }

    FixedMortalityTableStrategy(ConstrainedString startTable, Double startYear, ConstrainedString index) {
        this.startTable = startTable
        this.startYear = startYear
        this.index = index
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
