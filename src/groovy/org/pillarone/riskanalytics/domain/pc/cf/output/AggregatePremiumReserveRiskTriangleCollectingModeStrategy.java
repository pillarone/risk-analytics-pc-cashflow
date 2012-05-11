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

import java.util.*;

/**
 * This collecting mode strategy calculates the premium and reserve risk
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregatePremiumReserveRiskTriangleCollectingModeStrategy extends AggregateSplitByInceptionDateCollectingModeStrategy implements ICollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregatePremiumReserveRiskTriangleCollectingModeStrategy.class);

    static final String IDENTIFIER = "PREMIUM_RESERVE_RISK_TRIANGLE";

    public List<SingleValueResultPOJO> collect(PacketList packets) throws IllegalAccessException {
        initSimulation();
        iteration = packetCollector.getSimulationScope().getIterationScope().getCurrentIteration();
        period = packetCollector.getSimulationScope().getIterationScope().getPeriodScope().getCurrentPeriod();

        if (isCompatibleWith(packets.get(0).getClass())) {
            List<SingleValueResultPOJO> singleValueResults = new ArrayList<SingleValueResultPOJO>();
            if (packets.get(0) instanceof ClaimCashflowPacket) {
                singleValueResults.addAll(createPremiumReserveRisk(packets));
            }
            return singleValueResults;
        } else {
            String notImplemented = ResourceBundle.getBundle(RESOURCE_BUNDLE).getString("AggregatePremiumReserveRiskTriangleCollectingModeStrategy.notImplemented");
            throw new NotImplementedException(notImplemented + "\n(" + packetCollector.getPath() + ")");
        }
    }

    /**
     * @param packets
     * @return a map with paths as key
     */
    protected Map<PathMapping, Packet> aggregate(List<Packet> packets) {
        // has to be a LinkedHashMap to make sure the shortest path is the first in the map and gets AGGREGATED as collecting mode
        Map<PathMapping, Packet> resultMap = new LinkedHashMap<PathMapping, Packet>(packets.size());
        if (packets == null || packets.size() == 0) {
            return resultMap;
        }

        for (Packet packet : packets) {
            String originPath = packetCollector.getSimulationScope().getStructureInformation().getPath(packet);
            PathMapping path = mappingCache.lookupPath(originPath);
            addToMap(packet, path, resultMap);

        }
        return resultMap;
    }

    
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean isCompatibleWith(Class packetClass) {
        return ClaimCashflowPacket.class.isAssignableFrom(packetClass);
    }
}
