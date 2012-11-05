package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;


import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot

import org.pillarone.riskanalytics.core.simulation.SimulationException
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AdditionalPremiumPerLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IIncurredCalculation
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.APBasis
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IContractClaimStore
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache

/**
 * author simon.parten @ art-allianz . com
 */
public class AnnualIncurredCalc implements IIncurredCalculation {

    public double layerCededIncurred(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters) {

        double lossAfterAnnualStructure = lossAfterAnnualStructure(incurredClaims, layerParameters);

        double lossAfterShareAndProRata = lossAfterAnnualStructure * layerParameters.getShare();

        return lossAfterShareAndProRata;

    }

    private double lossAfterAnnualStructure(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters) {
        double lossAfterClaimStructure = 0;
        for (IClaimRoot incurredClaim : incurredClaims) {
            lossAfterClaimStructure += Math.min(Math.max(incurredClaim.getUltimate() - layerParameters.getClaimExcess(), 0), layerParameters.getClaimLimit());
        }
        return Math.min(Math.max(lossAfterClaimStructure - layerParameters.getLayerPeriodExcess(), 0), layerParameters.getLayerPeriodLimit());
    }

    public double additionalPremiumByLayer( Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters, double layerPremium) {
        double additionalPremium = 0;

        for (AdditionalPremiumPerLayer additionalPremiumPerLayer : layerParameters.getAdditionalPremiums()) {
            double tempAdditionalPremium = 0;
            LayerParameters tempLayer = new LayerParameters(layerParameters.getShare(), layerParameters.getClaimExcess(), layerParameters.getClaimLimit());
            tempLayer.addAdditionalPremium(additionalPremiumPerLayer.getPeriodExcess(), additionalPremiumPerLayer.getPeriodLimit(), additionalPremiumPerLayer.getAdditionalPremium(), additionalPremiumPerLayer.getBasis());
            switch (additionalPremiumPerLayer.getBasis()) {
                case APBasis.PREMIUM:
                    double loss = lossAfterAnnualStructure(incurredClaims, tempLayer);
                    tempAdditionalPremium = (loss * layerPremium * layerParameters.getShare() * additionalPremiumPerLayer.getAdditionalPremium()) / tempLayer.getLayerPeriodLimit();
                    break;
                case APBasis.LOSS:
                    tempAdditionalPremium = layerCededIncurred(incurredClaims, tempLayer) * additionalPremiumPerLayer.getAdditionalPremium();
                    break;
                case APBasis.NCB:
                    if (lossAfterAnnualStructure(incurredClaims, tempLayer) == 0) {
                        tempAdditionalPremium = layerParameters.getShare() * additionalPremiumPerLayer.getAdditionalPremium() * layerPremium;
                    }
                    break;
                default:
                    throw new SimulationException("Unknown additional premium basis :" + additionalPremiumPerLayer.getBasis());
            }
            additionalPremium += tempAdditionalPremium;
        }

        return additionalPremium;
    }

    public double additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium) {
        double additionalPremiumAllLayers = 0;
        for (LayerParameters layerParameter : layerParameters) {
            additionalPremiumAllLayers += additionalPremiumByLayer(incurredClaims, layerParameter, layerPremium);
        }
        return additionalPremiumAllLayers;
    }

    public double cededIncurredRespectTerm(IAllContractClaimCache claimStore, ScaledPeriodLayerParameters scaledLayerParameters, PeriodScope periodScope, double termExcess, double termLimit, IPeriodCounter counter, ContractCoverBase coverageBase) {
        throw new SimulationException("Annual calculation was asked for a term calculation. It has no knowledge of this");
    }
}

