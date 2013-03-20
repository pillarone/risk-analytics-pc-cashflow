package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker;

import java.util.Collections;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class Discounting extends Component implements IDiscountMarker, ICorrelationMarker {

    private PeriodScope periodScope;

    /** events have only an effect in combination with stochastic indices selected */
    private PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);
    private PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket.class);

    private IIndexStrategy parmIndex = IndexStrategyType.getStrategy(IndexStrategyType.NONE, Collections.emptyMap());

    @Override
    protected void doCalculation() {
        FactorsPacket factors = parmIndex.getFactors(periodScope, this, inEventSeverities);
        if (factors != null && factors.getFactorsPerDate().size() > 0) {
            outFactors.add(factors);
        }
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<FactorsPacket> getOutFactors() {
        return outFactors;
    }

    public void setOutFactors(PacketList<FactorsPacket> outFactors) {
        this.outFactors = outFactors;
    }

    public IIndexStrategy getParmIndex() {
        return parmIndex;
    }

    public void setParmIndex(IIndexStrategy parmIndex) {
        this.parmIndex = parmIndex;
    }

    public PacketList<EventDependenceStream> getInEventSeverities() {
        return inEventSeverities;
    }

    public void setInEventSeverities(PacketList<EventDependenceStream> inEventSeverities) {
        this.inEventSeverities = inEventSeverities;
    }
}

