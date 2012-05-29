package org.pillarone.riskanalytics.domain.pc.cf.segment

import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountedValuesPacket
import org.pillarone.riskanalytics.domain.pc.cf.discounting.NetPresentValuesPacket
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class Segments extends DynamicMultiPhaseComposedComponent {

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    // todo: remove inReserves as soon as PMO-1733 is solved (and collect within inClaims)
    PacketList<ClaimCashflowPacket> inReserves = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    // todo: remove inReserves as soon as PMO-1733 is solved (and collect within inClaimsCeded)
    PacketList<ClaimCashflowPacket> inReservesCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    // todo: remove inReserves as soon as PMO-1733 is solved (and collect within inUnderwritingInfoCeded)
    PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded2 = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault)

    PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<DiscountedValuesPacket> outDiscountedValues = new PacketList<DiscountedValuesPacket>(DiscountedValuesPacket)
    PacketList<NetPresentValuesPacket> outNetPresentValues = new PacketList<NetPresentValuesPacket>(NetPresentValuesPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)

    private static final String PHASE_GROSS = "Phase Gross";
    private static final String PHASE_NET = "Phase Net";

    public Segment createDefaultSubComponent() {
        Segment segment = new Segment(parmClaimsPortions: new ConstrainedMultiDimensionalParameter(
                [[], []], Arrays.asList(Segment.PERIL, Segment.PORTION), ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER)),
                parmUnderwritingPortions: new ConstrainedMultiDimensionalParameter(
                        [[], []], Arrays.asList(Segment.UNDERWRITING, Segment.PORTION),
                        ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER)),
                parmReservesPortions: new ConstrainedMultiDimensionalParameter(
                        [[], []], Arrays.asList(Segment.RESERVE, Segment.PORTION), ConstraintsFactory.getConstraints(ReservePortion.IDENTIFIER)))
        return segment
    }

    protected void doCalculation(String phase) {
        for (Component component: componentList) {
            ((MultiPhaseComponent) component).doCalculation phase
        }
    }

    @Override
    void wire() {
        replicateInChannels this, 'inClaims'
        replicateInChannels this, 'inReserves'
        replicateInChannels this, 'inClaimsCeded'
        replicateInChannels this, 'inReservesCeded'
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inUnderwritingInfoCeded'
        replicateInChannels this, 'inUnderwritingInfoCeded2'
        replicateInChannels this, 'inFactors'
        if (isReceiverWired(inLegalEntityDefault)) {
            replicateInChannels this, 'inLegalEntityDefault'
        }
        replicateOutChannels this, 'outClaimsGross'
        replicateOutChannels this, 'outClaimsNet'
        replicateOutChannels this, 'outClaimsCeded'
        replicateOutChannels this, 'outUnderwritingInfoGross'
        replicateOutChannels this, 'outUnderwritingInfoNet'
        replicateOutChannels this, 'outUnderwritingInfoCeded'
        if (isSenderWired(outDiscountedValues)) {
            replicateOutChannels this, 'outDiscountedValues'
            replicateOutChannels this, 'outNetPresentValues'
        }
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inClaims, PHASE_GROSS);
        setTransmitterPhaseInput(inReserves, PHASE_GROSS);
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_GROSS);
        setTransmitterPhaseInput(inFactors, PHASE_GROSS);
        setTransmitterPhaseInput(inLegalEntityDefault, PHASE_GROSS)
        setTransmitterPhaseOutput(outClaimsGross, PHASE_GROSS);
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_GROSS);

        setTransmitterPhaseInput(inClaimsCeded, PHASE_NET);
        setTransmitterPhaseInput(inReservesCeded, PHASE_NET);
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_NET);
        setTransmitterPhaseInput(inUnderwritingInfoCeded2, PHASE_NET);
        setTransmitterPhaseOutput(outClaimsNet, PHASE_NET);
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_NET);
        setTransmitterPhaseOutput(outDiscountedValues, PHASE_NET);
        setTransmitterPhaseOutput(outNetPresentValues, PHASE_NET);
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_NET);
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_NET);
    }
}
