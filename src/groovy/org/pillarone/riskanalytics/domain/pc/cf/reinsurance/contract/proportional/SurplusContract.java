package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SurplusContract extends AbstractProportionalReinsuranceContract {

    private double retention;
    /** number of lines */
    private double lines;
    /** surplus share for claims without sum insured information */
    private double defaultCededLossShare;


    public SurplusContract(double retention, double lines, double defaultCededLossShare, ICommission commission) {
        this.retention = retention;
        this.lines = lines;
        this.defaultCededLossShare = defaultCededLossShare;
        this.commission = commission;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        double cessionRate = defaultCededLossShare;
        if (grossClaim.hasExposureInfo()) {
            if (grossClaim.getNominalUltimate() > grossClaim.getExposureInfo().getSumInsured()) {
                // handle a total loss according to https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1261
                cessionRate = getFractionCeded(grossClaim.getNominalUltimate());
            }
            else {
                cessionRate = getFractionCeded(grossClaim.getExposureInfo().getSumInsured());
            }
        }
        return ClaimUtils.getCededClaim(grossClaim, storage, -cessionRate, -cessionRate, -cessionRate, true);
    }

    private double getFractionCeded(double sumInsured) {
        if (sumInsured > 0) {
            return Math.min(Math.max(sumInsured - retention, 0), lines * retention) / sumInsured;
        }
        else {
            return 0;
        }
    }

    public void calculatePremium(List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers, boolean fillNet) {
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            double cessionRate = getFractionCeded(grossUnderwritingInfo.getSumInsured());
            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.scale(grossUnderwritingInfo,
                    contractMarker, 1, cessionRate * coveredByReinsurers, 1);
            cededUwInfos.add(cededUnderwritingInfo);
            netUnderwritingInfos.add(grossUnderwritingInfo.getNet(cededUnderwritingInfo, true));
        }
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("retention: ");
        buffer.append(retention);
        buffer.append(", lines: ");
        buffer.append(lines);
        buffer.append(", default ceded loss share: ");
        buffer.append(defaultCededLossShare);
        buffer.append(", ");
        buffer.append(commission.toString());
        return buffer.toString();
    }
}
