package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent;
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy;
import org.pillarone.riskanalytics.core.output.PathMapping;
import org.pillarone.riskanalytics.core.output.SingleValueResultPOJO;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IStructureMarker;

import java.util.*;

/**
 * Component has the same purpose as AggregateDrillDownCollectingModeStrategy in the property casualty plugin and a similar implementation
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateSplitPerSourceReducedCollectingModeStrategy extends AggregateSplitPerSourceCollectingModeStrategy implements ICollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregateSplitPerSourceReducedCollectingModeStrategy.class);

    static final String IDENTIFIER = "SPLIT_PER_SOURCE_REDUCED";

    List<String> collectedFieldList = Arrays.asList("ultimate", "reportedIncrementalIndexed");

    // try introducing a more general concept!, see AggregateUltimatePaidClaimCollectingModeStrategy as example
    protected List<SingleValueResultPOJO> createSingleValueResults(Map<PathMapping, Packet> packets) throws IllegalAccessException {
        List<SingleValueResultPOJO> singleValueResults = new ArrayList<SingleValueResultPOJO>(packets.size());
        boolean firstPath = true;
        for (Map.Entry<PathMapping, Packet> packetEntry : packets.entrySet()) {
            PathMapping path = packetEntry.getKey();
            Packet packet = packetEntry.getValue();
            for (Map.Entry<String, Number> field : packet.getValuesToSave().entrySet()) {
                String fieldName = field.getKey();
                if (!collectedFieldList.contains(fieldName)) continue;
                Double value = (Double) field.getValue();
                if (value == Double.NaN || value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY) {
                    if (LOG.isErrorEnabled()) {
                        StringBuilder message = new StringBuilder();
                        message.append(value).append(" collected at ").append(packetCollector.getPath());
                        message.append(" (period ").append(period).append(") in iteration ");
                        message.append(iteration).append(" - ignoring.");
                        LOG.error(message);
                    }
                    continue;
                }
                SingleValueResultPOJO result = new SingleValueResultPOJO();
                result.setSimulationRun(simulationRun);
                result.setIteration(iteration);
                result.setPeriod(period);
                result.setPath(path);
                if (firstPath) {    // todo(sku): might be completely removed
                    result.setCollector(mappingCache.lookupCollector("AGGREGATED"));
                }
                else {
                    result.setCollector(mappingCache.lookupCollector(getIdentifier()));
                }
                result.setField(mappingCache.lookupField(fieldName));
                result.setValueIndex(0);
                result.setValue(value);
                singleValueResults.add(result);
            }
            firstPath = false;
        }
        return singleValueResults;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public boolean isCompatibleWith(Class packetClass) {
        return ClaimCashflowPacket.class.isAssignableFrom(packetClass);
    }
}
