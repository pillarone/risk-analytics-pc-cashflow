package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PolicyIndices extends DynamicComposedComponent {

    PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket)

    @Override
    Component createDefaultSubComponent() {
        PolicyIndex index = new PolicyIndex(parmIndex : IndexStrategyType.getDefault());
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

