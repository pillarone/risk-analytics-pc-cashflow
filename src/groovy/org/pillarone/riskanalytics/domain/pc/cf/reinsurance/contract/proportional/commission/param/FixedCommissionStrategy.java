package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.FixedCommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FixedCommissionStrategy extends AbstractCommissionStrategy {

    private double commission = 0;

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.FIXEDCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Double> map = new HashMap<String, Double>(1);
        map.put("commission", commission);
        return map;
    }

    @Override
    public ICommission getCalculator(DoubleValuePerPeriod lossCarriedForward) {
        return new FixedCommission(commission);
    }
}
