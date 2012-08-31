package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidCalculation {

    double layerCededPaid(Collection<ClaimCashflowPacket> layerCashflows, LayerParameters layerParameters);

    double cededIncrementalPaidRespectTerm(Collection<ClaimCashflowPacket> allPaidClaims, PeriodLayerParameters layerParameters, PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess);

    double paidLossAllLayers(Collection<ClaimCashflowPacket> allLayerCashflows, Collection<LayerParameters> layerParameters);

    double additionalPremiumByLayer(Collection<ClaimCashflowPacket> cashflowsByLayer, LayerParameters layerParameters, double layerPremium);

    double cumulativePaidForPeriodIgnoreTermStructure(Collection<ClaimCashflowPacket> allPaidClaims, PeriodLayerParameters layerParameters,
                                                      PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, int period);


}
