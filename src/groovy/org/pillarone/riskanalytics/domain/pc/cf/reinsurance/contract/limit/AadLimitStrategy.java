package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AadLimitStrategy extends AbstractParameterObject implements ILimitStrategy {

    private double aad = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.AAD;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("aad", aad);
        return parameters;
    }

    public double getAAD() { return aad; }

    public double getAAL() { return Double.MAX_VALUE; }

    public double appliedLimit(ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty) {
        return appliedLimit(claimProperty.cumulatedIndexed(claim));
    }

    public double appliedLimit(double value) {
        return Math.max(0, value - aad);
    }
}
