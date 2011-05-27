package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.utils.math.copula.ICorrelationMarker;

import java.util.Collections;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Index extends Component implements ICorrelationMarker {

    private PeriodScope periodScope;

    private PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);
    private PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<IndexPacket> outIndices = new PacketList<IndexPacket>(IndexPacket.class);

    private IIndexStrategy parmIndex = IndexStrategyType.getStrategy(IndexStrategyType.NONE, Collections.emptyMap());
    
    @Override
    protected void doCalculation() {
        FactorsPacket factors = parmIndex.getFactors(periodScope, this, inEventSeverities);
        if (factors != null && factors.factorsPerDate.size() > 0) {
            outFactors.add(factors);
        }
        if (this.isSenderWired(outIndices)) {
            outIndices.add(new IndexPacket(factors, periodScope.getCurrentPeriodStartDate()));
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

    public PacketList<IndexPacket> getOutIndices() {
        return outIndices;
    }

    public void setOutIndices(PacketList<IndexPacket> outIndices) {
        this.outIndices = outIndices;
    }

    public PacketList<EventDependenceStream> getInEventSeverities() {
        return inEventSeverities;
    }

    public void setInEventSeverities(PacketList<EventDependenceStream> inEventSeverities) {
        this.inEventSeverities = inEventSeverities;
    }
}
