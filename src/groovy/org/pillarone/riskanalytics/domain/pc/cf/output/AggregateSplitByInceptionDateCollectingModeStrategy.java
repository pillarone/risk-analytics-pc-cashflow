package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy;
import org.pillarone.riskanalytics.core.output.PathMapping;
import org.pillarone.riskanalytics.core.output.SingleValueResultPOJO;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This collecting mode strategy splits claim and underwriting information up by inception date.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateSplitByInceptionDateCollectingModeStrategy extends AbstractSplitCollectingModeStrategy implements ICollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregateSplitByInceptionDateCollectingModeStrategy.class);

    static final String IDENTIFIER = "SPLIT_BY_INCEPTION_DATE";

    public List<SingleValueResultPOJO> collect(PacketList packets) {
        initSimulation();
        iteration = packetCollector.getSimulationScope().getIterationScope().getCurrentIteration();
        period = packetCollector.getSimulationScope().getIterationScope().getPeriodScope().getCurrentPeriod();

        if (isCompatibleWith(packets.get(0).getClass())) {
            try {
                return createSingleValueResults(aggregate(packets));
            }
            catch (IllegalAccessException ex) {
//                todo(sku): remove
            }
        } else {
            String notImplemented = ResourceBundle.getBundle(RESOURCE_BUNDLE).getString("AggregateSplitByInceptionDateCollectingModeStrategy.notImplemented");
            throw new NotImplementedException(notImplemented + "\n(" + packetCollector.getPath() + ")");
        }
        return null;
    }

    /**
     * @param packets
     * @return a map with paths as key
     */
    private Map<PathMapping, Packet> aggregate(List<Packet> packets) {
        // has to be a LinkedHashMap to make sure the shortest path is the first in the map and gets AGGREGATED as collecting mode
        Map<PathMapping, Packet> resultMap = new LinkedHashMap<PathMapping, Packet>(packets.size());
        if (packets == null || packets.size() == 0) {
            return resultMap;
        }

        for (Packet packet : packets) {
            String originPath = packetCollector.getSimulationScope().getStructureInformation().getPath(packet);
            PathMapping path = mappingCache.lookupPath(originPath);
            addToMap(packet, path, resultMap);

            PathMapping periodPath = getPathMapping(packet);
            addToMap(packet, periodPath, resultMap);
        }
        return resultMap;
    }
    
    private String inceptionPeriod(Packet packet) {
        if (packet instanceof ClaimCashflowPacket) {
            return String.valueOf(((ClaimCashflowPacket) packet).getOccurrenceDate().getYear());
        }
        else if (packet instanceof UnderwritingInfoPacket) {
            return String.valueOf(((UnderwritingInfoPacket) packet).getExposure().getInceptionDate().getYear());
        }
        else {
            throw new IllegalArgumentException("Packet type " + packet.getClass() + " is not supported.");
        }
    }

    private void addToMap(Packet packet, PathMapping path, Map<PathMapping, Packet> resultMap) {
        if (packet instanceof ClaimCashflowPacket) {
            addToMap((ClaimCashflowPacket) packet, path, resultMap);
        }
        else if (packet instanceof UnderwritingInfoPacket) {
            addToMap((UnderwritingInfoPacket) packet, path, resultMap);
        }
        else {
            throw new IllegalArgumentException("Packet type " + packet.getClass() + " is not supported.");
        }
    }

    private PathMapping getPathMapping(Packet packet) {
        String periodLabel = inceptionPeriod(packet);
        String pathExtension = "period" + PATH_SEPARATOR + periodLabel;
        String pathExtended = getExtendedPath(packet, pathExtension);
        return mappingCache.lookupPath(pathExtended);
    }
    
    public String getIdentifier() {
        return IDENTIFIER;
    }

}
