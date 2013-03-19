package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy;
import org.pillarone.riskanalytics.core.output.SingleValueResultPOJO;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Arrays;
import java.util.List;

/**
 * Component has the same purpose as AggregateDrillDownCollectingModeStrategy in the property casualty plugin and a similar implementation
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateSplitPerSourceReducedCollectingModeStrategy extends AggregateSplitPerSourceCollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregateSplitPerSourceReducedCollectingModeStrategy.class);

    static final String IDENTIFIER = "SPLIT_PER_SOURCE_REDUCED";

    List<String> collectedFieldList = Arrays.asList("ultimate", "reportedIncrementalIndexed");

    @Override
    public List<String> filter() {
        return collectedFieldList;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public boolean isCompatibleWith(Class packetClass) {
        return ClaimCashflowPacket.class.isAssignableFrom(packetClass);
    }
}
