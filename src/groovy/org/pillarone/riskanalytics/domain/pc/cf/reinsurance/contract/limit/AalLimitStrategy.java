package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AalLimitStrategy extends AbstractParameterObject implements ILimitStrategy {

    private double aal = 0;

    public IParameterObjectClassifier getType() {
        return LimitStrategyType.AAL;
    }

    public Map getParameters() {
        Map<String, Double> parameters = new HashMap<String, Double>(1);
        parameters.put("aal", aal);
        return parameters;
    }

    public double getAAD() {
        return 0;
    }

    public double getAAL() { return aal; }
}
