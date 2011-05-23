package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsGenerators extends DynamicComposedComponent {

    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)
    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream)
    PacketList<SystematicFrequencyPacket> inEventFrequencies = new PacketList<SystematicFrequencyPacket>(SystematicFrequencyPacket)

    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    public ClaimsGenerator createDefaultSubComponent() {
        ClaimsGenerator newComponent = new ClaimsGenerator(
                parmClaimsModel: ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, [
                        claimsSizeDistribution: DistributionType.getDefault(),
                        claimsSizeModification: DistributionModifier.getDefault(),
                        claimsSizeBase: ExposureBase.ABSOLUTE]))
        return newComponent
    }

    public void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inFactors'
        replicateInChannels this, 'inPatterns'
        replicateInChannels this, 'inEventSeverities'
        replicateInChannels this, 'inEventFrequencies'
        replicateOutChannels this, 'outClaims'
    }
}