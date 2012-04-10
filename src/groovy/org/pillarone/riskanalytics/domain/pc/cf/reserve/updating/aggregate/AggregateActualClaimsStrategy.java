package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.ReserveVolatility;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateActualClaimsStrategy extends AbstractParameterObject implements IAggregateActualClaimsStrategy {

    private ConstrainedMultiDimensionalParameter history = getDefaultHistory();
    private ReserveVolatility reserveVolatility = ReserveVolatility.NONE;

    public IParameterObjectClassifier getType() {
        return AggregateActualClaimsStrategyType.AGGREGATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("history", history);
        parameters.put("reserveVolatility", reserveVolatility);
        return parameters;
    }

    public static ConstrainedMultiDimensionalParameter getDefaultHistory() {
        return AggregateHistoricClaimsConstraints.getDefault();
    }
}
