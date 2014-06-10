package org.pillarone.riskanalytics.domain.pc.cf.segment;

import org.pillarone.riskanalytics.core.output.aggregation.IPacketAggregator;

import java.util.List;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public class FinancialsPacketAggregator implements IPacketAggregator<FinancialsPacket> {

    /**
     * @param packetList
     * @return summed properties, ratios are recalculated, inception date is set equal to the first inception date of the list
     */
    @Override
    public FinancialsPacket aggregate(List<FinancialsPacket> packetList) {
        FinancialsPacket sum = new FinancialsPacket();
        for (FinancialsPacket packet : packetList) {
            if (sum.getInceptionDate() == null) {
                sum.setInceptionDate(packet.getInceptionDate());
            }
            sum.plus(packet);
        }
        return sum;
    }

    @Override
    public Class<FinancialsPacket> getPacketClass() {
        return FinancialsPacket.class;
    }
}
