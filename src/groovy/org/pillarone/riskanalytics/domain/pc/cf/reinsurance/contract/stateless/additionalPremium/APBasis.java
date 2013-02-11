package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AdditionalPremiumPerLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum APBasis {

    PREMIUM {
        @Override
        public AdditionalPremium calculateAP(double lossAfterAnnualStructure, double lossAfterStructureShareApplied, LayerParameters layerParameters, double layerPremium, AdditionalPremiumPerLayer additionalPremiumPerLayer) {
            double premAP = (lossAfterAnnualStructure * layerPremium * layerParameters.getShare() * additionalPremiumPerLayer.getAdditionalPremium()) / layerParameters.getLayerPeriodLimit();
            return new AdditionalPremium(premAP, APBasis.PREMIUM);
        }
    }, LOSS {
        @Override
        public AdditionalPremium calculateAP(double lossAfterAnnualStructure, double lossAfterStructureShareApplied, LayerParameters layerParameters, double layerPremium, AdditionalPremiumPerLayer additionalPremiumPerLayer) {
            double lossAP = lossAfterStructureShareApplied * additionalPremiumPerLayer.getAdditionalPremium();
            return new AdditionalPremium(lossAP, APBasis.LOSS);
        }
    }, NCB {
        @Override
        public AdditionalPremium calculateAP(double lossAfterAnnualStructure, double lossAfterStructureShareApplied, LayerParameters layerParameters, double layerPremium, AdditionalPremiumPerLayer additionalPremiumPerLayer) {
            double ncbAP = 0d;
            if (lossAfterAnnualStructure == 0d) {
                ncbAP = layerParameters.getShare() * additionalPremiumPerLayer.getAdditionalPremium() * layerPremium;
            }
            return new AdditionalPremium(ncbAP, APBasis.NCB);
        }
    };

    public abstract AdditionalPremium calculateAP(double lossAfterAnnualStructure, double lossAfterStructureShareApplied, LayerParameters layerParameters, double layerPremium, AdditionalPremiumPerLayer additionalPremiumPerLayer);

    public static APBasis getStringValue(String value) {
        APBasis[] values = APBasis.values();
        for (APBasis basis : values) {
            if (value.equals(basis.toString())) {
                return basis;
            }
        }
        throw new IllegalArgumentException("Enum not found for " + value);
    }
}
