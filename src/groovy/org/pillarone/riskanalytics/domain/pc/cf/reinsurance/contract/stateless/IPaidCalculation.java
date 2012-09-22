package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidCalculation {

    double layerCededPaid(Collection<ClaimCashflowPacket> layerCashflows, LayerParameters layerParameters);

    Map<Integer, Double> cededIncrementalPaidRespectTerm(List<ClaimCashflowPacket> allPaidClaims, ScaledPeriodLayerParameters layerParameters,
                                                         PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, DateTime fromDate, DateTime toDate, boolean sanityChecks);

    double paidLossAllLayers(Collection<ClaimCashflowPacket> allLayerCashflows, Collection<LayerParameters> layerParameters);

    double additionalPremiumByLayer(Collection<ClaimCashflowPacket> cashflowsByLayer, LayerParameters layerParameters, double layerPremium);

    double cumulativePaidForPeriodIgnoreTermStructure(Collection<ClaimCashflowPacket> allPaidClaims, ScaledPeriodLayerParameters layerParameters,
                                                      PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, int period);


}
