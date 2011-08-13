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
    private boolean updateAggregateFactorRequired = false;
    private DateTime allCounterPartiesDefaultAsOf = null;

    public CounterPartyState() {
        initialStateModified = false;
        counterPartyFactors = new HashMap<ILegalEntityMarker, TreeMap<DateTime, Double>>();
        coveredByReinsurersByDate = new TreeMap<DateTime, Double>();
    }

    public void addCounterPartyFactor(DateTime validAsOf, ILegalEntityMarker counterParty, double factor, boolean initialSetting) {
        updateAggregateFactorRequired = true;
        Map<DateTime, Double> counterPartyFactorsByDate = counterPartyFactors.get(counterParty);
        if (counterPartyFactorsByDate == null) {
            counterPartyFactorsByDate = new TreeMap<DateTime, Double>();
        }
        counterPartyFactorsByDate.put(validAsOf, factor);
        if (!initialSetting) {
            initialStateModified |= !initialSetting;
        }
    }

    private void updateAggregateFactor(DateTime validAsOf) {
        if (!updateAggregateFactorRequired) return;
        updateAggregateFactorRequired = false;
        double totalCoveredPortion = 0;
        for (TreeMap<DateTime, Double> entry : counterPartyFactors.values()) {
            totalCoveredPortion += entry.floorEntry(validAsOf).getValue();
        }
        if (totalCoveredPortion > 1) {
            for (TreeMap<DateTime, Double> entry : counterPartyFactors.values()) {
                entry.put(validAsOf, entry.floorEntry(validAsOf).getValue() / totalCoveredPortion);
            }
        }
        coveredByReinsurersByDate.put(validAsOf, totalCoveredPortion);
        allCounterPartiesDefaultAsOf = totalCoveredPortion == 0 ? validAsOf : null;
    }

    public double getCoveredByReinsurers(DateTime validAsOf) {
        if (coveredByReinsurersByDate.isEmpty()) return 1d;
        updateAggregateFactor(validAsOf);
        return coveredByReinsurersByDate.floorEntry(validAsOf).getValue();
    }

    public double getCoveredByReinsurer(DateTime validAsOf, ILegalEntityMarker counterParty) {
        if (counterPartyFactors.isEmpty()) return 1d;
        updateAggregateFactor(validAsOf);
        return counterPartyFactors.get(counterParty).floorEntry(validAsOf).getValue();
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
            factors.put(entry.getKey(), entry.getValue().floorEntry(validAsOf).getValue());
        }
        return factors;
    }
}
