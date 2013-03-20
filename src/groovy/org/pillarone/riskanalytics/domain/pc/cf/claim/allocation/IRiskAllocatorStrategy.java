package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IRiskAllocatorStrategy {

    List<ClaimRoot> getAllocatedClaims(List<ClaimRoot> claims, List<UnderwritingInfoPacket> underwritingInfos);
}
