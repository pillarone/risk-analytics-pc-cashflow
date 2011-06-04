package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.segment.ISegmentMarker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): correct implementation for written and paid https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1579
abstract public class AbstractPremiumAllocation extends AbstractParameterObject implements IPremiumAllocationStrategy {

    protected Map<UnderwritingInfoPacket, Double> cededPremiumSharePerGrossUnderwritingInfo = new HashMap<UnderwritingInfoPacket, Double>();

    public double getShare(UnderwritingInfoPacket grossUnderwritingInfoPacket) {
        Double share = cededPremiumSharePerGrossUnderwritingInfo.get(grossUnderwritingInfoPacket);
        return share == null ? 1d : share;
    }

    protected void initUnderwritingInfoShares(List<UnderwritingInfoPacket> grossUnderwritingInfoPackets, Map<ISegmentMarker, Double> segmentShares) {
        if (segmentShares.isEmpty()) {
            proportionalAllocation(grossUnderwritingInfoPackets);
        }
        else {
            Map<ISegmentMarker, Double> segmentPremium = new HashMap<ISegmentMarker, Double>();
            for (UnderwritingInfoPacket underwritingInfo : grossUnderwritingInfoPackets) {
                Double aggregatedPremium = segmentPremium.get(underwritingInfo.getSegment());
                if (aggregatedPremium == null) {
                    segmentPremium.put(underwritingInfo.getSegment(), underwritingInfo.getPremiumWritten());
                }
                else {
                    segmentPremium.put(underwritingInfo.getSegment(), underwritingInfo.getPremiumWritten() + aggregatedPremium);
                }
            }
            for (UnderwritingInfoPacket underwritingInfo : grossUnderwritingInfoPackets) {
                double premiumShareInSegment = underwritingInfo.getPremiumWritten() / segmentPremium.get(underwritingInfo.getSegment());
                Double segmentShare = segmentShares.get(underwritingInfo.getSegment());
                cededPremiumSharePerGrossUnderwritingInfo.put(underwritingInfo, premiumShareInSegment * (segmentShare == null ? 1 : segmentShare));
            }
        }
    }

    protected void proportionalAllocation(List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        cededPremiumSharePerGrossUnderwritingInfo = new HashMap<UnderwritingInfoPacket, Double>();
        if (grossUnderwritingInfos.size() > 0) {
            double totalPremium = UnderwritingInfoUtils.aggregate(grossUnderwritingInfos).getPremiumWritten();
            if (totalPremium == 0) {
                for (UnderwritingInfoPacket underwritingInfo: grossUnderwritingInfos) {
                    cededPremiumSharePerGrossUnderwritingInfo.put(underwritingInfo, 0d);
                }
            }
            else {
                for (UnderwritingInfoPacket underwritingInfo: grossUnderwritingInfos) {
                    cededPremiumSharePerGrossUnderwritingInfo.put(underwritingInfo, underwritingInfo.getPremiumWritten() / totalPremium);
                }
            }
        }
    }
}
