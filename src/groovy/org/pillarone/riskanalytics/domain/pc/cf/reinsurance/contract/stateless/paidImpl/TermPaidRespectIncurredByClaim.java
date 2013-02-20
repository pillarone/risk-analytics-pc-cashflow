package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.global.SimulationConstants;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.TermIncurredCalculation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class TermPaidRespectIncurredByClaim implements IPaidCalculation {

    private static Log LOG = LogFactory.getLog(TermPaidRespectIncurredByClaim.class);

    public double layerCededPaid(Collection<ClaimCashflowPacket> layerCashflows, LayerParameters layerParameters) {
        double lossAfterAnnualStructure = lossAfterAnnualStructure(layerCashflows, layerParameters);

        double lossAfterShareAndProRata = lossAfterAnnualStructure * layerParameters.getShare();

        return lossAfterShareAndProRata;
    }

    public double cumulativePaidForPeriodIgnoreTermStructure(Collection<ClaimCashflowPacket> allPaidClaims, ScaledPeriodLayerParameters layerParameters, PeriodScope periodScope, ContractCoverBase coverageBase, double termLimit, double termExcess, int period) {
        return 0d;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TermLossAndPaidAps cededIncrementalPaidRespectTerm(IAllContractClaimCache claimCache, ScaledPeriodLayerParameters layerParameters,
                                                              PeriodScope periodScope, ContractCoverBase coverageBase,
                                                              double termLimit, double termExcess, boolean sanityChecks, Map<Integer, IncurredLossAndAP> incurredAPs, Map<Integer, Double> premiumPerPeriod) {

        TermLossAndLossByLayer paidByPeriodUpToFilterFromDate = cededCumulativePaidRespectTerm(periodScope.getCurrentPeriod() - 1, layerParameters, periodScope, coverageBase, termLimit, termExcess, claimCache, coverageBase, premiumPerPeriod);
        TermLossAndLossByLayer cumulativePaidToDate = cededCumulativePaidRespectTerm(periodScope.getCurrentPeriod(), layerParameters, periodScope, coverageBase, termLimit, termExcess, claimCache, coverageBase, premiumPerPeriod);
        Map<Integer, Double> paidByPeriod = incrementalTermLossByPeriod(sanityChecks, paidByPeriodUpToFilterFromDate, cumulativePaidToDate);
        Collection<PaidAdditionalPremium> paidAps = calculateIncrementalPaidAPs(incurredAPs, paidByPeriodUpToFilterFromDate, cumulativePaidToDate, periodScope.getCurrentPeriod(), periodScope.getPeriodCounter());
        for (PaidAdditionalPremium paidAp : paidAps) {
            paidAp.setDate(periodScope.getPeriodCounter().getCurrentPeriodEnd().minusDays(1));
        }

        return new TermLossAndPaidAps(paidByPeriod, paidAps);
    }

    private Collection<PaidAdditionalPremium> calculateIncrementalPaidAPs(Map<Integer, IncurredLossAndAP> incurredAPs, TermLossAndLossByLayer paidByPeriodUpToFilterFromDate, TermLossAndLossByLayer cumulativePaidToDate, Integer currentPeriod, IPeriodCounter periodCounter) {

        Collection<PaidAdditionalPremium> apsPaidThisPeriod = Lists.newArrayList();
//        For each simulation period
        for (Map.Entry<Integer, IncurredLossAndAP> period : incurredAPs.entrySet()) {
            Collection<LayerAndAP> layers = period.getValue().getAps();
            AllLayersPaidLoss lastSimPeriodCumPaid = paidByPeriodUpToFilterFromDate.getPaidLossesByLayer().get(period.getKey());
            AllLayersPaidLoss thisSimPeriodCumPaid = cumulativePaidToDate.getPaidLossesByLayer().get(period.getKey());
//            And each layer in this contract
            for (LayerAndAP layer : layers) {
//                Then for each incurred additional premium
                for (AdditionalPremium additionalPremium : layer.getAdditionalPremiums()) {
                    if(additionalPremium.getAdditionalPremium() == 0d) {
                        continue;
                    }
                    if (additionalPremium.getPremiumType().equals(APBasis.NCB)) {
                        if (period.getKey().equals(currentPeriod)) {
                            PaidAdditionalPremium ncbAP = new PaidAdditionalPremium(additionalPremium.getAdditionalPremium(), additionalPremium);
                            apsPaidThisPeriod.add(ncbAP);
                        }
                        continue;
                    }
                    LayerParameters.LayerIdentifier identifier = layer.getLayerParameters().getLayerIdentifier();
                    IncurredLossAndLayer lossAndLayer = period.getValue().getLayerAndIncurredLoss(identifier);
                    double cumPaidApLastPeriod = 0;
                    if(lastSimPeriodCumPaid != null) {
                        LayerAndPaidLoss lastSimPeriodLayerLoss = lastSimPeriodCumPaid.getLayerOrNull(identifier);
                        cumPaidApLastPeriod = (lastSimPeriodLayerLoss == null ? 0 : lastSimPeriodLayerLoss.getPaidLoss()) * additionalPremium.getAdditionalPremium() / lossAndLayer.getLoss();
                    }
                    LayerAndPaidLoss thisSimPeriodLayerLoss = thisSimPeriodCumPaid.getLayerOrNull(identifier);
                    double cumPaidApThisPeriod = thisSimPeriodLayerLoss.getPaidLoss() * additionalPremium.getAdditionalPremium() / lossAndLayer.getLoss();
                    final PaidAdditionalPremium paidAdditionalPremium = new PaidAdditionalPremium(cumPaidApThisPeriod - cumPaidApLastPeriod, additionalPremium);
                    paidAdditionalPremium.setDate( periodCounter.getCurrentPeriodEnd() );
                    apsPaidThisPeriod.add(paidAdditionalPremium);
                }
            }
        }
        return apsPaidThisPeriod;
    }

    private Map<Integer, Double> incrementalTermLossByPeriod(boolean sanityChecks, TermLossAndLossByLayer paidByPeriodUpToFilterFromDate, TermLossAndLossByLayer cumulativePaidToDate) {
        Map<Integer, Double> termLossBeforeThisSimPeriod = paidByPeriodUpToFilterFromDate.getTermLosses();
        Map<Integer, Double> termLossIncludingThisSimPeriod = cumulativePaidToDate.getTermLosses();

        Map<Integer, Double> paidByPeriod = new TreeMap<Integer, Double>();

        for (int modelPeriod = 0; modelPeriod < termLossIncludingThisSimPeriod.size(); modelPeriod++) {
//          It is possible nothing is entered for the map, which may only run to the end of a prior period.
            if (termLossBeforeThisSimPeriod.get(modelPeriod) == null) {
                paidByPeriod.put(modelPeriod, termLossIncludingThisSimPeriod.get(modelPeriod));
            } else {
                double paidPriorSimPeriod = termLossBeforeThisSimPeriod.get(modelPeriod);
                double paidToCurrentSimPoint = termLossIncludingThisSimPeriod.get(modelPeriod);
                double cumPaid = paidToCurrentSimPoint - paidPriorSimPeriod;
                if (cumPaid < -SimulationConstants.EPSILON) {

                    String message = "Insanity detected: incremental paid amount in model period : " + modelPeriod + " is calculated as negative. " + cumPaid +
                            ". Contact support. ";
                    LOG.error(message);
                    if (sanityChecks) {
                        throw new SimulationException(message);
                    }
                }
                paidByPeriod.put(modelPeriod, cumPaid);
            }
        }
        return paidByPeriod;
    }

    public Collection<PaidAdditionalPremium> paidAdditionalPremium(Collection<ClaimCashflowPacket> layerCashflows, LayerAndAP layerAps, IIncurredCalculation annualCalc) {
        Collection<PaidAdditionalPremium> paidAps = Lists.newArrayList();
        for (AdditionalPremium additionalPremium : layerAps.getAdditionalPremiums()) {
            if (additionalPremium.getPremiumType().equals(APBasis.NCB)) {
                paidAps.add(new PaidAdditionalPremium(additionalPremium.getAdditionalPremium(), additionalPremium));
            }
            double layerPaid = layerCededPaid(layerCashflows, layerAps.getLayerParameters());
            Collection<IClaimRoot> incurredClaims = RIUtilities.incurredClaims(layerCashflows, IncurredClaimBase.BASE);
            double incurredLossAfterAnnualStructure = annualCalc.layerCededIncurred(incurredClaims, layerAps.getLayerParameters());
            double paidAP = additionalPremium.getAdditionalPremium() * layerPaid / incurredLossAfterAnnualStructure;
            paidAps.add(new PaidAdditionalPremium(paidAP, additionalPremium));
        }
        return paidAps;
    }


    public TermLossAndLossByLayer cededCumulativePaidRespectTerm(Integer claimsToSimulationPeriod, ScaledPeriodLayerParameters layerParameters, PeriodScope periodScope,
                                                                 ContractCoverBase coverageBase, double termLimit, double termExcess, IAllContractClaimCache claimCache, ContractCoverBase coverBase, Map<Integer, Double> premiumPerPeriod) {
        if (claimsToSimulationPeriod == -1) {
            final HashMap<Integer, Double> integerDoubleHashMap = new HashMap<Integer, Double>();
            integerDoubleHashMap.put(0, 0d);
            HashMap<Integer, AllLayersPaidLoss> byLayer = Maps.newHashMap();
            byLayer.put(0, new AllLayersPaidLoss(new ArrayList<LayerAndPaidLoss>()));
            return new TermLossAndLossByLayer(integerDoubleHashMap, byLayer);
        }
        TermIncurredCalculation incCalc = new TermIncurredCalculation();
        Map<Integer, Double> cededIncurredByPeriod = incCalc.cededIncurredsByPeriods(claimCache, periodScope, termExcess, termLimit, layerParameters, coverageBase, claimsToSimulationPeriod, premiumPerPeriod);

        Map<Integer, AllLayersPaidLoss> allPaidIncludingThisPeriod = cededPaidByUnderwritingPeriod(periodScope, layerParameters, coverageBase, periodScope.getCurrentPeriod(), termExcess, termLimit, claimCache, claimsToSimulationPeriod, coverBase);
        Map<Integer, Double> allPaidToDateRespectIncurredTerm = imposeIncurredLimits(cededIncurredByPeriod, allPaidIncludingThisPeriod);

        return new TermLossAndLossByLayer(allPaidToDateRespectIncurredTerm, allPaidIncludingThisPeriod);
    }

    /**
     * This method calculates the cumulative ceded amounts (by model period). It inspects the total amount ceded across the entire
     * simulation to determine if the term excess is breached, and then begins allocating paid amounts to periods.
     *
     * @param periodScope
     * @param layerParameters
     * @param base
     * @param toUnderwritingPeriod
     * @param termExcess
     * @param termLimit
     * @param claimCache
     * @param claimsToSimulationPeriod
     * @param coverBase
     * @return
     */
    private Map<Integer/*Simulation Period */  , Map<Integer /* Underwriting period */ , AllLayersPaidLoss>> cacheSimPeriodUwPeriodResult = Maps.newHashMap();

    public Map<Integer, AllLayersPaidLoss> cededPaidByUnderwritingPeriod(PeriodScope periodScope,
                                                                         ScaledPeriodLayerParameters layerParameters,
                                                                         ContractCoverBase base,
                                                                         int toUnderwritingPeriod,
                                                                         double termExcess,
                                                                         double termLimit,
                                                                         IAllContractClaimCache claimCache, Integer claimsToSimulationPeriod, ContractCoverBase coverBase) {
        if (cacheSimPeriodUwPeriodResult.get(claimsToSimulationPeriod) != null) {
            return cacheSimPeriodUwPeriodResult.get(claimsToSimulationPeriod);
        }
        Map<Integer, AllLayersPaidLoss> period_paid = Maps.newHashMap();

        /* As it stands, the spec takes no notice of the term excess when calculating payments.
For the moment, ignore it here too. Set to falase to enable functionality. Not guaranteed to work.  */
        boolean termExcessExceeded = true;
        double cumulativePaidInSimulation = 0d;
        for (int uwPeriod = 0; uwPeriod <= Math.min(toUnderwritingPeriod, claimsToSimulationPeriod); uwPeriod++) {
            if (termExcessExceeded) {
                Collection<ClaimCashflowPacket> cashflowsPaidAgainsThisModelPeriod = claimCache.cashflowsByUnderwritingPeriodUpToSimulationPeriod(claimsToSimulationPeriod, uwPeriod, periodScope, coverBase);
                Collection<ClaimCashflowPacket> latestCashflowsInPeriod = RIUtilities.latestCashflowByIncurredClaim(cashflowsPaidAgainsThisModelPeriod, IncurredClaimBase.BASE);
                Collection<LayerParameters> layers = layerParameters.getLayers(uwPeriod);
                AllLayersPaidLoss paidLossToModelPeriod = paidLossAllLayers(latestCashflowsInPeriod, layers);
                period_paid.put(uwPeriod, paidLossToModelPeriod);
                continue;
            }

/*            double incrementalPaidSimPeriod = cededPaidUpToSimulationPeriod(claimsToSimPeriod, layerParameters, periodScope, termExcess, termLimit, base, period);
            cumulativePaidInSimulation += incrementalPaidSimPeriod;

            if (cumulativePaidInSimulation >= termExcess) {
                termExcessExceeded = true;
                period_paid.put(period, incrementalPaidSimPeriod);

            } else {
                period_paid.put(period, 0d);
            } */
        }
        cacheSimPeriodUwPeriodResult.put(claimsToSimulationPeriod, period_paid);
        return cacheSimPeriodUwPeriodResult.get(claimsToSimulationPeriod);
    }

    /**
     * This method calculated the incremental paid amount (respecting the term limit) across the entire simulation.
     *
     * @param allCashflows
     * @param layerParameters
     * @param periodScope
     * @param termExcess
     * @param termLimit
     * @param coverageBase
     * @param periodTo
     * @return
     */

    public double cededPaidUpToSimulationPeriod(Collection<ClaimCashflowPacket> allCashflows, ScaledPeriodLayerParameters layerParameters,
                                                PeriodScope periodScope, double termExcess, double termLimit, ContractCoverBase coverageBase, int periodTo) {

        IncurredClaimBase claimBase = IncurredClaimBase.BASE;

        double termPaidPriorPeriod = 0;
        for (int period = 0; period < periodTo; period++) {
            Collection<ClaimCashflowPacket> cashflowsPaidAgainsThisModelPeriod = GRIUtilities.cashflowsCoveredInModelPeriod(allCashflows, periodScope, coverageBase, period);
            Collection<ClaimCashflowPacket> latestCashflowsInPeriod = RIUtilities.latestCashflowByIncurredClaim(cashflowsPaidAgainsThisModelPeriod, claimBase);
            Collection<LayerParameters> layers = layerParameters.getLayers(period);
            termPaidPriorPeriod += paidLossAllLayers(latestCashflowsInPeriod, layers).paidLossAllLayers();
        }

        Collection<ClaimCashflowPacket> cashflowsPaidAgainsThisModelPeriod = GRIUtilities.cashflowsCoveredInModelPeriod(allCashflows, periodScope, coverageBase, periodTo);
        Collection<ClaimCashflowPacket> latestCashflowsInPeriod = RIUtilities.latestCashflowByIncurredClaim(cashflowsPaidAgainsThisModelPeriod, claimBase);
        List<LayerParameters> layers = layerParameters.getLayers(periodTo);
        double paidLossThisPeriod = paidLossAllLayers(latestCashflowsInPeriod, layers).paidLossAllLayers();

        double lossAfterTermStructure = Math.min(Math.max(termPaidPriorPeriod + paidLossThisPeriod - termExcess, 0), termLimit);
        double lossAfterTermStructurePriorPeriods = Math.min(Math.max(termPaidPriorPeriod - termExcess, 0), termLimit);
        return lossAfterTermStructure - lossAfterTermStructurePriorPeriods;

    }

    /**
     * This method accepts all cashflows in all layers and calculates the amount ceded by contract.
     *
     * @param allLayerCashflows
     * @param layerParameters
     * @return
     */
    public AllLayersPaidLoss paidLossAllLayers(Collection<ClaimCashflowPacket> allLayerCashflows, Collection<LayerParameters> layerParameters) {
        double paidLoss = 0;
        Collection<LayerAndPaidLoss> layerAndPaidLosses = Lists.newArrayList();
        for (LayerParameters layerParameter : layerParameters) {
            double paidLayerLoss = layerCededPaid(allLayerCashflows, layerParameter);
            LayerAndPaidLoss layerAndPaidLoss = new LayerAndPaidLoss(paidLayerLoss, layerParameter);
            layerAndPaidLosses.add(layerAndPaidLoss);
        }
        return new AllLayersPaidLoss(layerAndPaidLosses);
    }

    /**
     * This calculates the amount ceded respecting the annual structure.
     *
     * @param layerCashflows
     * @param layerParameters
     * @return
     */
    public double lossAfterAnnualStructure(Collection<ClaimCashflowPacket> layerCashflows, LayerParameters layerParameters) {
        double lossAfterClaimStructure = 0;
        for (ClaimCashflowPacket aClaim : layerCashflows) {
            lossAfterClaimStructure += Math.min(Math.max(aClaim.getPaidCumulatedIndexed() - layerParameters.getClaimExcess(), 0), layerParameters.getClaimLimit());
        }
        return Math.min(Math.max(lossAfterClaimStructure - layerParameters.getLayerPeriodExcess(), 0), layerParameters.getLayerPeriodLimit());
    }

    public Map<Integer, Double> imposeIncurredLimits(Map<Integer, Double> incurredLimits, Map<Integer, AllLayersPaidLoss> paidAmounts) {
        Map<Integer, Double> paidAmountRespectingIncurred = new TreeMap<Integer, Double>();

        for (Map.Entry<Integer, AllLayersPaidLoss> entry : paidAmounts.entrySet()) {
            double incurredLimitInPeriod = incurredLimits.get(entry.getKey());
            double paidAmountInPeriod = entry.getValue().paidLossAllLayers();
            paidAmountRespectingIncurred.put(entry.getKey(), Math.min(paidAmountInPeriod, incurredLimitInPeriod));
        }
        return paidAmountRespectingIncurred;
    }
}
