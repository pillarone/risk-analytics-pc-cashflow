package org.pillarone.riskanalytics.domain.pc.cf.discounting

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class Discountings extends DynamicComposedComponent {

    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream)
    PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket)

    @Override
    Component createDefaultSubComponent() {
        Discounting discount = new Discounting(parmIndex: IndexStrategyType.getDefault());
        return discount
    }


    @Override
    void wire() {
        replicateOutChannels this, 'outFactors'
        replicateInChannels this, 'inEventSeverities'
    }
}
