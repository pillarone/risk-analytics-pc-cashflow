package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;

/**
 * author simon.parten @ art-allianz . com
 */
public class ReinstatementLayer {

    private final YearLayerIdentifier yearLayerIdentifier;
    private final int priority;
    private final double reinstatementPercentage;

    public ReinstatementLayer(final YearLayerIdentifier yearLayerIdentifier, final int priority, final double reinstatementPercentage) {
        this.yearLayerIdentifier = yearLayerIdentifier;
        this.priority = priority;
        this.reinstatementPercentage = reinstatementPercentage;
    }

    public YearLayerIdentifier getYearLayerIdentifier() {
        return yearLayerIdentifier;
    }

    public int getPriority() {
        return priority;
    }


    public double getReinstatementPercentage() {
        return reinstatementPercentage;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ReinstatementLayer)) return false;

        final ReinstatementLayer that = (ReinstatementLayer) o;

        if (priority != that.priority) return false;
        if (Double.compare(that.reinstatementPercentage, reinstatementPercentage) != 0) return false;
        if (!yearLayerIdentifier.equals(that.yearLayerIdentifier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = yearLayerIdentifier.hashCode();
        result = 31 * result + priority;
        temp = Double.doubleToLongBits(reinstatementPercentage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ReinstatementLayer{" +
                "yearLayerIdentifier=" + yearLayerIdentifier +
                ", reinstatementPercentage=" + reinstatementPercentage +
                '}';
    }
}
