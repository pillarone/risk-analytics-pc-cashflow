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
public class EventLimitStrategy extends AbstractParameterObject implements ILimitStrategy {

    private double eventLimit = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.EVENTLIMIT;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("eventLimit", eventLimit);
        return parameters;
    }

    public double getEventLimit() { return eventLimit; }

    public double getAAD() {
        return 0;
    }

    public double getAAL() {
        return Double.MAX_VALUE;
    }

    @Override
    public double appliedLimit(ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty) {
        double value = claimProperty.cumulatedIndexed(claim);
        if (claim.hasEvent()) {
            return -Math.min(eventLimit, -value);
        }
        return appliedLimit(value);
    }

    @Override
    public double appliedLimit(double value) {
        // shortcut, as we don't know if the current claim is an event claim
        return -Math.min(eventLimit, -value);
    }
}
