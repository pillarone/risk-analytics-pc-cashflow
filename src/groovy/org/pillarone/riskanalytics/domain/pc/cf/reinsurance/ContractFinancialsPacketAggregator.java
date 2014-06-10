package org.pillarone.riskanalytics.domain.pc.cf.reinsurance;

import org.pillarone.riskanalytics.core.output.aggregation.IPacketAggregator;

import java.util.List;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public class ContractFinancialsPacketAggregator implements IPacketAggregator<ContractFinancialsPacket> {

    /**
     * @param packetList
     * @return summed properties, ratios are recalculated, inception date is set equal to the first inception date of the list
     */
    @Override
    public ContractFinancialsPacket aggregate(List<ContractFinancialsPacket> packetList) {
        ContractFinancialsPacket sum = new ContractFinancialsPacket();
        for (ContractFinancialsPacket packet : packetList) {
            if (sum.getInceptionDate() == null) {
                sum.setInceptionDate(packet.getInceptionDate());
            }
            sum.plus(packet);
        }
        return sum;
    }

    @Override
    public Class<ContractFinancialsPacket> getPacketClass() {
        return ContractFinancialsPacket.class;
    }
}
