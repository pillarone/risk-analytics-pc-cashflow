package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter helper class for layers. It's used as intermediary object.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LayerIdentifier {

    private final double share;
    private final double claimExcess;
    private final double claimLimit;
    private final double layerPeriodExcess;
    private final double layerPeriodLimit;

    public LayerIdentifier(double share, double claimExcess, double claimLimit, double layerPeriodExcess, double layerPeriodLimit) {
        this.share = share;
        this.claimExcess = claimExcess;
        this.claimLimit = claimLimit;
        this.layerPeriodExcess = layerPeriodExcess;
        this.layerPeriodLimit = layerPeriodLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayerIdentifier that = (LayerIdentifier) o;

        if (Double.compare(that.claimExcess, claimExcess) != 0) return false;
        if (Double.compare(that.claimLimit, claimLimit) != 0) return false;
        if (Double.compare(that.layerPeriodExcess, layerPeriodExcess) != 0) return false;
        if (Double.compare(that.layerPeriodLimit, layerPeriodLimit) != 0) return false;
        if (Double.compare(that.share, share) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = share != +0.0d ? Double.doubleToLongBits(share) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = claimExcess != +0.0d ? Double.doubleToLongBits(claimExcess) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = claimLimit != +0.0d ? Double.doubleToLongBits(claimLimit) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = layerPeriodExcess != +0.0d ? Double.doubleToLongBits(layerPeriodExcess) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = layerPeriodLimit != +0.0d ? Double.doubleToLongBits(layerPeriodLimit) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LayerIdentifier{" +
                "share=" + share +
                ", claimExcess=" + claimExcess +
                ", claimLimit=" + claimLimit +
                ", layerPeriodExcess=" + layerPeriodExcess +
                ", layerPeriodLimit=" + layerPeriodLimit +
                '}';
    }
}

