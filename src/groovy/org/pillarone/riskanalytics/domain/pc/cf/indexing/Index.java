package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

import java.util.Collections;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Index extends Component {

    private PeriodScope periodScope;

    private PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket.class);

    private IIndexStrategy parmIndices = IndexStrategyType.getStrategy(IndexStrategyType.TRIVIAL, Collections.emptyMap());
    
    @Override
    protected void doCalculation() {
        outFactors.addAll(parmIndices.getFactors(periodScope, this));
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

    public IIndexStrategy getParmIndices() {
        return parmIndices;
    }

    public void setParmIndices(IIndexStrategy parmIndices) {
        this.parmIndices = parmIndices;
    }
}
