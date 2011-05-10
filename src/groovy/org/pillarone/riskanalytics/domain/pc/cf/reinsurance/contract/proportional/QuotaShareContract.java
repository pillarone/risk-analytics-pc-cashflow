package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContract implements IPropReinsuranceContract {

    // todo(sku): should probably be part of an abstract super class
    protected IReinsuranceContractMarker contractMarker;
    protected double quotaShare = 0;

    public QuotaShareContract(double quotaShare) {
        this.quotaShare = quotaShare;
    }

    public void initBookkeepingFigures(List<ClaimCashflowPacket> grossClaims) {
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot(-quotaShare, contractMarker);
        return grossClaim.withBaseClaimAndShare(cededBaseClaim, -quotaShare, -quotaShare, grossClaim.ultimate() != 0);
    }

    public UnderwritingInfoPacket calculateUnderwritingInfoCeded(UnderwritingInfoPacket grossInfo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CommissionPacket calculateCommission(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> cededUnderwritingInfo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

     @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(quotaShare);
        return buffer.toString();
    }
}
