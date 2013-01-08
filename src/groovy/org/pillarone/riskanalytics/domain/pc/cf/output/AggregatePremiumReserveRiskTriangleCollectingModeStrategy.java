package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

/**
 * This collecting mode strategy calculates the premium and reserve risk
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregatePremiumReserveRiskTriangleCollectingModeStrategy extends AggregateSplitByInceptionDateCollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregatePremiumReserveRiskTriangleCollectingModeStrategy.class);

    static final String IDENTIFIER = "PREMIUM_RESERVE_RISK_TRIANGLE";

    @Override
    protected boolean includeDefaultClaimsProperties() {
        return false;
    }

    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean isCompatibleWith(Class packetClass) {
        return ClaimCashflowPacket.class.isAssignableFrom(packetClass);
    }
}
