package org.pillarone.riskanalytics.domain.pc.cf.structure

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.segment.FinancialsPacket

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class Structures extends DynamicComposedComponent {

    PacketList<ClaimCashflowPacket> inClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)

    PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<FinancialsPacket> outFinancials = new PacketList<FinancialsPacket>(FinancialsPacket)

    public void wire() {
        replicateInChannels this, inUnderwritingInfoGross
        replicateInChannels this, inUnderwritingInfoCeded
        replicateInChannels this, inClaimsGross
        replicateInChannels this, inClaimsCeded
        replicateOutChannels this, outUnderwritingInfoGross
        replicateOutChannels this, outUnderwritingInfoNet
        replicateOutChannels this, outUnderwritingInfoCeded
        replicateOutChannels this, outClaimsGross
        replicateOutChannels this, outClaimsCeded
        replicateOutChannels this, outClaimsNet
        replicateOutChannels this, outFinancials
    }

    public Structure createDefaultSubComponent() {
        Structure structure = new Structure(parmBasisOfStructures: StructuringType.getDefault())
        return structure
    }

    public String getGenericSubComponentName() {
        return "structure"
    }
}
