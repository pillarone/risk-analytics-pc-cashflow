package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;

import java.util.Collection;

public interface IIncurredCalculation {

    LossAfterClaimAndAnnualStructures layerCededIncurred(Collection<IClaimRoot> incurredClaims, IRiLayer layerParameters);

    Collection<AdditionalPremium> additionalPremiumByLayer(double layerPremium, final IncurredLossWithTerm lossAndLayer, final IRiLayer layerParams);

    IncurredLossAndApsAfterTermStructure cededIncurredAndApsRespectTerm(IAllContractClaimCache claimStore, IContractStructure scaledLayerParameters, PeriodScope periodScope, ContractCoverBase coverageBase, IPremiumPerPeriod premiumPerPeriod);

//    Collection<LayerAndAP> additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium);
}
