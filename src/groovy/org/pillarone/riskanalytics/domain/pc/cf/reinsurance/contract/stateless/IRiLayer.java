package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.AdditionalPremiumLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ProfitCommissions;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ReinstatementLayer;

import java.util.Collection;
import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IRiLayer {

    public double getShare();

    public double getClaimExcess();

    public double getClaimLimit();

    public double getLayerPeriodExcess();

    public double getLayerPeriodLimit();

    public double getInitialPremium();

    public LayerIdentifier getLayerIdentifier();

    public YearLayerIdentifier getYearLayerIdentifier();

    /* We have a new method for processing APs*/
    @Deprecated
    public Collection<AdditionalPremiumPerLayer> getLegacyAdditionalPremiums();

    public Collection<ProfitCommissions> getProfitCommissions();

    public Collection<ReinstatementLayer> getReinstatements();

    public Collection<AdditionalPremiumLayer> getAddPrem();

    public double getNcbPercentage();

}
