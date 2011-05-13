package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

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
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot(-quotaShare, contractMarker);
        ClaimCashflowPacket cededClaim = grossClaim.withBaseClaimAndShare(cededBaseClaim, -quotaShare, -quotaShare, grossClaim.ultimate() != 0);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    public void calculatePremium() {
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.scale(grossUnderwritingInfo, contractMarker, 1, quotaShare, 1);
            cededUwInfos.add(cededUnderwritingInfo);
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
