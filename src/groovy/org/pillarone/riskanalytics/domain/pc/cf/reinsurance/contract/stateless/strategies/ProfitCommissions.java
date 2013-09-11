package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;

/**
 * author simon.parten @ art-allianz . com
 */
public class ProfitCommissions {

    private final YearLayerIdentifier yearLayerIdentifier;
    private final double claimsAsPercentageOfPremium;
    private final double percentageOfPremiumAsPC;

    public ProfitCommissions(final double percentageOfPremiumAsPC, final double claimsAsPercentageOfPremium, final YearLayerIdentifier yearLayerIdentifier) {
        this.percentageOfPremiumAsPC = percentageOfPremiumAsPC;
        this.claimsAsPercentageOfPremium = claimsAsPercentageOfPremium;
        this.yearLayerIdentifier = yearLayerIdentifier;
    }

    public YearLayerIdentifier getYearLayerIdentifier() {
        return yearLayerIdentifier;
    }

    public double getClaimsAsPercentageOfPremium() {
        return claimsAsPercentageOfPremium;
    }

    public double getPercentageOfPremiumAsPC() {
        return percentageOfPremiumAsPC;
    }

    @Override
    public String toString() {
        return "ProfitCommissions{" +
                "yearLayerIdentifier=" + yearLayerIdentifier +
                ", claimsAsPercentageOfPremium=" + claimsAsPercentageOfPremium +
                ", percentageOfPremiumAsPC=" + percentageOfPremiumAsPC +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfitCommissions)) return false;

        final ProfitCommissions that = (ProfitCommissions) o;

        if (Double.compare(that.claimsAsPercentageOfPremium, claimsAsPercentageOfPremium) != 0) return false;
        if (Double.compare(that.percentageOfPremiumAsPC, percentageOfPremiumAsPC) != 0) return false;
        if (!yearLayerIdentifier.equals(that.yearLayerIdentifier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = yearLayerIdentifier.hashCode();
        temp = Double.doubleToLongBits(claimsAsPercentageOfPremium);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(percentageOfPremiumAsPC);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
