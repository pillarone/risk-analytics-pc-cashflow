package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.pillarone.riskanalytics.core.output.AggregatedWithSingleAvailableCollectingModeStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SingleUltimatePaidClaimCollectingModeStrategy extends AbstractSingleCollectingModeStrategy {

    public static final String IDENTIFIER = "SINGLE_ULTIMATE_PAID_CLAIM";
    AggregatedWithSingleAvailableCollectingModeStrategy aggregatedStrategy;

    public String getIdentifier() {
        return IDENTIFIER;
    }

    public SingleUltimatePaidClaimCollectingModeStrategy() {
        aggregatedStrategy = new AggregatedWithSingleAvailableCollectingModeStrategy();
        super.setAggregatedCollectionStrategy(aggregatedStrategy);
    }

    public boolean isCompatibleWith(Class packetClass) {
        return ClaimCashflowPacket.class.isAssignableFrom(packetClass);
    }

    public List<String> filter() {
        return new AggregateUltimatePaidClaimCollectingModeStrategy().filter();
    }
}
