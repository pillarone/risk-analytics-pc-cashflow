package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.core.components.MultiPhaseComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationState
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
//public abstract class AbstractClaimsGenerators extends DynamicMultiPhaseComposedComponent {
public abstract class AbstractClaimsGenerators extends DynamicComposedComponent {

    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)
    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

//    PacketList<CommutationState> inCommutationState = new PacketList<CommutationState>(CommutationState)

//    protected void doCalculation(String phase) {
//        for (Component component : componentList) {
//            ((MultiPhaseComposedComponent) component).doCalculation phase
//        }
//    }

//    void allocateChannelsToPhases() {
//        setTransmitterPhaseInput(inUnderwritingInfo, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )
//        setTransmitterPhaseInput(inFactors, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )
//        setTransmitterPhaseInput(inPatterns, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )
//
//        setTransmitterPhaseOutput(outClaims, AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION )
//
//        setTransmitterPhaseInput(inCommutationState, AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE )
//
//    }

    @Override
    void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inFactors'
        replicateInChannels this, 'inPatterns'
//        replicateInChannels this, 'inCommutationState'

        replicateOutChannels this, 'outClaims'
    }
}