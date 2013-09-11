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
    public LossAfterClaimAndAnnualStructures layerCededIncurred(Collection<IClaimRoot> incurredClaims, IRiLayer layerParameters) {
        return new AnnualIncurredCalc().layerCededIncurred(incurredClaims, layerParameters);
    }

    public Collection<AdditionalPremium> additionalPremiumByLayer(double layerPremium, final IncurredLossWithTerm lossAndLayer, final IRiLayer layerParams) {
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();
        return annualIncurredCalc.additionalPremiumByLayer(layerPremium, lossAndLayer, layerParams);
    }

    /**
     * This method calculates the ceded incurred amount to a given period. It is left as a layer as abstraction in case
     * the incurred amount could (in the future) potentially change in a prior period.
     * <p/>
     * This implmentation basically delegates to {@link TermIncurredCalculation#cededIncurredToPeriod(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IContractStructure, org.pillarone.riskanalytics.core.simulation.engine.PeriodScope, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase, int, org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IPremiumPerPeriod) }
     *
     *
     *
     * @param claimStore
     * @param scaledLayerParameters
     * @param periodScope
     * @param coverageBase
     * @param premiumPerPeriod
     * @return
     */
    public IncurredLossAndApsAfterTermStructure cededIncurredAndApsRespectTerm(IAllContractClaimCache claimStore, IContractStructure scaledLayerParameters,
                                                                               PeriodScope periodScope,
                                                                               ContractCoverBase coverageBase, IPremiumPerPeriod premiumPerPeriod) {
//        Collection<IClaimRoot> incClaimsCurrentPeriod = claimStore.allIncurredClaimsInModelPeriod(periodScope.getCurrentPeriod(), periodScope, coverageBase);
        return cededIncurredToPeriod(claimStore, scaledLayerParameters, periodScope, coverageBase, periodScope.getCurrentPeriod(), premiumPerPeriod);

    }

