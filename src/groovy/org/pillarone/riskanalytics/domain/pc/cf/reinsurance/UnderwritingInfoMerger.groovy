package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeRow
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

class UnderwritingInfoMerger extends Component {

    PacketList<UnderwritingInfoPacket> inUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfoBenefit = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    PacketList<UnderwritingInfoPacket> outUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)

    MatrixCoverAttributeStrategy coverAttributeStrategy
    private List<ISegmentMarker> coveredSegmentsGross

    @Override
    protected void doCalculation() {
        filterGrossOfMentionedSegments()

        List<UnderwritingInfoPacket> uwInfosWithCorrectedSign = UnderwritingInfoUtils.correctSign(inUnderwritingInfoGross, false)
        uwInfosWithCorrectedSign.addAll(UnderwritingInfoUtils.correctSign(inUnderwritingInfoNet, false))
        uwInfosWithCorrectedSign.addAll(UnderwritingInfoUtils.correctSign(inUnderwritingInfoCeded, true))
        uwInfosWithCorrectedSign.addAll(UnderwritingInfoUtils.correctSign(inUnderwritingInfoBenefit, false))

        outUnderwritingInfo.addAll(UnderwritingInfoUtils.aggregateBySegmentAndInceptionPeriod(uwInfosWithCorrectedSign))
    }

    private List<UnderwritingInfoPacket> filterGrossOfMentionedSegments() {
        initCoveredSegmentsGross()

        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();

        for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfoGross) {
            if (coveredSegmentsGross.contains(underwritingInfo.segment())) {
                filteredUnderwritingInfo.add(underwritingInfo);
            }
        }
        inUnderwritingInfoGross.clear();
        if (!filteredUnderwritingInfo.isEmpty()) {
            inUnderwritingInfoGross.addAll(filteredUnderwritingInfo);
        }
        return filteredUnderwritingInfo
    }

    private void initCoveredSegmentsGross() {
        if (coveredSegmentsGross == null) {
            List<MatrixCoverAttributeRow> filters = coverAttributeStrategy.getRowFilters()
            coveredSegmentsGross = new ArrayList<ISegmentMarker>()
            for (MatrixCoverAttributeRow filter : filters) {
                if (filter.coverGrossClaimsOnly()) {
                    coveredSegmentsGross.add(filter.segment)
                }
            }
        }
    }
}
