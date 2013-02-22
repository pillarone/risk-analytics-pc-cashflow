package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.AdditionalPremium;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.AdditionalPremiumAndPaidTuple;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.PaidAdditionalPremium;

import java.util.Collection;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class AllTermAPLayers {

    final Collection<TermLayer> termLayers;

    double termLossAllLayers(double loss) {
        double termLoss = 0d;
        for (TermLayer termLayer : termLayers) {
            termLoss += termLayer.getLossFromThisLayer(loss);
        }
        return termLoss;
    }

    public AdditionalPremiumAndPaidTuple incrementalTermLoss(Map<Integer, Double> annualLosses, double termLimit, double termExess, PeriodScope periodScope) {
        double termLossNoStructurePriorPeriod = 0d;
        for (int i = 0; i < periodScope.getCurrentPeriod(); i++) {
            termLossNoStructurePriorPeriod += annualLosses.get(i);
        }

        double termLossNoStructureThisPeriod = termLossNoStructurePriorPeriod + annualLosses.get(periodScope.getCurrentPeriod());
        double lossAfterTermStructurePrior = Math.min(Math.max(termLossNoStructurePriorPeriod - termExess, 0), termLimit);
        double lossAfterTermStructureCurrent = Math.min(Math.max(termLossNoStructureThisPeriod - termExess, 0), termLimit);

        double incrementalTermAP = termLossAllLayers(lossAfterTermStructureCurrent) - termLossAllLayers(lossAfterTermStructurePrior);
        AdditionalPremium additionalPremium = new AdditionalPremium(incrementalTermAP, APBasis.TERM);
        DateTime dateTime = additionalPremium.getPremiumType().getAPDate(periodScope.getPeriodCounter());
        additionalPremium.setDate(dateTime);
        PaidAdditionalPremium paidAdditionalPremium = new PaidAdditionalPremium(incrementalTermAP, additionalPremium);
        paidAdditionalPremium.setDate(dateTime);
        return new AdditionalPremiumAndPaidTuple(additionalPremium, paidAdditionalPremium);

    }

    public AllTermAPLayers(Collection<TermLayer> termLayers) {
        this.termLayers = termLayers;
    }

    public Collection<TermLayer> getTermLayers() {
        return termLayers;
    }
}
