package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.CalcAPBasis;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AdditionalPremiumPerLayer {

    final double periodLimit;
    final double periodExcess;
    final double additionalPremium;
    final CalcAPBasis basis;

    public AdditionalPremiumPerLayer(double periodExcess, double periodLimit, double additionalPremium, APBasis basis) {
        if(periodLimit == 0) {
            this.periodLimit = Double.MAX_VALUE;
        } else {
            this.periodLimit = periodLimit;
        }
        this.periodExcess = periodExcess;
        this.additionalPremium = additionalPremium;
        this.basis = basis.calcAPBasis();
    }

    public double getPeriodLimit() {
        return periodLimit;
    }

    public double getPeriodExcess() {
        return periodExcess;
    }

    public double getAdditionalPremium() {
        return additionalPremium;
    }

    public CalcAPBasis getBasis() {
        return basis;
    }
}

