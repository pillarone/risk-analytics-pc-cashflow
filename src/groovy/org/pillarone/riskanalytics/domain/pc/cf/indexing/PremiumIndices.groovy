package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumIndices extends DynamicComposedComponent {

    PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket)

    @Override
    Component createDefaultSubComponent() {
        PremiumIndex index = new PremiumIndex(parmIndices : IndexStrategyType.getDefault());
        return index
    }

    @Override protected void doCalculation() {
        for (Index index : componentList) {
            index.start()
        }
    }

    @Override
    void wire() {
        replicateOutChannels this, 'outFactors'
    }
}
