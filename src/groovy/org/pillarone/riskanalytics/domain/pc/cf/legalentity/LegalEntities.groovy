package org.pillarone.riskanalytics.domain.pc.cf.legalentity

import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.domain.utils.constant.Rating
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.DefaultProbabilities
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LegalEntities extends DynamicMultiPhaseComposedComponent {

    PacketList<DefaultProbabilities> inDefaultProbabilities = new PacketList<DefaultProbabilities>(DefaultProbabilities)

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
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
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    private static final String PHASE_DEFAULT = "Phase Default";
    private static final String PHASE_CALC = "Phase Calculation";

    public LegalEntity createDefaultSubComponent(){
        new LegalEntity(parmRating: Rating.NO_DEFAULT)
    }

    @Override
    protected void doCalculation(String phase) {
        for (Component component : componentList) {
            ((MultiPhaseComponent) component).doCalculation phase
        }
    }

    void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inDefaultProbabilities, PHASE_DEFAULT)
        setTransmitterPhaseOutput(outLegalEntityDefault, PHASE_DEFAULT)

        setTransmitterPhaseInput(inClaims, PHASE_CALC)
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_CALC)
        setTransmitterPhaseOutput(outClaimsGross, PHASE_CALC)
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_CALC)

        setTransmitterPhaseInput(inClaimsCeded, PHASE_CALC)
        setTransmitterPhaseInput(inClaimsInward, PHASE_CALC)
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_CALC)
        setTransmitterPhaseInput(inUnderwritingInfoInward, PHASE_CALC)
        setTransmitterPhaseOutput(outClaimsPrimaryInsurer, PHASE_CALC)
        setTransmitterPhaseOutput(outClaimsReinsurer, PHASE_CALC)
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_CALC)
        setTransmitterPhaseOutput(outClaimsNet, PHASE_CALC)
        setTransmitterPhaseOutput(outUnderwritingInfoPrimeryInsurer, PHASE_CALC)
        setTransmitterPhaseOutput(outUnderwritingInfoReinsurer, PHASE_CALC)
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_CALC)
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_CALC)
    }

    @Override
    void wire() {
        replicateInChannels this, 'inDefaultProbabilities'
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
