package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.segment.FinancialsPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IStructureMarker;

import java.util.List;


/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class Structure extends Component implements IStructureMarker {

    private PacketList<ClaimCashflowPacket> inClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);

    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    /** contains one aggregate net claim only */
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<FinancialsPacket> outFinancials = new PacketList<FinancialsPacket>(FinancialsPacket.class);

    private IStructuringStrategy parmBasisOfStructures = StructuringType.getDefault();

    private PeriodScope periodScope;

    @Override
    protected void doCalculation() {
        outClaimsGross.addAll(parmBasisOfStructures.filterClaims(inClaimsGross));
        outClaimsCeded.addAll(parmBasisOfStructures.filterClaims(inClaimsCeded));
        outClaimsNet.addAll(ClaimUtils.calculateNetClaims(outClaimsGross, outClaimsCeded));

        outUnderwritingInfoGross.addAll(parmBasisOfStructures.filterUnderwritingInfos(inUnderwritingInfoGross));
        outUnderwritingInfoCeded.addAll(parmBasisOfStructures.filterUnderwritingInfosCeded(inUnderwritingInfoCeded));
        outUnderwritingInfoNet.addAll(UnderwritingInfoUtils.calculateNetUnderwritingInfo(outUnderwritingInfoGross, outUnderwritingInfoCeded));
        fillFinancials(periodScope.getPeriodCounter());
    }

    private void fillFinancials(IPeriodCounter periodCounter) {
        if (isSenderWired(outFinancials)) {
            outFinancials.addAll(FinancialsPacket.getFinancialsPacketsByInceptionPeriod(outUnderwritingInfoGross,
                    outUnderwritingInfoNet, outUnderwritingInfoCeded, outClaimsGross, outClaimsNet, outClaimsCeded, periodCounter));
        }
        if (!isSenderWired(outClaimsNet)) {
            outClaimsNet.clear();
        }
        if (!isSenderWired(outUnderwritingInfoNet)) {
            outUnderwritingInfoNet.clear();
        }
    }

    public PacketList<ClaimCashflowPacket> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<ClaimCashflowPacket> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
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

    public PacketList<CededUnderwritingInfoPacket> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded) {
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

    public PacketList<CededUnderwritingInfoPacket> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public IStructuringStrategy getParmBasisOfStructures() {
        return parmBasisOfStructures;
    }

    public void setParmBasisOfStructures(IStructuringStrategy parmBasisOfStructures) {
        this.parmBasisOfStructures = parmBasisOfStructures;
    }

    public PacketList<FinancialsPacket> getOutFinancials() {
        return outFinancials;
    }

    public void setOutFinancials(PacketList<FinancialsPacket> outFinancials) {
        this.outFinancials = outFinancials;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }
}

