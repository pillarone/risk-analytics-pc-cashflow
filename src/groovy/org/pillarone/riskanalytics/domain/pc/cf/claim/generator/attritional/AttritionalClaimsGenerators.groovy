package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.components.MultiPhaseDynamicComposedComponent
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationState
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent
import org.pillarone.riskanalytics.core.components.MultiPhaseComposedComponent
import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGenerators extends DynamicMultiPhaseComposedComponent {

    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)
    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class)

    protected PacketList<CommutationState> inCommutationState = new PacketList<CommutationState>(CommutationState.class)

    @Override
    Component createDefaultSubComponent() {
        new AttritionalClaimsGenerator()
    }

    protected void doCalculation(String phase) {
        for (Component component : componentList) {
            ((MultiPhaseComposedComponent) component).doCalculation phase
        }
    }

    void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inUnderwritingInfo, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )
        setTransmitterPhaseInput(inFactors, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )
        setTransmitterPhaseInput(inPatterns, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )

        setTransmitterPhaseOutput(outClaims, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )

        setTransmitterPhaseInput(inCommutationState, AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE )

    }

    @Override
    void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inFactors'
        replicateInChannels this, 'inPatterns'
        replicateInChannels this, 'inCommutationState'
        replicateOutChannels this, 'outClaims'
    }
}
