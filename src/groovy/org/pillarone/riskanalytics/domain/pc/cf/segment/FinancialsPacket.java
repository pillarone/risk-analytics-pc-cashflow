package org.pillarone.riskanalytics.domain.pc.cf.segment;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FinancialsPacket extends MultiValuePacket {

    private double netCashflow;
    private double netPremiumPaid;
    private double netPremiumWritten;
    private double netClaimPaid;
    private double netClaimUltimate;
    private double commission;
    private double lossRatio;

    public FinancialsPacket() {
    }

    public FinancialsPacket(List<UnderwritingInfoPacket> netUwInfos, List<CededUnderwritingInfoPacket> cededUwInfos,
                            List<ClaimCashflowPacket> netClaims) {
        if (!netUwInfos.isEmpty()) {
            netPremiumWritten = UnderwritingInfoUtils.aggregate(netUwInfos).getPremiumWritten();
            netPremiumPaid = UnderwritingInfoUtils.aggregate(netUwInfos).getPremiumPaid();
        }
        if (!netClaims.isEmpty()) {
            netClaimUltimate = ClaimUtils.sum(netClaims, true).getNominalUltimate();
            netClaimPaid = ClaimUtils.sum(netClaims, true).getPaidIncrementalIndexed();
        }
        if (!cededUwInfos.isEmpty()) {
            CededUnderwritingInfoPacket aggregateCededUwInfoPacket = UnderwritingInfoUtils.aggregate(cededUwInfos);
            commission = aggregateCededUwInfoPacket.getCommission();
        }
        netCashflow = netPremiumPaid + netClaimPaid + commission;
        lossRatio = netPremiumWritten == 0 ? 0 : -netClaimUltimate / netPremiumWritten;
    }

    public double getNetCashflow() {
        return netCashflow;
    }

    public void setNetCashflow(double netCashflow) {
        this.netCashflow = netCashflow;
    }

    public double getNetPremiumPaid() {
        return netPremiumPaid;
    }

    public void setNetPremiumPaid(double netPremiumPaid) {
        this.netPremiumPaid = netPremiumPaid;
    }

    public double getNetClaimPaid() {
        return netClaimPaid;
    }

    public void setNetClaimPaid(double netClaimPaid) {
        this.netClaimPaid = netClaimPaid;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getLossRatio() {
        return lossRatio;
    }

    public void setLossRatio(double lossRatio) {
        this.lossRatio = lossRatio;
    }
}
