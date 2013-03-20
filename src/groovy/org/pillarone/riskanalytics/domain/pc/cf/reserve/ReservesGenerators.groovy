package org.pillarone.riskanalytics.domain.pc.cf.reserve

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket

import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.core.packets.SingleValuePacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReservesGenerators extends DynamicComposedComponent {

    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)

    PacketList<ClaimCashflowPacket> outReserves = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<SingleValuePacket> outNominalUltimates = new PacketList<SingleValuePacket>(SingleValuePacket)

    public ReservesGenerator createDefaultSubComponent() {
        ReservesGenerator newComponent = new ReservesGenerator(
                parmUltimateEstimationMethod: ReserveCalculationType.getDefault())
        return newComponent
    }

    public void wire() {
        replicateInChannels this, 'inFactors'
        replicateInChannels this, 'inPatterns'
        replicateOutChannels this, 'outReserves'
        replicateOutChannels this, 'outNominalUltimates'
    }
}