package org.pillarone.riskanalytics.domain.pc.cf.exposure

import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnderwritingInfoPacketAggregatorTests extends GroovyTestCase {

    void testUsage() {
        UnderwritingInfoPacket grossUnderwritingInfo = new UnderwritingInfoPacket()
        CededUnderwritingInfoPacket cededUnderwritingInfo = new CededUnderwritingInfoPacket()

        UnderwritingInfoPacketAggregator aggregator = new UnderwritingInfoPacketAggregator()
        PacketList<UnderwritingInfoPacket> packetList = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
        packetList << grossUnderwritingInfo << cededUnderwritingInfo
        UnderwritingInfoPacket aggregatePacket = aggregator.aggregate(packetList)

        assertTrue "packet instance", aggregatePacket instanceof CededUnderwritingInfoPacket

    }
}
