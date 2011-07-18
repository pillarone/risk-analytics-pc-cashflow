package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IRIPremiumSplitStrategy extends IParameterObject {
    void initSegmentShares(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos);
    double getShare(UnderwritingInfoPacket grossUnderwritingInfo);
}