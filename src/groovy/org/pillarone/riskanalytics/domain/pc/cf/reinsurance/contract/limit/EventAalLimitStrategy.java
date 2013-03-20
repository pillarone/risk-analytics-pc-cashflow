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
public class EventAalLimitStrategy extends AbstractParameterObject implements ILimitStrategy {

    private double eventLimit = 0;
    private double aal = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.EVENTLIMITAAL;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(2);
        parameters.put("eventLimit", eventLimit);
        parameters.put("aal", aal);
        return parameters;
    }

    public double getEventLimit() { return eventLimit; }

    public double getAAD() {
        return 0;
    }

    public double getAAL() { return aal; }

    @Override
    public double appliedLimit(ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty) {
        double value = claimProperty.cumulatedIndexed(claim);
        if (claim.hasEvent()) {
            return appliedLimit(-Math.min(eventLimit, -value));
        }
        return appliedLimit(value);
    }

    @Override
    public double appliedLimit(double value) {
        // shortcut, as we don't know if the current claim is an event claim
        return -Math.min(aal, -value);
    }
}
