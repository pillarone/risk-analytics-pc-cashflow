package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class MapPremiumPerPeriod implements IPremiumPerPeriod {

    private final Map<Integer, Double> premiumPerPeriod;

    public MapPremiumPerPeriod(Map<Integer, Double> premiumPerPeriod) {
        this.premiumPerPeriod = premiumPerPeriod;
    }

    @Override
    public double getPremiumInPeriod(final int period) {
        return premiumPerPeriod.get(period);
    }

    public Map<Integer, Double> getPremiumPerPeriod() {
        return premiumPerPeriod;
    }
}
