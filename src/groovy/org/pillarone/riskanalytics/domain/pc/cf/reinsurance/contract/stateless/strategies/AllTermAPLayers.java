package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IContractStructure;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;

import java.util.ArrayList;
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

    public AdditionalPremiumAndPaidTuple incrementalTermLoss(Map<Integer, Double> annualLosses, PeriodScope periodScope, final IContractStructure contractStructure) {
        double termLossNoStructurePriorPeriod = 0d;
        for (int i = 0; i < periodScope.getCurrentPeriod(); i++) {
            termLossNoStructurePriorPeriod += annualLosses.get(i);
        }

        double termLossNoStructureThisPeriod = termLossNoStructurePriorPeriod + annualLosses.get(periodScope.getCurrentPeriod());
        double lossAfterTermStructurePrior = Math.min(Math.max(termLossNoStructurePriorPeriod - contractStructure.getTermExcess(), 0), contractStructure.getTermLimit());
        double lossAfterTermStructureCurrent = Math.min(Math.max(termLossNoStructureThisPeriod - contractStructure.getTermExcess(), 0), contractStructure.getTermLimit());

        double incrementalTermAP = termLossAllLayers(lossAfterTermStructureCurrent) - termLossAllLayers(lossAfterTermStructurePrior);

        /* Create a dummy contract layer for the term layer...*/
        final ContractLayer contractLayer =  new ContractLayer(new YearLayerIdentifier(0d, 0d), 0d, 0d, 0d, 0d, 0d, 0d,
                new ArrayList<ReinstatementLayer>(), new ArrayList<AdditionalPremiumLayer>(), new ArrayList<ProfitCommissions>(), 0d);
        AdditionalPremium additionalPremium = new AdditionalPremium(incrementalTermAP, CalcAPBasis.TERM, contractLayer );
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
