package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

import java.io.Serializable;
import java.util.*;

/**
 * Contains/Updates the time series of cover ratios for all counter parties
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CounterPartyState implements Serializable{

    private Map<ILegalEntityMarker, TreeMap<DateTime, Double>> counterPartyFactors;
    private TreeMap<DateTime, Double> coveredByReinsurersByDate;

    private boolean initialStateModified = false;
    private Map<DateTime, Boolean> updateAggregateFactorRequired;
    private DateTime allCounterPartiesDefaultAsOf = null;
    private DateTime initializationDate;

    public CounterPartyState() {
        initialStateModified = false;
        counterPartyFactors = new HashMap<ILegalEntityMarker, TreeMap<DateTime, Double>>();
        coveredByReinsurersByDate = new TreeMap<DateTime, Double>();
        updateAggregateFactorRequired = new HashMap<DateTime, Boolean>();
    }

    /**
     * copy c'tor
     * @param counterPartyState
     */
    public CounterPartyState(CounterPartyState counterPartyState) {
        this();
        initializationDate = counterPartyState.initializationDate;
        for (ILegalEntityMarker counterParty : counterPartyState.getCounterParties()) {
            double factor = counterPartyState.getCoveredByReinsurer(initializationDate, counterParty);
            addCounterPartyFactor(initializationDate, counterParty, factor, true);
        }
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
        else {
            initializationDate = validAsOf;
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

    /**
     * @param validAsOf
     * @return current covered ratio respecting original contract shares, default and recovery of every single counter party
     */
    public double getCoveredByReinsurers(DateTime validAsOf) {
        // no counter parties and cover portions are defined
        if (counterPartyFactors.isEmpty()) return 1d;
        // a default might have occurred and therefore the aggregate factor might be lower
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

    public boolean hasCounterParties() {
        return !(counterPartyFactors.isEmpty());
    }

    private Set<ILegalEntityMarker> getCounterParties() {
        return counterPartyFactors.keySet();
    }

    /**
     * @param validAsOf
     * @return adjusted factor for every counter party considering original weight and possible recovery rate
     */
    public Map<ILegalEntityMarker, Double> getFactors(DateTime validAsOf) {
        Map<ILegalEntityMarker, Double> factors = new HashMap<ILegalEntityMarker, Double>();
        double coveredByReinsurers = coveredByReinsurersByDate.isEmpty() ? 1 : getCoveredByReinsurers(validAsOf);
        for (Map.Entry<ILegalEntityMarker, TreeMap<DateTime, Double>> entry : counterPartyFactors.entrySet()) {
            if (entry.getValue() != null && entry.getValue().floorEntry(validAsOf) != null && coveredByReinsurers != 0d) {
                factors.put(entry.getKey(), entry.getValue().floorEntry(validAsOf).getValue() / coveredByReinsurers);
            }
        }
        return factors;
    }
}
