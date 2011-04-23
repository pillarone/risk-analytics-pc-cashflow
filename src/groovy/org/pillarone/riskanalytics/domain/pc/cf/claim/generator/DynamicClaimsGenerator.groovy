package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicClaimsGenerator extends DynamicComposedComponent {

//    /** needs to be connected only if a none absolute base is selected    */
//    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
//    /** needs to be connected only if the claims generator was selected as target in a copula    */
//    PacketList<DependenceStream> inProbabilities = new PacketList<DependenceStream>(DependenceStream.class);
//    /** needs to be connected only ...    */
//    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);

    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    public ClaimsGenerator createDefaultSubComponent() {
        ClaimsGenerator newComponent = new ClaimsGenerator(
                parmClaimsModel: ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, [
                        claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d]),
                        claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        claimsSizeBase: ExposureBase.ABSOLUTE]))
        return newComponent
    }

    // todo(sku): modify as soon as in channels are available
    @Override protected void doCalculation() {
        for (ClaimsGenerator generator : super.componentList) {
            generator.start()
        }
    }




    public void wire() {
//        replicateInChannels this, 'inUnderwritingInfo'
//        replicateInChannels this, 'inProbabilities'
//        replicateInChannels this, 'inEventSeverities'
        replicateOutChannels this, 'outClaims'
    }
}