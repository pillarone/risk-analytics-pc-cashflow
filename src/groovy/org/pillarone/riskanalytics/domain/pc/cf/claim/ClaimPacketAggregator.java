package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.pillarone.riskanalytics.core.output.aggregation.IPacketAggregator;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimPacketAggregator implements IPacketAggregator<ClaimCashflowPacket> {

    public ClaimCashflowPacket aggregate(PacketList packetList) {
        List<ClaimCashflowPacket> aggregateClaimsByBaseClaim = ClaimUtils.aggregateByBaseClaim(packetList);
        return ClaimUtils.sum(aggregateClaimsByBaseClaim, true);
    }
}
