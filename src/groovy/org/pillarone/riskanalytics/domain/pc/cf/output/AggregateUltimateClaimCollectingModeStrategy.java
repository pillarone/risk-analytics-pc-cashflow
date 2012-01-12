package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateUltimateClaimCollectingModeStrategy extends AbstractAggregateCollectingModeStrategy {
    
    static final String IDENTIFIER = "AGGREGATED_ULTIMATE_CLAIM";

    private static final List<String> filter = Arrays.asList(ClaimCashflowPacket.ULTIMATE);

    public String getIdentifier() {
        return IDENTIFIER;
    }

    public boolean isCompatibleWith(Class packetClass) {
        return ClaimCashflowPacket.class.isAssignableFrom(packetClass);
    }

    @Override
    public List<String> filter() {
        return filter;
    }
}
