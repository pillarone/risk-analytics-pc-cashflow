package org.pillarone.riskanalytics.domain.pc.cf.legalentity

import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.domain.utils.constant.Rating
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LegalEntities extends DynamicMultiPhaseComposedComponent {

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    PacketList<LegalEntityDefault> outLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault)

    PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsPrimaryInsurer = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsReinsurer = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoPrimeryInsurer = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoReinsurer = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    private static final String PHASE_DEFAULT = "Phase Default";
    private static final String PHASE_GROSS = "Phase Gross";
    private static final String PHASE_NET = "Phase Net";

    public LegalEntity createDefaultSubComponent(){
        new LegalEntity(parmRating: Rating.NO_DEFAULT)
    }

//    @Override
//    void start() {
//        doCalculation(PHASE_DEFAULT)
//    }

    @Override
    protected void doCalculation(String phase) {
        for (Component component : componentList) {
            ((MultiPhaseComponent) component).doCalculation phase
        }
    }

    void allocateChannelsToPhases() {
        setTransmitterPhaseOutput(outLegalEntityDefault, PHASE_DEFAULT)

        setTransmitterPhaseInput(inClaims, PHASE_GROSS)
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_GROSS)
        setTransmitterPhaseOutput(outClaimsGross, PHASE_GROSS)
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_GROSS)

        setTransmitterPhaseInput(inClaimsCeded, PHASE_NET)
        setTransmitterPhaseInput(inClaimsInward, PHASE_NET)
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_NET)
        setTransmitterPhaseInput(inUnderwritingInfoInward, PHASE_NET)
        setTransmitterPhaseOutput(outClaimsPrimaryInsurer, PHASE_NET)
        setTransmitterPhaseOutput(outClaimsReinsurer, PHASE_NET)
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_NET)
        setTransmitterPhaseOutput(outClaimsNet, PHASE_NET)
        setTransmitterPhaseOutput(outUnderwritingInfoPrimeryInsurer, PHASE_NET)
        setTransmitterPhaseOutput(outUnderwritingInfoReinsurer, PHASE_NET)
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_NET)
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_NET)
    }

    @Override
    void wire() {
        replicateInChannels this, 'inClaims'
        replicateInChannels this, 'inClaimsCeded'
        replicateInChannels this, 'inClaimsInward'
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inUnderwritingInfoCeded'
        replicateInChannels this, 'inUnderwritingInfoInward'
        replicateOutChannels this, 'outLegalEntityDefault'
        replicateOutChannels this, 'outClaimsGross'
        replicateOutChannels this, 'outClaimsPrimaryInsurer'
        replicateOutChannels this, 'outClaimsReinsurer'
        replicateOutChannels this, 'outClaimsCeded'
        replicateOutChannels this, 'outClaimsNet'
        replicateOutChannels this, 'outUnderwritingInfoGross'
        replicateOutChannels this, 'outUnderwritingInfoPrimeryInsurer'
        replicateOutChannels this, 'outUnderwritingInfoReinsurer'
        replicateOutChannels this, 'outUnderwritingInfoCeded'
        replicateOutChannels this, 'outUnderwritingInfoNet'
    }
}
