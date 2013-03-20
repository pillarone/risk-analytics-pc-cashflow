package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class LossAfterTermStructure {

    final Map<Integer, IncurredLossAndAP> lossesByPeriod;
    final double incLossAfterTermStructureCurrentSimPeriod;
    final Integer period;

    public LossAfterTermStructure(Map<Integer, IncurredLossAndAP> lossesByPeriod, double loss, Integer period) {
        this.lossesByPeriod = lossesByPeriod;
        this.incLossAfterTermStructureCurrentSimPeriod = loss;
        this.period = period;
    }

    public Map<Integer, IncurredLossAndAP> getLayerLossesByPeriod() {
        return Collections.unmodifiableMap( lossesByPeriod );
    }

    public Map<Integer, Double> getPeriodLosses() {
        Map<Integer, Double> aMap = Maps.newHashMap();
        for (Map.Entry<Integer, IncurredLossAndAP> integerIncurredLossAndAPEntry : lossesByPeriod.entrySet()) {
            aMap.put(integerIncurredLossAndAPEntry.getKey(), integerIncurredLossAndAPEntry.getValue().getLoss());
        }
        return Collections.unmodifiableMap(aMap);
    }

    public double getIncLossAfterTermStructureCurrentSimPeriod() {
        return incLossAfterTermStructureCurrentSimPeriod;
    }

    public Integer getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return "LossAfterTermStructure{" +
                ", period=" + period +
                ", loss=" + incLossAfterTermStructureCurrentSimPeriod +
                "lossesByPeriod=" + lossesByPeriod +
                '}';
    }
}
