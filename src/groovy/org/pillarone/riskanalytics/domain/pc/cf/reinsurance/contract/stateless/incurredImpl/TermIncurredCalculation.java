package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;


import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.*;

public class TermIncurredCalculation implements IIncurredCalculation {

    public double layerCededIncurred(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters) {
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();
        return annualIncurredCalc.layerCededIncurred(incurredClaims, layerParameters);

    }

    public double additionalPremiumByLayer(Collection<IClaimRoot> incurredClaims, LayerParameters layerParameters, double layerPremium) {
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();
        return annualIncurredCalc.additionalPremiumByLayer(incurredClaims, layerParameters, layerPremium);
    }

    public double additionalPremiumAllLayers(Collection<IClaimRoot> incurredClaims, Collection<LayerParameters> layerParameters, double layerPremium) {

        double additionalPremium = 0;
        for (LayerParameters layerParameter : layerParameters) {
            additionalPremium += additionalPremiumByLayer(incurredClaims, layerParameter, layerPremium);
        }
        return additionalPremium;
    }

    public double cededIncurredRespectTerm(List<IClaimRoot> incurredClaims, PeriodLayerParameters layerParameters, PeriodScope periodScope, double termExcess, double termLimit, IPeriodCounter counter, ContractCoverBase coverageBase) {
        return cededIncurredToPeriod(incurredClaims, layerParameters, periodScope, termExcess, termLimit, coverageBase, periodScope.getCurrentPeriod());
    }

    public double cededIncurredToPeriod(List<IClaimRoot> incurredClaims, PeriodLayerParameters layerParameters, PeriodScope periodScope, double termExcess, double termLimit, ContractCoverBase coverageBase, int periodTo) {

        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc();

        double termIncurredInPriorPeriods = 0;
        for (int period = 0; period < periodTo; period++) {
            termIncurredInPriorPeriods += incurredAmountInPeriod(incurredClaims, layerParameters, period, annualIncurredCalc, coverageBase, periodScope);
        }

        double annualIncurredThisPeriod = incurredAmountInPeriod(incurredClaims, layerParameters, periodTo, annualIncurredCalc, coverageBase, periodScope);

        double lossAfterTermStructure = Math.min(Math.max(termIncurredInPriorPeriods + annualIncurredThisPeriod - termExcess, 0), termLimit);
        double lossAfterTermStructurePriorPeriods = Math.min(Math.max(termIncurredInPriorPeriods - termExcess, 0), termLimit);
        return lossAfterTermStructure - lossAfterTermStructurePriorPeriods;
    }

    public double incurredAmountInPeriod(List<IClaimRoot> incurredClaims, PeriodLayerParameters layerParameters, int period, AnnualIncurredCalc annualIncurredCalc, ContractCoverBase base, PeriodScope periodScope) {
        double termIncurredInPeriod = 0;
        ArrayList<IClaimRoot> claimsInPeriod = GRIUtilities.claimsCoveredInPeriod(incurredClaims, periodScope, base, period);
        List<LayerParameters> layers = layerParameters.getLayers(period + 1);
        for (LayerParameters layerParameter : layers) {
            termIncurredInPeriod += annualIncurredCalc.layerCededIncurred(claimsInPeriod, layerParameter);
        }
        return termIncurredInPeriod;
    }

    public Map<Integer, Double> cededIncurredsByPeriods(List<IClaimRoot> allCededClaims, PeriodScope periodScope, double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase) {
        TermIncurredCalculation iRiIncurredCalculation = new TermIncurredCalculation();
        Map<Integer, Double> incurredCededAmountByPeriod = new TreeMap<Integer, Double>();
        for (int contractPeriod = 0; contractPeriod <= periodScope.getCurrentPeriod(); contractPeriod++) {
            double incurredInPeriod = iRiIncurredCalculation.cededIncurredToPeriod(allCededClaims, layerParameters, periodScope, termExcess, termLimit, coverageBase, contractPeriod);
            incurredCededAmountByPeriod.put(contractPeriod, incurredInPeriod);
        }
        return incurredCededAmountByPeriod;
    }

}
