package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
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
     * This implmentation basically delegates to {@link TermIncurredCalculation#cededIncurredToPeriod(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters, org.pillarone.riskanalytics.core.simulation.engine.PeriodScope, double, double, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase, int, java.util.Map }
     *
     *
     *
     *
     *
     *
     *
     * @param claimStore
     * @param scaledLayerParameters
     * @param periodScope
     * @param termExcess
     * @param termLimit
     * @param coverageBase
     * @param premiumPerPeriod
     * @return
     */
    public LossAfterTermStructure cededIncurredRespectTerm(IAllContractClaimCache claimStore, ScaledPeriodLayerParameters scaledLayerParameters,
                                                           PeriodScope periodScope, double termExcess, double termLimit,
                                                           ContractCoverBase coverageBase, Map<Integer, Double> premiumPerPeriod) {
        Collection<IClaimRoot> incClaimsCurrentPeriod = claimStore.allIncurredClaimsInModelPeriod(periodScope.getCurrentPeriod(), periodScope, coverageBase);

        return cededIncurredToPeriod(claimStore, scaledLayerParameters, periodScope, termExcess, termLimit, coverageBase, periodScope.getCurrentPeriod(), premiumPerPeriod);
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
     *
     *
     *
     * @param claimCache
     * @param layerParameters parameters (scaled if relative) for the contract in question
     * @param periodScope {@link org.pillarone.riskanalytics.core.simulation.engine.PeriodScope}
     * @param termExcess term excess (scaled) for the period contract
     * @param termLimit term limit for the period contract
     * @param coverageBase {@link org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase}
     * @param periodTo the simulation period to calculate to
     * @param periodPremium
     * @return the double vaule, given all the incurred claims in any period, which is ceded by the contract in the current simulation period.
     */
    public LossAfterTermStructure cededIncurredToPeriod(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters, PeriodScope periodScope, double termExcess, double termLimit, ContractCoverBase coverageBase, int periodTo, Map<Integer, Double> periodPremium) {

        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();

        double termIncurredInPriorPeriods = 0;
        Map<Integer, IncurredLossAndAP> lossesByPeriod = Maps.newHashMap();
        for (int period = 0; period < periodTo; period++) {
            IncurredLossAndAP temp = incurredLossAndAP(claimCache, layerParameters, period, annualIncurredCalc, coverageBase, periodScope, periodPremium.get(period));
            termIncurredInPriorPeriods += temp.getLoss();
            lossesByPeriod.put(period, temp);
        }

        IncurredLossAndAP annualIncurredThisPeriod = incurredLossAndAP(claimCache, layerParameters, periodTo, annualIncurredCalc, coverageBase, periodScope, periodPremium.get(periodScope.getCurrentPeriod()));
        lossesByPeriod.put(periodTo, annualIncurredThisPeriod);

        double lossAfterTermStructure = Math.min(Math.max(termIncurredInPriorPeriods + annualIncurredThisPeriod.getLoss() - termExcess, 0), termLimit);
        double lossAfterTermStructurePriorPeriods = Math.min(Math.max(termIncurredInPriorPeriods - termExcess, 0), termLimit);
        double lossAfterTermStructureThisPeriod =  lossAfterTermStructure - lossAfterTermStructurePriorPeriods;
        return new LossAfterTermStructure(lossesByPeriod, lossAfterTermStructureThisPeriod, periodTo);
    }

    /**
     *
     *
     *
     *
     * @param claimCache
     * @param layerParameters
     * @param period
     * @param annualIncurredCalc
     * @param base
     * @param periodScope
     * @param periodPremium
     * @return
     */
    public IncurredLossAndAP incurredLossAndAP(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters,
                                               int period, AnnualIncurredCalc annualIncurredCalc, ContractCoverBase base, PeriodScope periodScope, double periodPremium) {
        double incurredInPeriod = 0;
        Collection<IClaimRoot> claimsInPeriod = claimCache.allIncurredClaimsInModelPeriod(period, periodScope, base);
        List<LayerParameters> layers = layerParameters.getLayers(period);

        Collection<IncurredLossAndLayer> lossesByLayer = Lists.newArrayList();
        for (LayerParameters layerParameter : layers) {
            double layerLoss = annualIncurredCalc.layerCededIncurred(claimsInPeriod, layerParameter);
            lossesByLayer.add(new IncurredLossAndLayer(layerLoss, layerParameter));
        }
        Collection<LayerAndAP> additionalPremiums = Lists.newArrayList();
        additionalPremiums = additionalPremiumAllLayers(claimsInPeriod, layers, periodPremium);

        final IncurredLossAndAP incurredLossAndAP = new IncurredLossAndAP(lossesByLayer, additionalPremiums);
        incurredLossAndAP.setAPDates(periodScope.getPeriodCounter());
        return incurredLossAndAP;
    }

    /**
     *
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
     * @param premiumByPeriod
     * @return
     */
    public Map<Integer, Double> cededIncurredsByPeriods(IAllContractClaimCache claimCache, PeriodScope periodScope, double termExcess, double termLimit, ScaledPeriodLayerParameters layerParameters, ContractCoverBase coverageBase, Integer claimsToSimulationPeriod, Map<Integer, Double> premiumByPeriod) {
        Map<Integer, Double> incurredCededAmountByPeriod = new TreeMap<Integer, Double>();
        for (int contractPeriod = 0; contractPeriod <= periodScope.getCurrentPeriod(); contractPeriod++) {
            double incurredInPeriod = cededIncurredToPeriod(claimCache, layerParameters, periodScope, termExcess, termLimit,
                    coverageBase, contractPeriod, premiumByPeriod).getIncLossAfterTermStructureCurrentSimPeriod();
            incurredCededAmountByPeriod.put(contractPeriod, incurredInPeriod);
        }
        return incurredCededAmountByPeriod;
    }

}
