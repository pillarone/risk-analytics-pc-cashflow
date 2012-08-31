package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.Collection;
import java.util.List;

public interface IIncurredCalculation {

    double layerCededIncurred(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters);

    double additionalPremiumByLayer(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters, double layerPremium);

    double additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium);

    double cededIncurredRespectTerm(List<IClaimRoot> incurredClaims, PeriodLayerParameters layerParameters, PeriodScope periodScope, double termExcess, double termLimit, IPeriodCounter counter, ContractCoverBase coverageBase);
}
