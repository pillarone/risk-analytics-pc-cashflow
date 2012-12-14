package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ILossParticipation {
    boolean noLossParticipation();
    void initPeriod(List<ClaimCashflowPacket> claims, List<UnderwritingInfoPacket> underwritingInfos);
    ClaimCashflowPacket cededClaim(double quotaShare, ClaimCashflowPacket grossClaim, ClaimStorage storage, boolean adjustExposureInfo);
    double lossParticipation(double lossRatio);
}
