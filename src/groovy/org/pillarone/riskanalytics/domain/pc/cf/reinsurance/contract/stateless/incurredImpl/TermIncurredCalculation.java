package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;


import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.AdditionalPremium;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.LayerAndAP;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.LossAndAP;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;

import java.util.*;

/**
 * A reinsurance calculation which respects term effects. Composed with of {@link org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.AnnualIncurredCalc}
 * to deal with the annual coverage calcs.
 */
public class TermIncurredCalculation implements IIncurredCalculation {

    /**
     * Calculates the ceded incurred to the layer. Relies on composition of the {@link org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.AnnualIncurredCalc}
     *
     * @param incurredClaims
     * @param layerParameters
     * @return
     */
    public double layerCededIncurred(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters) {
        return new AnnualIncurredCalc().layerCededIncurred(incurredClaims, layerParameters);
    }

    public Collection<AdditionalPremium> additionalPremiumByLayer(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters, double layerPremium) {
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();
        return annualIncurredCalc.additionalPremiumByLayer(incurredClaims, layerParameters, layerPremium);
    }

    /**
     * This method calculates the ceded incurred amount to a given period. It is left as a layer as abstraction in case
     * the incurred amount could (in the future) potentially change in a prior period.
     *
     * This implmentation basically delegates to {@link TermIncurredCalculation#cededIncurredToPeriod(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters, org.pillarone.riskanalytics.core.simulation.engine.PeriodScope, double, double, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase, int) }
     *
     *
     *
     * @param claimStore
     * @param scaledLayerParameters
     * @param periodScope
     * @param termExcess
     * @param termLimit
     * @param counter
     * @param coverageBase
     * @param contractPeriodPremium
     * @return
     */
    public LossAndAP cededIncurredRespectTerm(IAllContractClaimCache claimStore, ScaledPeriodLayerParameters scaledLayerParameters,
                                              PeriodScope periodScope, double termExcess, double termLimit, IPeriodCounter counter,
                                              ContractCoverBase coverageBase, double contractPeriodPremium) {
        Collection<IClaimRoot> incClaimsCurrentPeriod = claimStore.allIncurredClaimsInModelPeriod(periodScope.getCurrentPeriod(), periodScope, coverageBase);
        Collection<LayerAndAP> layerAndAPs = additionalPremiumAllLayers(incClaimsCurrentPeriod, scaledLayerParameters.getLayers(periodScope.getCurrentPeriod()), contractPeriodPremium);
        double loss = cededIncurredToPeriod(claimStore, scaledLayerParameters, periodScope, termExcess, termLimit, coverageBase, periodScope.getCurrentPeriod());
        return new LossAndAP(loss, layerAndAPs);
    }

    public Collection<LayerAndAP> additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium) {
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();
        return annualIncurredCalc.additionalPremiumAllLayers(incurredClaims, layerParameters, layerPremium);
    }

    /**
     * This method calculates the amount ceded up to the current simulation  period. To do this it re-calculates
     * every period in order to the prior period, then calculates the amount incurred in this period.
     *
     * We can then check the term limit to the prior period, and the term limit to the current period... the difference between them should be what we
     * incur, respecting the term limit up to this period.
     *
     * </B> The crucial assumption is that incurred amounts in a model <I>cannot change</I> in different simulation periods; i.e that
     * incurred amounts are generated with perfect knowledge. </B>
     *
     *
     * @param claimCache
     * @param layerParameters parameters (scaled if relative) for the contract in question
     * @param periodScope {@link org.pillarone.riskanalytics.core.simulation.engine.PeriodScope}
     * @param termExcess term excess (scaled) for the period contract
     * @param termLimit term limit for the period contract
     * @param coverageBase {@link org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase}
     * @param periodTo the simulation period to calculate to
     * @return the double vaule, given all the incurred claims in any period, which is ceded by the contract in the current simulation period.
     */
    public double cededIncurredToPeriod(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters, PeriodScope periodScope, double termExcess, double termLimit, ContractCoverBase coverageBase, int periodTo) {

        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();

        double termIncurredInPriorPeriods = 0;
        for (int period = 0; period < periodTo; period++) {
            termIncurredInPriorPeriods += incurredAmountInPeriod(claimCache, layerParameters, period, annualIncurredCalc, coverageBase, periodScope);
        }

        double annualIncurredThisPeriod = incurredAmountInPeriod(claimCache, layerParameters, periodTo, annualIncurredCalc, coverageBase, periodScope);

        double lossAfterTermStructure = Math.min(Math.max(termIncurredInPriorPeriods + annualIncurredThisPeriod - termExcess, 0), termLimit);
        double lossAfterTermStructurePriorPeriods = Math.min(Math.max(termIncurredInPriorPeriods - termExcess, 0), termLimit);
        return lossAfterTermStructure - lossAfterTermStructurePriorPeriods;
    }

    /**
     *
     *
     * @param claimCache
     * @param layerParameters
     * @param period
     * @param annualIncurredCalc
     * @param base
     * @param periodScope
     * @return
     */
    public double incurredAmountInPeriod(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters,
                                         int period, AnnualIncurredCalc annualIncurredCalc, ContractCoverBase base, PeriodScope periodScope) {
        double termIncurredInPeriod = 0;
        Collection<IClaimRoot> claimsInPeriod = claimCache.allIncurredClaimsInModelPeriod(period, periodScope, base);
        List<LayerParameters> layers = layerParameters.getLayers(period);
        for (LayerParameters layerParameter : layers) {
            termIncurredInPeriod += annualIncurredCalc.layerCededIncurred(claimsInPeriod, layerParameter);
        }
        return termIncurredInPeriod;
    }

    /**
     *
     *
     *
     * @param claimCache
     * @param periodScope
     * @param termExcess
     * @param termLimit
     * @param layerParameters
     * @param coverageBase
     * @param claimsToSimulationPeriod
     * @return
     */
    public Map<Integer, Double> cededIncurredsByPeriods(IAllContractClaimCache claimCache, PeriodScope periodScope, double termExcess, double termLimit, ScaledPeriodLayerParameters layerParameters, ContractCoverBase coverageBase, Integer claimsToSimulationPeriod) {
        Map<Integer, Double> incurredCededAmountByPeriod = new TreeMap<Integer, Double>();
        for (int contractPeriod = 0; contractPeriod <= periodScope.getCurrentPeriod(); contractPeriod++) {
            double incurredInPeriod = cededIncurredToPeriod(claimCache, layerParameters, periodScope, termExcess, termLimit, coverageBase, contractPeriod);
            incurredCededAmountByPeriod.put(contractPeriod, incurredInPeriod);
        }
        return incurredCededAmountByPeriod;
    }

}
