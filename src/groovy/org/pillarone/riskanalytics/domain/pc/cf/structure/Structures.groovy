package org.pillarone.riskanalytics.domain.pc.cf.structure

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class Structures extends DynamicComposedComponent {

    PacketList<ClaimCashflowPacket> inClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    public void wire() {
        replicateInChannels this, 'inUnderwritingInfoGross'
        replicateInChannels this, 'inUnderwritingInfoNet'
        replicateInChannels this, 'inUnderwritingInfoCeded'
        replicateInChannels this, 'inClaimsGross'
        replicateInChannels this, 'inClaimsCeded'
        replicateInChannels this, 'inClaimsNet'
        replicateOutChannels this, 'outUnderwritingInfoGross'
        replicateOutChannels this, 'outUnderwritingInfoNet'
        replicateOutChannels this, 'outUnderwritingInfoCeded'
        replicateOutChannels this, 'outClaimsGross'
        replicateOutChannels this, 'outClaimsCeded'
        replicateOutChannels this, 'outClaimsNet'
    }

    public Structure createDefaultSubComponent() {
        Structure structure = new Structure(parmBasisOfStructures: StructuringType.getDefault())
        return structure
    }

    public String getGenericSubComponentName() {
        return "structure"
    }
}
