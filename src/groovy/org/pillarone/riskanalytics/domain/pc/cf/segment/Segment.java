package org.pillarone.riskanalytics.domain.pc.cf.segment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion;
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Segment extends MultiPhaseComponent implements ISegmentMarker {

    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);

    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);

    private ConstrainedMultiDimensionalParameter parmClaimsPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(PERIL, PORTION), ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER));
    private ConstrainedMultiDimensionalParameter parmUnderwritingPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(UNDERWRITING, PORTION),
            ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER));


    private static final String PERIL = "Claims Generator";
    private static final String UNDERWRITING = "Underwriting";
    private static final String PORTION = "Portion of Claims";

    private static final String PHASE_GROSS = "Phase Gross";
    private static final String PHASE_NET = "Phase Net";

    @Override
    public void doCalculation(String phase) {
        if (phase.equals(PHASE_GROSS)) {
            getSegmentClaims();
            getSegmentUnderwritingInfo();
        }
        else if (phase.equals(PHASE_NET)) {
            filterCededClaims();
            calculateNetClaims();
            filterCededUnderwritingInfo();
            calculateNetUnderwritingInfo();
        }
    }

    private void calculateNetUnderwritingInfo() {
        ListMultimap<UnderwritingInfoPacket, CededUnderwritingInfoPacket> aggregateCededUnderwritingInfos
                = ArrayListMultimap.create();
        if (isSenderWired(outUnderwritingInfoNet)) {
            for (CededUnderwritingInfoPacket cededUnderwritingInfo : inUnderwritingInfoCeded) {
                aggregateCededUnderwritingInfos.put(cededUnderwritingInfo.getOriginal(), cededUnderwritingInfo);
            }
            for (UnderwritingInfoPacket grossUwInfo : outUnderwritingInfoGross) {
                List<CededUnderwritingInfoPacket> cededUnderwritingInfoPackets = aggregateCededUnderwritingInfos.get(grossUwInfo);
                CededUnderwritingInfoPacket aggregateCededUwInfo = UnderwritingInfoUtils.aggregate(cededUnderwritingInfoPackets);
                UnderwritingInfoPacket netUwInfo = grossUwInfo.getNet(aggregateCededUwInfo, true);
                outUnderwritingInfoNet.add(netUwInfo);
            }
        }
    }

    private void calculateNetClaims() {
        ListMultimap<IClaimRoot, ClaimCashflowPacket> aggregateCededClaimPerRoot = ArrayListMultimap.create();
        if (isSenderWired(outClaimsNet)) {
            for (ClaimCashflowPacket cededClaim : inClaimsCeded) {
                aggregateCededClaimPerRoot.put(cededClaim.getBaseClaim(), (ClaimCashflowPacket) cededClaim.copy());
            }
            for (ClaimCashflowPacket grossClaim : outClaimsGross) {
                List<ClaimCashflowPacket> cededClaims = aggregateCededClaimPerRoot.get(grossClaim.getBaseClaim());
                ClaimCashflowPacket aggregateCededClaim = ClaimUtils.sum(cededClaims, true);
                ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(grossClaim, aggregateCededClaim);
                outClaimsNet.add(netClaim);
            }
        }
    }

    private void filterCededUnderwritingInfo() {
        for (CededUnderwritingInfoPacket underwritingInfo : inUnderwritingInfoCeded) {
            if (underwritingInfo.segment().equals(this)) {
                outUnderwritingInfoCeded.add(underwritingInfo);
            }
        }
    }

    private void filterCededClaims() {
        for (ClaimCashflowPacket claim : inClaimsCeded) {
            if (claim.segment().equals(this)) {
                outClaimsCeded.add(claim);
            }
        }
    }

    private void getSegmentUnderwritingInfo() {
        if (inUnderwritingInfo.size() > 0) {
            List<UnderwritingInfoPacket> lobUnderwritingInfos = new ArrayList<UnderwritingInfoPacket>();
            int portionColumn = parmUnderwritingPortions.getColumnIndex(PORTION);
            for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
                String originName = underwritingInfo.riskBand().getNormalizedName();
                int row = parmUnderwritingPortions.getColumnByName(UNDERWRITING).indexOf(originName);
                if (row > -1) {
                    UnderwritingInfoPacket lobUnderwritingInfo = (UnderwritingInfoPacket) underwritingInfo.copy();
                    double segmentPortion = InputFormatConverter.getDouble(parmUnderwritingPortions.getValueAt(row + 1, portionColumn));
                    lobUnderwritingInfo.setPremiumWritten(lobUnderwritingInfo.getPremiumWritten() * segmentPortion);
                    lobUnderwritingInfo.setPremiumPaid(lobUnderwritingInfo.getPremiumPaid() * segmentPortion);
                    lobUnderwritingInfo.setSumInsured(lobUnderwritingInfo.getSumInsured() * segmentPortion);
                    lobUnderwritingInfo.setMaxSumInsured(lobUnderwritingInfo.getMaxSumInsured() * segmentPortion);
                    lobUnderwritingInfo.origin = this;
                    lobUnderwritingInfo.setSegment(this);
                    lobUnderwritingInfos.add(lobUnderwritingInfo);
                }
            }
            outUnderwritingInfoGross.addAll(lobUnderwritingInfos);
        }
    }

    private void getSegmentClaims() {
        if (inClaims.size() > 0) {
            List<ClaimCashflowPacket> segmentClaims = new ArrayList<ClaimCashflowPacket>();
            int portionColumn = parmClaimsPortions.getColumnIndex(PORTION);
            for (ClaimCashflowPacket marketClaim : inClaims) {
                String originName = marketClaim.peril().getNormalizedName();
                int row = parmClaimsPortions.getColumnByName(PERIL).indexOf(originName);
                if (row > -1 && marketClaim.peril() instanceof IPerilMarker) {
                    ClaimCashflowPacket segmentClaim = (ClaimCashflowPacket) marketClaim.copy();
                    // PMO-750: claim mergers in reinsurance program won't work with reference to market claims
                    segmentClaim.origin = this;
                    segmentClaim.setMarker(this);
                    double scaleFactor = InputFormatConverter.getDouble(parmClaimsPortions.getValueAt(row + 1, portionColumn));
                    segmentClaims.add(ClaimUtils.scale(segmentClaim, scaleFactor));
                }
            }
            outClaimsGross.addAll(segmentClaims);
        }
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inClaims, PHASE_GROSS);
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_GROSS);
        setTransmitterPhaseOutput(outClaimsGross, PHASE_GROSS);
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_GROSS);

        setTransmitterPhaseInput(inClaimsCeded, PHASE_NET);
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_NET);
        setTransmitterPhaseOutput(outClaimsNet, PHASE_NET);
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_NET);
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_NET);
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_NET);
    }

    @Override
    public String toString() {
        return getNormalizedName();
    }

    public PacketList<ClaimCashflowPacket> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<ClaimCashflowPacket> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<ClaimCashflowPacket> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<ClaimCashflowPacket> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<CededUnderwritingInfoPacket> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<ClaimCashflowPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<ClaimCashflowPacket> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<ClaimCashflowPacket> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }

    public PacketList<CededUnderwritingInfoPacket> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public ConstrainedMultiDimensionalParameter getParmClaimsPortions() {
        return parmClaimsPortions;
    }

    public void setParmClaimsPortions(ConstrainedMultiDimensionalParameter parmClaimsPortions) {
        this.parmClaimsPortions = parmClaimsPortions;
    }

    public ConstrainedMultiDimensionalParameter getParmUnderwritingPortions() {
        return parmUnderwritingPortions;
    }

    public void setParmUnderwritingPortions(ConstrainedMultiDimensionalParameter parmUnderwritingPortions) {
        this.parmUnderwritingPortions = parmUnderwritingPortions;
    }
}
