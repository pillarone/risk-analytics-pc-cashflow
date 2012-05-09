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
public class AggregatePremiumReserveRiskCollectingModeStrategy extends AggregateSplitByInceptionDateCollectingModeStrategy implements ICollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregatePremiumReserveRiskCollectingModeStrategy.class);

    static final String IDENTIFIER = "PREMIUM_RESERVE_RISK";

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
            String notImplemented = ResourceBundle.getBundle(RESOURCE_BUNDLE).getString("AggregateSplitByInceptionDateCollectingModeStrategy.notImplemented");
            throw new NotImplementedException(notImplemented + "\n(" + packetCollector.getPath() + ")");
        }
    }

    @Override
    protected List<SingleValueResultPOJO> createPremiumReserveRisk(List<ClaimCashflowPacket> claims) {
        List<SingleValueResultPOJO> results = new ArrayList<SingleValueResultPOJO>();
        double totalReserveRisk = 0;
        double premiumRisk = 0;
        for (ClaimCashflowPacket claim : claims) {
            if (claim.reserveRisk() != 0) {
                // belongs to reserve risk
                totalReserveRisk += claim.reserveRisk();
            }
            else if (claim.premiumRisk() != 0) {
                // belongs to premium risk
                premiumRisk = claim.premiumRisk();
                results.add(createSingleValueResult(packetCollector.getPath(), PREMIUM_RISK, premiumRisk));
            }
        }
        if (totalReserveRisk != 0) {
            results.add(createSingleValueResult(packetCollector.getPath(), RESERVE_RISK, totalReserveRisk));
        }
        if (premiumRisk + totalReserveRisk != 0) {
            results.add(createSingleValueResult(packetCollector.getPath(), CALENDAR_YEAR_VOLATILITY, premiumRisk + totalReserveRisk));
        }
        return results;
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
