package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinstatementsAndLimitStore {

    private ThresholdStore limitStore;
    private double maxReinstatements;
    private double reinstatementPremiumFactorPreviousPeriods;
    private List<Double> reinstatementPremiumFactors;
    private double limit;

    public ReinstatementsAndLimitStore(ThresholdStore limitStore, double limit, List<Double> reinstatementPremiumFactors) {
        this.limitStore = limitStore;
        this.limit = limit;
        this.reinstatementPremiumFactors = reinstatementPremiumFactors;
        double aggregateLimit = limitStore.get(BasedOnClaimProperty.PAID);
        maxReinstatements =  limit == 0d ? 0d : aggregateLimit / limit - 1;
    }

    public double calculateReinstatementPremiumFactor() {
        double usedReinstatements = usedReinstatmentsCumulated();
        double totalReinstatementPremiumFactor = 0d;
        for (int i = 0; i < Math.floor(usedReinstatements); i++) {
            totalReinstatementPremiumFactor += getReinstatementPremiumFactor(i);
        }
        double partialReinstatement = usedReinstatements - Math.floor(usedReinstatements);
        totalReinstatementPremiumFactor += partialReinstatement * getReinstatementPremiumFactor(
                new Double(usedReinstatements).intValue());
        double incrementalReinstatementPremiumFactor = totalReinstatementPremiumFactor - reinstatementPremiumFactorPreviousPeriods;
        reinstatementPremiumFactorPreviousPeriods = totalReinstatementPremiumFactor;
        return incrementalReinstatementPremiumFactor;
    }

    private double usedReinstatmentsCumulated() {
        double usedReinstatements = limit == 0d ? 0d : usedLimit() / limit;
        return Math.min(usedReinstatements, maxReinstatements);
    }

    private double usedLimit() {
        return limitStore.thresholdStabilized() - limitStore.get(BasedOnClaimProperty.PAID);
    }

    private double getReinstatementPremiumFactor(int reinstatementLevel) {
        if (reinstatementPremiumFactors.size() == 0) return 0;
        reinstatementLevel = Math.min(reinstatementLevel, reinstatementPremiumFactors.size() - 1);
        return reinstatementPremiumFactors.get(reinstatementLevel);
    }
}
