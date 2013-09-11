package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

/**
 * author simon.parten @ art-allianz . com
 */
public class NoPremiumPerPeriod implements IPremiumPerPeriod {
    @Override
    public double getPremiumInPeriod(final int period) {
        return 0d;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
