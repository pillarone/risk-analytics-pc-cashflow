package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneLimitStrategy extends AbstractParameterObject implements ILimitStrategy {

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public double getAAD() {
        return 0;
    }

    public double getAAL() {
        return Double.MAX_VALUE;
    }

    @Override
    public double appliedLimit(ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty) {
        return claimProperty.cumulatedIndexed(claim);
    }

    @Override
    public double appliedLimit(double value) {
        return value;
    }
}
