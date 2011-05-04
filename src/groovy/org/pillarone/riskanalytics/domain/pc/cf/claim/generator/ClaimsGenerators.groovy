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
import org.pillarone.riskanalytics.domain.pc.cf.dependency.DependenceStream

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsGenerators extends DynamicComposedComponent {

//    /** needs to be connected only if the claims generator was selected as target in a copula    */
//    PacketList<DependenceStream> inProbabilities = new PacketList<DependenceStream>(DependenceStream.class);
//    /** needs to be connected only ...    */
//    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);

    /** needs to be connected only if a none absolute base is selected    */
    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)
    PacketList<DependenceStream> inProbabilities = new PacketList<DependenceStream>(DependenceStream)

    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    public ClaimsGenerator createDefaultSubComponent() {
        ClaimsGenerator newComponent = new ClaimsGenerator(
                parmClaimsModel: ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, [
                        claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d]),
                        claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        claimsSizeBase: ExposureBase.ABSOLUTE]))
        return newComponent
    }


    public void wire() {
//        replicateInChannels this, 'inProbabilities'
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inFactors'
        replicateInChannels this, 'inPatterns'
        replicateOutChannels this, 'outClaims'
        replicateInChannels this, 'inProbabilities'
    }
}