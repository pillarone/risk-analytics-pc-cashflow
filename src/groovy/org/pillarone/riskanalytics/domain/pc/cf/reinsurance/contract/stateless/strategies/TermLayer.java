package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

/**
 * author simon.parten @ art-allianz . com
 */
public class TermLayer {

    private final double limit;
    private final double excess;
    private final double rate;

    public TermLayer(double limit, double excess, double rate) {
        this.limit = limit;
        this.excess = excess;
        this.rate = rate;
    }

    public double getLossFromThisLayer(double termLoss) {
        return Math.min(Math.max( termLoss - excess, 0), limit) * rate;
    }

    public double getLimit() {
        return limit;
    }

    public double getExcess() {
        return excess;
    }

    public double getRate() {
        return rate;
    }
}
