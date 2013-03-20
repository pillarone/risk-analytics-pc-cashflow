package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IntegralStabilizationStrategy extends AbstractStabilizationStrategy {

    private double franchise;

    public IParameterObjectClassifier getType() {
        return StabilizationStrategyType.INTEGRAL;
    }

    public Map getParameters() {
        Map<String, Object> params = super.getParameters();
        params.put("franchise", franchise);
        return params;
    }

    public double indexFactor(ClaimCashflowPacket claim, IPeriodCounter periodCounter) {
        double index = super.indexFactor(claim, periodCounter);
        return index < 1 + franchise  ? 1 : index;
    }
}
