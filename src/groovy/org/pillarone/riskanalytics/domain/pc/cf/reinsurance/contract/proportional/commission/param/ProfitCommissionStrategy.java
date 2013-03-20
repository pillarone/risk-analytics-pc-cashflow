package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ProfitCommission;

import java.util.Map;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ProfitCommissionStrategy extends AbstractCommissionStrategy {

    private double profitCommissionRatio = 0d;
    private double commissionRatio = 0d; // for "prior" fixed commission
    private double costRatio = 0d;
    private boolean lossCarriedForwardEnabled = true;
    private double initialLossCarriedForward = 0d;

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.PROFITCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Object> map = super.getParameters();
        map.put("profitCommissionRatio", profitCommissionRatio);
        map.put("commissionRatio", commissionRatio);
        map.put("costRatio", costRatio);
        map.put("lossCarriedForwardEnabled", lossCarriedForwardEnabled);
        map.put("initialLossCarriedForward", initialLossCarriedForward);
        return map;
    }

    public ICommission getCalculator(DoubleValuePerPeriod lossCarriedForward) {
        return new ProfitCommission(profitCommissionRatio, commissionRatio, costRatio, lossCarriedForwardEnabled,
                                    lossCarriedForward, useClaims.convert());
    }

    public DoubleValuePerPeriod getInitialLossCarriedForward() {
        return new DoubleValuePerPeriod(initialLossCarriedForward);
    }
}
