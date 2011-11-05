package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.pillarone.riskanalytics.core.output.aggregation.IPacketAggregator;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoPacketAggregator implements IPacketAggregator<UnderwritingInfoPacket> {

    public UnderwritingInfoPacket aggregate(PacketList<UnderwritingInfoPacket> packetList) {
        if (includesCededUnderwritingInfoPacket(packetList)) {
            CededUnderwritingInfoPacket summedUnderwritingInfo = new CededUnderwritingInfoPacket();
            for (UnderwritingInfoPacket underwritingInfo : packetList) {
                summedUnderwritingInfo.plus(underwritingInfo);
                summedUnderwritingInfo.setExposure(underwritingInfo.getExposure());
            }
            return summedUnderwritingInfo;
        }
        else {
            UnderwritingInfoPacket summedUnderwritingInfo = new UnderwritingInfoPacket();
            for (UnderwritingInfoPacket underwritingInfo : packetList) {
                summedUnderwritingInfo.plus(underwritingInfo);
                summedUnderwritingInfo.setExposure(underwritingInfo.getExposure());
            }
            return summedUnderwritingInfo;
        }
    }

    private boolean includesCededUnderwritingInfoPacket(PacketList<UnderwritingInfoPacket> packetList) {
        boolean oneCededUnderwritingInfoPacketFound = false;
        for (UnderwritingInfoPacket underwritingInfo : packetList) {
            oneCededUnderwritingInfoPacketFound = underwritingInfo instanceof CededUnderwritingInfoPacket;
            if (oneCededUnderwritingInfoPacketFound) break;
        }
        return oneCededUnderwritingInfoPacketFound;
    }
}
