package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.PremiumStructreAPBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;

/**
 * author simon.parten @ art-allianz . com
 */
public class AdditionalPremiumLayer {

    private final YearLayerIdentifier identifier;
    private final double limitStart;
    private final double limitTopBand;
    private final double limitAPPercent;
    private final PremiumStructreAPBasis apBasis;

    public AdditionalPremiumLayer(final YearLayerIdentifier identifier, final double limitStart, final double limitTopBand, final double limitAPPercent, final PremiumStructreAPBasis apBasis) {
        this.identifier = identifier;
        this.limitStart = limitStart;
        this.limitTopBand = limitTopBand;
        this.limitAPPercent = limitAPPercent;
        this.apBasis = apBasis;
    }

    public YearLayerIdentifier getIdentifier() {
        return identifier;
    }

    public double getLimitStart() {
        return limitStart;
    }

    public double getLimitTopBand() {
        return limitTopBand;
    }

    public double getLimitAPPercent() {
        return limitAPPercent;
    }

    public PremiumStructreAPBasis getApBasis() {
        return apBasis;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AdditionalPremiumLayer)) return false;

        final AdditionalPremiumLayer that = (AdditionalPremiumLayer) o;

        if (Double.compare(that.limitAPPercent, limitAPPercent) != 0) return false;
        if (Double.compare(that.limitStart, limitStart) != 0) return false;
        if (Double.compare(that.limitTopBand, limitTopBand) != 0) return false;
        if (!identifier.equals(that.identifier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = identifier.hashCode();
        temp = Double.doubleToLongBits(limitStart);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(limitTopBand);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(limitAPPercent);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
