package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IContractStructure;

import java.util.Collections;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLossAndApsAfterTermStructure {

    final Map<Integer, TermLossesAndAPs> lossesByPeriodAfterAnnualLimits;
    final IContractStructure contractStructure;

    public IncurredLossAndApsAfterTermStructure(Map<Integer, TermLossesAndAPs> lossesByPeriod, final IContractStructure contractStructure) {
        this.lossesByPeriodAfterAnnualLimits = lossesByPeriod;
        this.contractStructure = contractStructure;
    }

    public Map<Integer, IncurredLoss> getLayerLossesByPeriodIgnoreTerm() {

        Map<Integer, IncurredLoss> lossesAPMap = Maps.newHashMap();
        for (Map.Entry<Integer, TermLossesAndAPs> anEntry : lossesByPeriodAfterAnnualLimits.entrySet()) {
            lossesAPMap.put(anEntry.getKey(), anEntry.getValue().getLossesWithTerm().getIncurredLoss());
        }
        return lossesAPMap;
    }

    public Map<Integer, IncurredAPsWithTerm> getLayerApsByPeriodWithTerm() {

        Map<Integer, IncurredAPsWithTerm> lossesAPMap = Maps.newHashMap();
        for (Map.Entry<Integer, TermLossesAndAPs> anEntry : lossesByPeriodAfterAnnualLimits.entrySet()) {
            lossesAPMap.put(anEntry.getKey(), anEntry.getValue().getAdditionalPremiums());
        }
        return lossesAPMap;
    }

    public Map<Integer, Double> getPeriodLosses() {
        Map<Integer, Double> aMap = Maps.newHashMap();
        for (Map.Entry<Integer, TermLossesAndAPs> integerIncurredLossAndAPEntry : lossesByPeriodAfterAnnualLimits.entrySet()) {
            aMap.put(integerIncurredLossAndAPEntry.getKey(), integerIncurredLossAndAPEntry.getValue().getLossesWithTerm().getIncurredLossAfterTermStructurte());
        }
        return Collections.unmodifiableMap(aMap);
    }

    public IncurredLossWithTerm getIncurredLossAfterTermStructure(int period) {
        IncurredLossWithTerm result = lossesByPeriodAfterAnnualLimits.get(period).getLossesWithTerm();
        if (result == null) {
            throw new SimulationException(" Failed to find losses against the term structure in period " + period);
        }
        return result;
    }


    @Override
    public String toString() {
        return
                "lossesByPeriod=" + lossesByPeriodAfterAnnualLimits;
    }
}
