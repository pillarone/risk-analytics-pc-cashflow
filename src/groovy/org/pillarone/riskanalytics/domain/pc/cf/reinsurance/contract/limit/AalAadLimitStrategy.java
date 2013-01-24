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
public class AalAadLimitStrategy extends AbstractParameterObject implements ILimitStrategy {

    private double aad = 0;
    private double aal = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.AALAAD;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(2);
        parameters.put("aad", aad);
        parameters.put("aal", aal);
        return parameters;
    }

    public double getAAD() { return aad; }
    public double getAAL() { return aal; }

    @Override
    public double appliedLimit(ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty) {
        return appliedLimit(claimProperty.cumulatedIndexed(claim));
    }

    @Override
    public double appliedLimit(double value) {
        return -Math.min(aal, Math.max(0, -value - aad));
    }
}
