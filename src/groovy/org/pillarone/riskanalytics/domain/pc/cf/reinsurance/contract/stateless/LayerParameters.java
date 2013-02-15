package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter helper class for layers. It's used as intermediary object.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LayerParameters {

    private final double share;
    private final double claimExcess;
    private final double claimLimit;
    private double layerPeriodExcess;
    private double layerPeriodLimit;
    private final List<AdditionalPremiumPerLayer> additionalPremiums;
    private boolean firstAP = true;

    /**
     * Note that is important to subseqquently call addAdditionalpremium after constructing this object. The side effect of failing to do this will be
     * that the infinite limits on period excesses will no be correctly set if no additional premium is added.
     *
     * @param share
     * @param claimExcess
     * @param claimLimit
     */
    public LayerParameters(double share, double claimExcess, double claimLimit) {
        this.share = share;
        this.claimExcess = claimExcess;
        this.additionalPremiums = new ArrayList<AdditionalPremiumPerLayer>();

        if (claimLimit == 0) {
            this.claimLimit = Double.MAX_VALUE;
        } else {
            this.claimLimit = claimLimit;
        }
    }

    public void addAdditionalPremium(double periodExcess, double periodLimit, double additionalPremium, APBasis apBasis) {
        if (additionalPremium != 0) {
            additionalPremiums.add(new AdditionalPremiumPerLayer(periodExcess, periodLimit, additionalPremium, apBasis));
        }
        if (firstAP) {
            layerPeriodExcess = periodExcess;
            firstAP = false;
        } else {
            layerPeriodExcess = Math.min(layerPeriodExcess, periodExcess);
        }
        if (layerPeriodLimit == Double.MAX_VALUE) {
            if (periodLimit == 0) {
                return;
            } else {
                throw new SimulationException("PeriodLimit : " + periodLimit + " follows a zero in the structure table. " +
                        "The period limit has already been set infinite (inferred from the earlier 0) and cannot be changed - please check the structure table. ");
            }
        }

        layerPeriodLimit += periodLimit;

        if (layerPeriodLimit == 0) {
            layerPeriodLimit = Double.MAX_VALUE;
        }
    }

    public double getShare() {
        return share;
    }

    public double getClaimExcess() {
        return claimExcess;
    }

    public double getClaimLimit() {
        return claimLimit;
    }

    public double getLayerPeriodExcess() {
        return layerPeriodExcess;
    }

    public double getLayerPeriodLimit() {
        return layerPeriodLimit;
    }

    public List<AdditionalPremiumPerLayer> getAdditionalPremiums() {
        return additionalPremiums;
    }

    @Override
    public String toString() {
        return "LayerParameters{" +
                "layerPeriodLimit=" + layerPeriodLimit +
                ", layerPeriodExcess=" + layerPeriodExcess +
                ", claimLimit=" + claimLimit +
                ", claimExcess=" + claimExcess +
                ", share=" + share +
                '}';
    }

    public LayerIdentifier getLayerIdentifier() {
        return new LayerIdentifier(share, claimExcess, claimLimit, layerPeriodExcess, layerPeriodLimit);
    }

    public static class LayerIdentifier {
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
}

