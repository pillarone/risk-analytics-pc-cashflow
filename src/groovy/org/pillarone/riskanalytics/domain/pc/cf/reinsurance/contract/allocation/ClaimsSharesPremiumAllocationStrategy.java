package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ClaimsSharesPremiumAllocationStrategy extends AbstractPremiumAllocation {

    public PremiumAllocationType getType() {
        return PremiumAllocationType.CLAIMS_SHARES;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    /**
     * Calculates the share per segment based on the ultimate of each claim and the line of business property
     * @param cededClaims
     * @param grossUnderwritingInfos not used within this strategy
     */
    public void initSegmentShares(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        Map<ISegmentMarker, Double> segmentShares = new HashMap<ISegmentMarker, Double>();
        double totalCededClaim = 0d;
        for (ClaimCashflowPacket cededClaim : cededClaims) {
            ISegmentMarker segment = cededClaim.segment();
            if (segment == null) continue;
            Double totalCededSegmentClaim = segmentShares.get(segment);
            totalCededClaim += cededClaim.ultimate();
            if (totalCededSegmentClaim == null) {
                segmentShares.put(segment, cededClaim.ultimate());
            }
            else {
                segmentShares.put(segment, totalCededSegmentClaim + cededClaim.ultimate());
            }
        }
        if (totalCededClaim == 0) {
            segmentShares.clear();
        }
        else {
            for (Map.Entry<ISegmentMarker, Double> totalCededSegmentClaim : segmentShares.entrySet()) {
                segmentShares.put(totalCededSegmentClaim.getKey(), totalCededSegmentClaim.getValue() / totalCededClaim);
            }
        }
        initUnderwritingInfoShares(grossUnderwritingInfos, segmentShares);
    }

}