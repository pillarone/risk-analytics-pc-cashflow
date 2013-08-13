package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidCalculation {

    double layerCededPaid(Collection<ClaimCashflowPacket> layerCashflows, LayerParameters layerParameters);

    TermLossAndPaidAps cededIncrementalPaidRespectTerm(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters,
                                                       PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, boolean sanityChecks, Map<Integer, IncurredLossAndAP> incurredAPs, IPremiumPerPeriod premiumPerPeriod);

    AllLayersPaidLoss paidLossAllLayers(Collection<ClaimCashflowPacket> allLayerCashflows, Collection<LayerParameters> layerParameters);

    double cumulativePaidForPeriodIgnoreTermStructure(Collection<ClaimCashflowPacket> allPaidClaims, ScaledPeriodLayerParameters layerParameters,
                                                      PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, int period);


}
