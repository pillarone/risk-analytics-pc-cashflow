package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;


import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.AdditionalPremiumLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ReinstatementLayer;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class AnnualIncurredCalc implements IIncurredCalculation {

    public LossAfterClaimAndAnnualStructures layerCededIncurred(Collection<IClaimRoot> incurredClaims, IRiLayer layerParameters) {

        double lossAfterClaimStructure = 0;
        for (IClaimRoot incurredClaim : incurredClaims) {
            lossAfterClaimStructure += Math.min(Math.max(incurredClaim.getUltimate() - layerParameters.getClaimExcess(), 0), layerParameters.getClaimLimit());
        }
        double lossAfterAnnualStructure = Math.min(Math.max(lossAfterClaimStructure - layerParameters.getLayerPeriodExcess(), 0), layerParameters.getLayerPeriodLimit());
        return new LossAfterClaimAndAnnualStructures(lossAfterAnnualStructure, lossAfterClaimStructure, layerParameters);
    }

    public Collection<AdditionalPremium> additionalPremiumByLayer(double layerPremium, final IncurredLossWithTerm lossAndLayer, final IRiLayer layerParams) {
        Collection<AdditionalPremium> additionalPremiums = new ArrayList<AdditionalPremium>();

        for (AdditionalPremiumPerLayer additionalPremiumPerLayer : layerParams.getLegacyAdditionalPremiums()) {
            double loss = lossAndLayer.getIncurredLoss().getLayerAndIncurredLoss(layerParams.getLayerIdentifier()).getLossAfterClaimAndAnnualStructures().getLossAfterAnnualStructureWithShareApplied();
            switch (additionalPremiumPerLayer.getBasis()) {
                case PREMIUM:
                    double premAP = (loss * layerPremium * additionalPremiumPerLayer.getAdditionalPremium() * layerParams.getShare() ) / layerParams.getClaimLimit();
                    if(premAP != 0 ){
                        AdditionalPremium lossAPo = new AdditionalPremium(premAP, CalcAPBasis.PREMIUM, layerParams);
                        additionalPremiums.add(lossAPo);
                    }
                    break;
                case LOSS:
                    double lossAP = loss * additionalPremiumPerLayer.getAdditionalPremium();
                    if(lossAP != 0 ) {
                        AdditionalPremium additionalPremium = new AdditionalPremium(lossAP, CalcAPBasis.LOSS, layerParams);
                        additionalPremiums.add(additionalPremium);
                    }
                    break;
                case NCB:
                    double ncbAP = 0;
                    if (loss == 0) {
                        ncbAP = layerParams.getShare() * additionalPremiumPerLayer.getAdditionalPremium() * layerPremium;
                    }
                    if(ncbAP != 0) {
                        additionalPremiums.add(new AdditionalPremium(ncbAP, CalcAPBasis.NCB, layerParams));
                    }
                    break;
                case NONE:
                    break;
                default:
                    throw new SimulationException("Unknown additional premium basis :" + additionalPremiumPerLayer.getBasis());
            }
        }
        double numberReinstatements = layerParams.getReinstatements().size();
        for (ReinstatementLayer reinstatementLayer : layerParams.getReinstatements()) {
            if(layerParams.getClaimLimit() == 0){
                continue;
            }
            double lossAfterAnnStruct = lossAndLayer.getIncurredLossAfterTermStructurte();
            double reinstatementPerc = Math.min( lossAfterAnnStruct / layerParams.getClaimLimit() , numberReinstatements );
            double usedReinstatement = Math.min(1, Math.max(reinstatementPerc - reinstatementLayer.getPriority(), 0));
            double reinstatementPremium = usedReinstatement * layerParams.getShare() * layerParams.getInitialPremium() * reinstatementLayer.getReinstatementPercentage() ;
            additionalPremiums.add(new AdditionalPremium(reinstatementPremium, CalcAPBasis.REINSTATEMENT_PREMIUM, layerParams));
        }

        for(AdditionalPremiumLayer additionalPremium : layerParams.getAddPrem() ){
            if(layerParams.getClaimLimit() == 0){
                continue;
            }
            double lossAfterAnnStruct = lossAndLayer.getIncurredLossAfterTermStructurte();
            double apPercLimit = lossAfterAnnStruct / (layerParams.getClaimLimit() * layerParams.getShare());
            double percentageUsed = Math.max(0, Math.min( apPercLimit - additionalPremium.getLimitStart(), additionalPremium.getLimitTopBand() - additionalPremium.getLimitStart())) * additionalPremium.getLimitAPPercent();
            if(additionalPremium.getApBasis().equals(PremiumStructreAPBasis.LOSS)) {
                double lossPrem = percentageUsed * lossAfterAnnStruct;
                additionalPremiums.add(new AdditionalPremium(lossPrem, CalcAPBasis.LOSS, layerParams));
            }
            else if(additionalPremium.getApBasis().equals(PremiumStructreAPBasis.PREMIUM)) {
                double premiumBasis = layerParams.getInitialPremium() * layerParams.getShare() * percentageUsed;
                additionalPremiums.add(new AdditionalPremium(premiumBasis, CalcAPBasis.PREMIUM, layerParams));
            } else {throw new SimulationException("Uknown ap basis ;" + additionalPremium.getApBasis().toString());}
        }

        if(lossAndLayer.getIncurredLossAfterTermStructurte() == 0)  {
            double ncbPercentage = layerParams.getNcbPercentage();
            double ncbAP = layerParams.getInitialPremium() * layerParams.getShare() * ncbPercentage;
            additionalPremiums.add(new AdditionalPremium(ncbAP, CalcAPBasis.NCB, layerParams));
        }
        return additionalPremiums;
    }

//    public Collection<LayerAndAP> additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium) {
//        Collection<LayerAndAP> additionalPremiumAllLayers = new ArrayList<LayerAndAP>();
//        for (LayerParameters layerParameter : layerParameters) {
//            LayerAndAP layerAndAP = new LayerAndAP(layerParameter, additionalPremiumByLayer(incurredClaims, layerParameter, layerPremium));
//            additionalPremiumAllLayers.add(layerAndAP);
//        }
//        return additionalPremiumAllLayers;
//    }

    public IncurredLossAndApsAfterTermStructure cededIncurredAndApsRespectTerm(IAllContractClaimCache claimStore, IContractStructure scaledLayerParameters, PeriodScope periodScope, ContractCoverBase coverageBase, IPremiumPerPeriod premiumPerPeriod) {
        throw new SimulationException("Annual calculation was asked for a term calculation. It has no knowledge of this");
    }
}

