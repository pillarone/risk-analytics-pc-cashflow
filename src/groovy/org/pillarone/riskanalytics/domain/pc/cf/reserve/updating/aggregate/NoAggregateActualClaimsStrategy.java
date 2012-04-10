package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoAggregateActualClaimsStrategy extends AbstractParameterObject implements IAggregateActualClaimsStrategy {

    public IParameterObjectClassifier getType() {
        return AggregateActualClaimsStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
