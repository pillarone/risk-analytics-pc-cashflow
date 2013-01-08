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
public class AggregatePremiumReserveRiskCollectingModeStrategy extends AggregateSplitByInceptionDateCollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregatePremiumReserveRiskCollectingModeStrategy.class);

    static final String IDENTIFIER = "PREMIUM_RESERVE_RISK";

    @Override
    protected boolean includeDefaultClaimsProperties() {
        return false;
    }

    protected boolean splitByInceptionPeriod() {
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