//    public Collection<LayerAndAP> additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium) {
//        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();
//        return annualIncurredCalc.additionalPremiumAllLayers(incurredClaims, layerParameters, layerPremium);
//    }

    /**
     * This method calculates the amount ceded up to the current simulation  period. To do this it re-calculates
     * every period in order to the prior period, then calculates the amount incurred in this period.
     * <p/>
     * We can then check the term limit to the prior period, and the term limit to the current period... the difference between them should be what we
     * incur, respecting the term limit up to this period.
     * <p/>
     * </B> The crucial assumption is that incurred amounts in a model <I>cannot change</I> in different simulation periods; i.e that
     * incurred amounts are generated with perfect knowledge. </B>
     *
     * @param claimCache
     * @param layerParameters parameters (scaled if relative) for the contract in question
     * @param periodScope     {@link org.pillarone.riskanalytics.core.simulation.engine.PeriodScope}
     * @param coverageBase    {@link org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase}
     * @param periodTo        the simulation period to calculate to
     * @param periodPremium
     * @return the double vaule, given all the incurred claims in any period, which is ceded by the contract in the current simulation period.
     */
    public IncurredLossAndApsAfterTermStructure cededIncurredToPeriod(IAllContractClaimCache claimCache, IContractStructure layerParameters, PeriodScope periodScope,
                                                                ContractCoverBase coverageBase, int periodTo, IPremiumPerPeriod periodPremium) {

        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();

        double termIncurredInPriorPeriods = 0;
        Map<Integer, TermLossesAndAPs> lossesAndApsByPeriod = Maps.newHashMap();
        double termPriorPeriod = 0d;

        for (int period = 0; period <= periodTo; period++) {
            IncurredLoss temp = incurredLossOnly(claimCache, layerParameters, period, annualIncurredCalc, coverageBase, periodScope, periodPremium.getPremiumInPeriod(period));
            termIncurredInPriorPeriods += temp.getLossWithShareAppliedAllLayers();

            double cumulativeCededLoss = termPriorPeriod + temp.getLossWithShareAppliedAllLayers();
            double lossAfterTermStructurePrior = Math.min(Math.max(termPriorPeriod - layerParameters.getTermExcess(), 0), layerParameters.getTermLimit());
            double aLossAfterTermStructure = Math.min(Math.max(cumulativeCededLoss - layerParameters.getTermExcess(), 0), layerParameters.getTermLimit());
            double termLimitDifferenceToAllocate = cumulativeCededLoss - aLossAfterTermStructure;

            IncurredLossWithTerm incurredLossWithTerm =
                    new IncurredLossWithTerm(temp, aLossAfterTermStructure - lossAfterTermStructurePrior, termLimitDifferenceToAllocate, layerParameters, period);

            IncurredAPsWithTerm incrredAPsWithTerm = incurredAPsWithTerm(layerParameters, period, annualIncurredCalc, incurredLossWithTerm, periodPremium.getPremiumInPeriod(period));
            TermLossesAndAPs termLossesAndAPs = new TermLossesAndAPs(incurredLossWithTerm, incrredAPsWithTerm);
            lossesAndApsByPeriod.put(period, termLossesAndAPs);
            termPriorPeriod += temp.getLossWithShareAppliedAllLayers();
        }
        return new IncurredLossAndApsAfterTermStructure(lossesAndApsByPeriod, layerParameters);
    }

    public IncurredLoss incurredLossOnly(IAllContractClaimCache claimCache, IContractStructure layerParameters,
                                         int period, AnnualIncurredCalc annualIncurredCalc, ContractCoverBase base,
                                         PeriodScope periodScope, double periodPremium) {
        Collection<IClaimRoot> claimsInPeriod = claimCache.allIncurredClaimsInModelPeriod(period, periodScope, base);
        List<LayerParameters> layers = layerParameters.getLayers(period);
        Collection<IncurredLossAndLayer> lossesByLayer = Lists.newArrayList();
        for (IRiLayer layerParameter : layers) {
            LossAfterClaimAndAnnualStructures layerLoss = annualIncurredCalc.layerCededIncurred(claimsInPeriod, layerParameter);
            final IncurredLossAndLayer incurredLossAndLayer = new IncurredLossAndLayer(layerLoss, layerParameter);
            lossesByLayer.add(incurredLossAndLayer);
        }
        return new IncurredLoss(lossesByLayer);

    }

    public IncurredAPsWithTerm incurredAPsWithTerm(IContractStructure layerParameters,
                                         int period, AnnualIncurredCalc annualIncurredCalc, IncurredLossWithTerm incurredLossWithTerm,
                                         double periodPremium) {
        List<LayerParameters> layers = layerParameters.getLayers(period);
        Collection<AdditionalPremium> lossesByLayer = Lists.newArrayList();
        for (IRiLayer layerParameter : layers) {
            lossesByLayer.addAll(annualIncurredCalc.additionalPremiumByLayer(periodPremium, incurredLossWithTerm, layerParameter));
        }
        return new IncurredAPsWithTerm(lossesByLayer);

    }



    /**
     * @param claimCache
     * @param periodScope
     * @param layerParameters
     * @param coverageBase
     * @param claimsToSimulationPeriod
     * @param premiumByPeriod
     * @return
     */
    public Map<Integer, Double> cededIncurredsByPeriods(IAllContractClaimCache claimCache, PeriodScope periodScope, IContractStructure layerParameters, ContractCoverBase coverageBase, Integer claimsToSimulationPeriod, IPremiumPerPeriod premiumByPeriod) {
        Map<Integer, Double> incurredCededAmountByPeriod = new TreeMap<Integer, Double>();
        for (int contractPeriod = 0; contractPeriod <= periodScope.getCurrentPeriod(); contractPeriod++) {
            double incurredInPeriod = cededIncurredToPeriod(claimCache, layerParameters, periodScope,
                    coverageBase, contractPeriod, premiumByPeriod).getIncurredLossAfterTermStructure(contractPeriod).getIncurredLossAfterTermStructurte();
            incurredCededAmountByPeriod.put(contractPeriod, incurredInPeriod);
        }
        return incurredCededAmountByPeriod;
    }

}
