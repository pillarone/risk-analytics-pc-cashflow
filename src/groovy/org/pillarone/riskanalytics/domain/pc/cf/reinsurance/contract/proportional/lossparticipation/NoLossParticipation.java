package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.ILimitStrategy;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoLossParticipation implements ILossParticipation {
    public boolean noLossParticipation() {
        return true;
    }

    public void initPeriod(List<ClaimCashflowPacket> claims, List<UnderwritingInfoPacket> underwritingInfos, ILimitStrategy limit) {
    }

    public ClaimCashflowPacket cededClaim(double quotaShare, ClaimCashflowPacket grossClaim, ClaimStorage storage, boolean adjustExposureInfo) {
        throw new NotImplementedException();
    }

    public double lossParticipation(double lossRatio) {
        return 0;
    }

    public double adjustedQuote(double quotaShare, BasedOnClaimProperty basedOn) {
        return quotaShare;
    }
}
