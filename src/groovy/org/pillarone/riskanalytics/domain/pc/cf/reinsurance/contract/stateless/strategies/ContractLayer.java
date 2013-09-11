package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AdditionalPremiumPerLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IRiLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;

import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class ContractLayer implements IRiLayer {

    private final YearLayerIdentifier identifier;
    private final double share;
    private final double verticalLimit;
    private final double verticalExcess;
    private final double periodLimit;
    private final double periodExcess;
    private final double initialPremium;
    private final Collection<ReinstatementLayer> reinstatements;
    private final Collection<AdditionalPremiumLayer> additionalPremiums;
    private final Collection<ProfitCommissions> profitCommissions;
    /* This collection exists for legacy reasons only, in general it should be blank */
    private final Collection<AdditionalPremiumPerLayer> legacyAdditionalPremiums;
    private final double ncbPercentage;



        public ContractLayer(
                final YearLayerIdentifier identifier,
                final double share,
                final double verticalLimit,
                final double verticalExcess,
                final double periodLimit,
                final double periodExcess,
                final double initialPremium,
                final Collection<ReinstatementLayer> reinstatements,
                final Collection<AdditionalPremiumLayer> additionalPremiums,
                final Collection<ProfitCommissions> profitCommissions,
                final double ncbPercentage) {
        this.identifier = identifier;
        this.share = share;
        this.verticalLimit = verticalLimit;
        this.verticalExcess = verticalExcess;
        this.periodLimit = periodLimit;
        this.periodExcess = periodExcess;
        this.initialPremium = initialPremium;
        this.reinstatements = reinstatements;
        this.additionalPremiums = additionalPremiums;
        this.profitCommissions = profitCommissions;
            this.ncbPercentage = ncbPercentage;
            this.legacyAdditionalPremiums = Lists.newArrayList();
    }

    public ContractLayer(
            final YearLayerIdentifier identifier,
            final double share,
            final double verticalLimit,
            final double verticalExcess,
            final double periodLimit,
            final double periodExcess,
            final double initialPremium,
            final Collection<ReinstatementLayer> reinstatements,
            final Collection<AdditionalPremiumLayer> additionalPremiums,
            final Collection<ProfitCommissions> profitCommissions,
            final Collection<AdditionalPremiumPerLayer> legacyAdditionalPremiums,
            final double ncbPercentage) {
        this.identifier = identifier;
        this.share = share;
        this.verticalLimit = verticalLimit;
        this.verticalExcess = verticalExcess;
        this.periodLimit = periodLimit;
        this.periodExcess = periodExcess;
        this.initialPremium = initialPremium;
        this.reinstatements = reinstatements;
        this.additionalPremiums = additionalPremiums;
        this.profitCommissions = profitCommissions;
        this.legacyAdditionalPremiums = legacyAdditionalPremiums;
        this.ncbPercentage = ncbPercentage;
    }

    public LayerParameters getLayerParameterNoAP() {
        final LayerParameters layerParameters = new LayerParameters(share, verticalExcess, verticalLimit, (int) identifier.getYear(), (int) identifier.getLayer());
        layerParameters.addAdditionalPremium(periodExcess, periodLimit, 0d, APBasis.NONE);
        return layerParameters;
    }

    public YearLayerIdentifier getIdentifier() {
        return identifier;
    }

    public double getShare() {
        return share;
    }

    public double getClaimLimit() {
        return verticalLimit;
    }

    public double getClaimExcess() {
        return verticalExcess;
    }

    public double getLayerPeriodLimit() {
        return periodLimit;
    }

    @Override
    public LayerIdentifier getLayerIdentifier() {
        return new LayerIdentifier(share, verticalExcess, verticalLimit, periodExcess, periodLimit);
    }

    @Override
    public YearLayerIdentifier getYearLayerIdentifier() {
        return identifier;
    }

    public double getLayerPeriodExcess() {
        return periodExcess;
    }

    public double getInitialPremium() {
        return initialPremium;
    }

    public Collection<ReinstatementLayer> getReinstatements() {
        return reinstatements;
    }

    public Collection<AdditionalPremiumLayer> getAdditionalPremiums() {
        return additionalPremiums;
    }

    public Collection<ProfitCommissions> getProfitCommissions() {
        return profitCommissions;
    }

    public Collection<AdditionalPremiumPerLayer> getLegacyAdditionalPremiums() {
        return legacyAdditionalPremiums;
    }

    @Override
    public Collection<AdditionalPremiumLayer> getAddPrem() {
        return additionalPremiums;
    }

    public double getNcbPercentage() {
        return ncbPercentage;
    }

    @Override
    public String toString() {
        return "ContractLayer{" +
                "identifier=" + identifier +
                ", share=" + share +
                ", vL=" + verticalLimit +
                ", vE=" + verticalExcess +
                ", pL=" + periodLimit +
                ", pE=" + periodExcess +
                ", initialPremium=" + initialPremium +
                ", no reinstatements=" + reinstatements.size() +
                ", no additionalPremiums=" + additionalPremiums.size() +
                ", no profitCommissions=" + profitCommissions.size() +
                '}';
    }
}
