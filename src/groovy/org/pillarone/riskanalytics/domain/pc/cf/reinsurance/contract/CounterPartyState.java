package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CounterPartyState {

    private Map<ILegalEntityMarker, TreeMap<DateTime, Double>> counterPartyFactors;
    private TreeMap<DateTime, Double> coveredByReinsurersByDate;

    private boolean initialStateModified = false;
    private Map<DateTime, Boolean> updateAggregateFactorRequired;
    private DateTime allCounterPartiesDefaultAsOf = null;

    public CounterPartyState() {
        initialStateModified = false;
        counterPartyFactors = new HashMap<ILegalEntityMarker, TreeMap<DateTime, Double>>();
        coveredByReinsurersByDate = new TreeMap<DateTime, Double>();
        updateAggregateFactorRequired = new HashMap<DateTime, Boolean>();
    }

    public void addCounterPartyFactor(DateTime validAsOf, ILegalEntityMarker counterParty, double factor, boolean initialSetting) {
        updateAggregateFactorRequired.put(validAsOf, true);
        TreeMap<DateTime, Double> counterPartyFactorsByDate = counterPartyFactors.get(counterParty);
        if (counterPartyFactorsByDate == null) {
            counterPartyFactorsByDate = new TreeMap<DateTime, Double>();
            counterPartyFactors.put(counterParty, counterPartyFactorsByDate);
        }
        counterPartyFactorsByDate.put(validAsOf, factor);
        if (!initialSetting) {
            initialStateModified |= !initialSetting;
        }
        updateAggregateFactor(validAsOf);
    }

    /**
     * Updates allCounterPartiesDefaultAsOf, updateAggregateFactorRequired, counterPartyFactors if addCounterPartyFactor
     * has been executed since the last call of this function.
     * @param validAsOf update counterPartyFactors for this date
     */
    private void updateAggregateFactor(DateTime validAsOf) {
        if (updateAggregateFactorRequired.get(validAsOf) != null) {
            updateAggregateFactorRequired.put(validAsOf, false);
        }
        if (updateAggregateFactorRequired.get(validAsOf) != null || coveredByReinsurersByDate.isEmpty()) {
            double totalCoveredPortion = 0;
            for (TreeMap<DateTime, Double> entry : counterPartyFactors.values()) {
                if (entry.floorEntry(validAsOf) != null) {
                    totalCoveredPortion += entry.floorEntry(validAsOf).getValue();
                }
            }
            if (totalCoveredPortion > 1) {
                for (TreeMap<DateTime, Double> entry : counterPartyFactors.values()) {
                    entry.put(validAsOf, entry.floorEntry(validAsOf).getValue() / totalCoveredPortion);
                }
            }
            coveredByReinsurersByDate.put(validAsOf, totalCoveredPortion);
            allCounterPartiesDefaultAsOf = totalCoveredPortion == 0 ? validAsOf : null;
        }
    }

    public double getCoveredByReinsurers(DateTime validAsOf) {
        // no counter parties and cover portions are defined
        if (counterPartyFactors.isEmpty()) return 1d;
        updateAggregateFactor(validAsOf);
        if (coveredByReinsurersByDate.isEmpty()) return 1d;
        if (coveredByReinsurersByDate.floorEntry(validAsOf) == null) {
            return 0d;
        }
        else {
            return coveredByReinsurersByDate.floorEntry(validAsOf).getValue();
        }
    }

    public double getCoveredByReinsurer(DateTime validAsOf, ILegalEntityMarker counterParty) {
        updateAggregateFactor(validAsOf);
        if (counterPartyFactors.isEmpty()) return 1d;
        if (counterPartyFactors.get(counterParty).floorEntry(validAsOf) == null) {
            return 0d;
        }
        else {
            return counterPartyFactors.get(counterParty).floorEntry(validAsOf).getValue();
        }
    }

    public DateTime allCounterPartiesDefaultAfter() {
        return allCounterPartiesDefaultAsOf;
    }

    public boolean newInitializationRequired() {
        return initialStateModified;
    }

    public Map<ILegalEntityMarker, Double> getFactors(DateTime validAsOf) {
        Map<ILegalEntityMarker, Double> factors = new HashMap<ILegalEntityMarker, Double>();
        for (Map.Entry<ILegalEntityMarker, TreeMap<DateTime, Double>> entry : counterPartyFactors.entrySet()) {
            if (entry.getValue() != null && entry.getValue().floorEntry(validAsOf) != null) {
                factors.put(entry.getKey(), entry.getValue().floorEntry(validAsOf).getValue());
            }
        }
        return factors;
    }
}
