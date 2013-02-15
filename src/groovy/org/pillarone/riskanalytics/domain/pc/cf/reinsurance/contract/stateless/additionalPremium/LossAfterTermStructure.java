package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class LossAfterTermStructure {

    final Map<Integer, IncurredLossAndAP> lossesByPeriod;
    final double lossAfterTermStructure;
    final Integer period;

    public LossAfterTermStructure(Map<Integer, IncurredLossAndAP> lossesByPeriod, double loss, Integer period) {
        this.lossesByPeriod = lossesByPeriod;
        this.lossAfterTermStructure = loss;
        this.period = period;
    }

    public Map<Integer, IncurredLossAndAP> getLossesByPeriod() {
        return Collections.unmodifiableMap( lossesByPeriod );
    }

    public double getLossAfterTermStructure() {
        return lossAfterTermStructure;
    }

    public Integer getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return "LossAfterTermStructure{" +
                ", period=" + period +
                ", loss=" + lossAfterTermStructure +
                "lossesByPeriod=" + lossesByPeriod +
                '}';
    }
}
