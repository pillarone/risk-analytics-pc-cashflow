package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;


/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class Structure extends Component {

    private PacketList<ClaimCashflowPacket> inClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

    private IStructuringStrategy parmBasisOfStructures = StructuringType.getDefault();

    @Override
    protected void doCalculation() {
        outClaimsGross.addAll(parmBasisOfStructures.filterClaims(inClaimsGross));
        outClaimsNet.addAll(parmBasisOfStructures.filterClaims(inClaimsNet));
        outClaimsCeded.addAll(parmBasisOfStructures.filterClaims(inClaimsCeded));

        outUnderwritingInfoGross.addAll(parmBasisOfStructures.filterUnderwritingInfos(inUnderwritingInfoGross));
        outUnderwritingInfoNet.addAll(parmBasisOfStructures.filterUnderwritingInfos(inUnderwritingInfoNet));
        outUnderwritingInfoCeded.addAll(parmBasisOfStructures.filterUnderwritingInfos(inUnderwritingInfoCeded));
    }

    public PacketList<ClaimCashflowPacket> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<ClaimCashflowPacket> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
    }

    public PacketList<ClaimCashflowPacket> getInClaimsNet() {
        return inClaimsNet;
    }

    public void setInClaimsNet(PacketList<ClaimCashflowPacket> inClaimsNet) {
        this.inClaimsNet = inClaimsNet;
    }

    public PacketList<ClaimCashflowPacket> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<ClaimCashflowPacket> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
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

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }

    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfoPacket> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfoNet() {
        return inUnderwritingInfoNet;
    }

    public void setInUnderwritingInfoNet(PacketList<UnderwritingInfoPacket> inUnderwritingInfoNet) {
        this.inUnderwritingInfoNet = inUnderwritingInfoNet;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<UnderwritingInfoPacket> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
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

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<UnderwritingInfoPacket> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public IStructuringStrategy getParmBasisOfStructures() {
        return parmBasisOfStructures;
    }

    public void setParmBasisOfStructures(IStructuringStrategy parmBasisOfStructures) {
        this.parmBasisOfStructures = parmBasisOfStructures;
    }
}

