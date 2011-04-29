package org.pillarone.riskanalytics.domain.pc.cf.exposure

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnderwritingSegments extends DynamicComposedComponent {

    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket);
    PacketList<UnderwritingInfoPacket> outUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket);

    @Override
    Component createDefaultSubComponent() {
        return new RiskBands();
    }

    @Override
    void wire() {
        replicateInChannels this, 'inFactors'
        replicateOutChannels this, 'outUnderwritingInfo'
    }
}


