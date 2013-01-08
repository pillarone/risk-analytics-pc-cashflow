package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.pillarone.riskanalytics.core.output.*;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractSingleCollectingModeStrategy extends AbstractCollectingModeStrategy {
    
    private static final String RESOURCE_BUNDLE = "org.pillarone.riskanalytics.domain.pc.cf.output.CollectingModeStrategyResources";
    private String displayName;

    private AbstractCollectingModeStrategy aggregateCollectionStrategy;
    private SingleValueCollectingModeStrategy singleValueCollectingModeStrategy = new SingleValueCollectingModeStrategy();

    public List<SingleValueResultPOJO> collect(PacketList packets, boolean crashSimulationOnError) throws IllegalAccessException {
        final List<SingleValueResultPOJO> theResults = new ArrayList<SingleValueResultPOJO>();
        aggregateCollectionStrategy.setPacketCollector(this.packetCollector);
        int valueIndex = 0;
        for (Object p : packets) {
            theResults.addAll(createSingleValueResults((Packet) p, filter(((Packet) p).getValuesToSave()), valueIndex, crashSimulationOnError));
            valueIndex++;
        }
        for (SingleValueResultPOJO singleValueResult : theResults) {
            singleValueResult.setCollector(packetCollector.getSimulationScope().getMappingCache().lookupCollector(singleValueCollectingModeStrategy.getIdentifier()));
        }
        final List<SingleValueResultPOJO> aggregatedValues;
        try {
             aggregatedValues = aggregateCollectionStrategy.collect(packets, crashSimulationOnError);
        } catch (Exception ex) {
            throw new SimulationException("Problem with collating aggregate results in single collection strategy", ex);
        }

        for (SingleValueResultPOJO singleValueResult : aggregatedValues) {
            singleValueResult.setCollector(packetCollector.getSimulationScope().getMappingCache().lookupCollector(aggregateCollectionStrategy.getIdentifier()));
        }
        theResults.addAll(aggregatedValues);

        return theResults;
    }

    public String getDisplayName(Locale locale) {
        if (displayName == null) {
            displayName = getIdentifier();
        }
        return displayName;
    }

    public void setAggregatedCollectionStrategy(AbstractCollectingModeStrategy aggregateCollectionStrategy ) {
        this.aggregateCollectionStrategy = aggregateCollectionStrategy;
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
