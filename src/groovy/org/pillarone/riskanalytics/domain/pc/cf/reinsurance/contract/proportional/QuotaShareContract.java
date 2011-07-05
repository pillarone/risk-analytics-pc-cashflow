package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContract extends AbstractProportionalReinsuranceContract {

    protected double quotaShare = 0;

    public QuotaShareContract(double quotaShare, ICommission commission) {
        this.quotaShare = quotaShare;
        this.commission = commission;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim = storage.lazyInitCededClaimRoot(-quotaShare);
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, -quotaShare, -quotaShare, -quotaShare, true);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    public void calculatePremium(List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers, boolean fillNet) {
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.scale(grossUnderwritingInfo,
                    contractMarker, 1, quotaShare * coveredByReinsurers, 1);
            cededUwInfos.add(cededUnderwritingInfo);
            netUnderwritingInfos.add(grossUnderwritingInfo.getNet(cededUnderwritingInfo, true));
        }
    }


     @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(quotaShare);
        buffer.append(", ");
        buffer.append(commission.toString());
        return buffer.toString();
    }
}
