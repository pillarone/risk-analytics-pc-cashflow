package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy;
import org.pillarone.riskanalytics.core.output.SingleValueResultPOJO;
import org.pillarone.riskanalytics.core.output.aggregation.IPacketAggregator;
import org.pillarone.riskanalytics.core.output.aggregation.PacketAggregatorRegistry;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractAggregateCollectingModeStrategy extends AggregatedCollectingModeStrategy {
    
    private static final String RESOURCE_BUNDLE = "org.pillarone.riskanalytics.domain.pc.cf.output.CollectingModeStrategyResources";
    private String displayName;


    public List<SingleValueResultPOJO> collect(PacketList packets, boolean crashSimulationOnError) throws IllegalAccessException {
        IPacketAggregator<Packet> sumAggregator = PacketAggregatorRegistry.getAggregator(packets.get(0).getClass());
        Packet aggregatedPacket = sumAggregator.aggregate(packets); 
        return createSingleValueResults((Packet) packets.get(0), filter(aggregatedPacket.getValuesToSave()), 0, crashSimulationOnError);
    }

    public String getDisplayName(Locale locale) {
        if (displayName == null) {
            displayName = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale).getString("ICollectingModeStrategy." + getIdentifier());
        }
        return displayName;
    }

    abstract public List<String> filter();

    public Map<String, Number> filter(Map<String, Number> valuesToSave) {
        Map<String, Number> filteredValuesToSave = new HashMap<String, Number>(filter().size());
        for (String filterItem : filter()) {
            filteredValuesToSave.put(filterItem, valuesToSave.get(filterItem));
        }
        return filteredValuesToSave;
    }
}
