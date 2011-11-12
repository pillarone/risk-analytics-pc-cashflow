package org.pillarone.riskanalytics.domain.pc.cf.exposure

import org.pillarone.riskanalytics.core.packets.PacketList
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnderwritingInfoPacketAggregatorTests extends GroovyTestCase {

    void testUsage() {
        UnderwritingInfoPacket grossUnderwritingInfo = new UnderwritingInfoPacket()
        ExposureInfo exposureInfo = new ExposureInfo(new DateTime(2011, 1, 1, 0,0,0,0), 0, 80, 100, ExposureBase.ABSOLUTE)
        grossUnderwritingInfo.setExposure(exposureInfo)
        CededUnderwritingInfoPacket cededUnderwritingInfo = new CededUnderwritingInfoPacket()
        cededUnderwritingInfo.setExposure(exposureInfo)

        UnderwritingInfoPacketAggregator aggregator = new UnderwritingInfoPacketAggregator()
        PacketList<UnderwritingInfoPacket> packetList = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
        packetList << grossUnderwritingInfo << cededUnderwritingInfo
        UnderwritingInfoPacket aggregatePacket = aggregator.aggregate(packetList)

        assertTrue "packet instance", aggregatePacket instanceof CededUnderwritingInfoPacket

    }
}
