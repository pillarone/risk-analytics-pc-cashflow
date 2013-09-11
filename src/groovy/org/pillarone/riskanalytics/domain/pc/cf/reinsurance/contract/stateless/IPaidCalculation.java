package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;

import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidCalculation {

    LossAfterClaimAndAnnualStructures layerCededPaid(Collection<ClaimCashflowPacket> layerCashflows, IRiLayer layerParameters);

    TermLossAndPaidAps cededIncrementalPaidRespectTerm(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters,
                                                       PeriodScope periodScope, ContractCoverBase coverageBase, boolean sanityChecks, IncurredLossAndApsAfterTermStructure lossAfterTermStructure, IPremiumPerPeriod premiumPerPeriod);

    AllLayersPaidLoss paidLossAllLayers(Collection<ClaimCashflowPacket> allLayerCashflows, Collection<LayerParameters> layerParameters);

    double cumulativePaidForPeriodIgnoreTermStructure(Collection<ClaimCashflowPacket> allPaidClaims, ScaledPeriodLayerParameters layerParameters,
                                                      PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, int period);


}
