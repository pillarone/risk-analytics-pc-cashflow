package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.CalcAPBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.AdditionalPremiumLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ProfitCommissions;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ReinstatementLayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Parameter helper class for layers. It's used as intermediary object.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LayerParameters implements IRiLayer {

    private final int year;
    private final int layer;
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
     * @param year
     * @param layer
     */
    public LayerParameters(double share, double claimExcess, double claimLimit, final int year, final int layer) {
        this.share = share;
        this.claimExcess = claimExcess;
        this.additionalPremiums = new ArrayList<AdditionalPremiumPerLayer>();
        this.year = year;
        this.layer = layer;
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

    @Override
    public double getInitialPremium() {
        return 0;
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

    @Override
    public YearLayerIdentifier getYearLayerIdentifier() {
        return new YearLayerIdentifier(year, layer);
    }

    @Override
    public Collection<AdditionalPremiumPerLayer> getLegacyAdditionalPremiums() {
        return additionalPremiums;
    }

    @Override
    public Collection<ProfitCommissions> getProfitCommissions() {
        return new ArrayList<ProfitCommissions>();
    }

    @Override
    public Collection<ReinstatementLayer> getReinstatements() {
        return new ArrayList<ReinstatementLayer>();
    }

    @Override
    public Collection<AdditionalPremiumLayer> getAddPrem() {
        return new ArrayList<AdditionalPremiumLayer>();
    }

    public double getNcbPercentage() {
        for (AdditionalPremiumPerLayer ap : additionalPremiums) {
            if(ap.getBasis().equals(CalcAPBasis.NCB)) {
                return ap.getAdditionalPremium();
            }
        }
        return 0d;
    }
}

