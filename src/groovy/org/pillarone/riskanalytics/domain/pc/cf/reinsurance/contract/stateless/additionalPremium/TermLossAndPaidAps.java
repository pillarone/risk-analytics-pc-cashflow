package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class TermLossAndPaidAps {

    final Map<Integer, Double> termLosses;
    final Collection<PaidAdditionalPremium> paidAPs;

    public TermLossAndPaidAps(Map<Integer, Double> termLosses, Collection<PaidAdditionalPremium> paidAPs) {
        this.termLosses = termLosses;
        this.paidAPs = paidAPs;
    }

    public Map<Integer, Double> getTermLosses() {
        return Collections.unmodifiableMap( termLosses );
    }

    public Collection<PaidAdditionalPremium> getPaidAPs() {
        return Collections.unmodifiableCollection(paidAPs);
    }

    public double termLoss() {
        double loss = 0d;
        for (Map.Entry<Integer, Double> integerDoubleEntry : termLosses.entrySet()) {
            loss += integerDoubleEntry.getValue();
        }
        return loss;
    }

    public double termAPs() {
        double aps = 0d;
        for (PaidAdditionalPremium paidAP : paidAPs) {
            aps += paidAP.getPaidAmount();
        }
        return aps;
    }

    @Override
    public String toString() {
        return "TermLossAndPaidAps{" +
                "termLosses=" + termLoss() +
                ", paidAPs=" + termAPs() +
                '}';
    }
}
