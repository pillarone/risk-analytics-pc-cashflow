package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGenerators extends DynamicComposedComponent {

    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)
    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    @Override
    Component createDefaultSubComponent() {
        new AttritionalClaimsGenerator()
    }

    @Override
    void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inFactors'
        replicateInChannels this, 'inPatterns'
        replicateOutChannels this, 'outClaims'
    }
}
