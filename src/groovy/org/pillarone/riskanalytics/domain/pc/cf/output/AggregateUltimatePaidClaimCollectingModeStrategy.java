package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateUltimatePaidClaimCollectingModeStrategy extends AbstractAggregateCollectingModeStrategy {

    static final String IDENTIFIER = "AGGREGATED_ULTIMATE_PAID_CLAIM";

    private static final List<String> filter = Arrays.asList(ClaimCashflowPacket.ULTIMATE,
            ClaimCashflowPacket.PAID_INDEXED);

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
