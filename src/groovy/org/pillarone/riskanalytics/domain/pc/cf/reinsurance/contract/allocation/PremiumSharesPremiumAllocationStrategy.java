package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.segment.ISegmentMarker;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class PremiumSharesPremiumAllocationStrategy extends AbstractPremiumAllocation {

    public PremiumAllocationType getType() {
        return PremiumAllocationType.PREMIUM_SHARES;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    /**
     * Calculates the share per segment based on the gross premium written and the line of business property
     * @param cededClaims not used within this strategy
     * @param grossUnderwritingInfos
     */
    public void initSegmentShares(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        initUnderwritingInfoShares(grossUnderwritingInfos, Collections.<ISegmentMarker, Double>emptyMap());
    }
}