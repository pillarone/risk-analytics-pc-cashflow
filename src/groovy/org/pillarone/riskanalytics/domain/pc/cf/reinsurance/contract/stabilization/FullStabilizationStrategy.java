package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FullStabilizationStrategy extends AbstractStabilizationStrategy {

    public IParameterObjectClassifier getType() {
        return StabilizationStrategyType.FULL;
    }

    public Map getParameters() {
        return super.getParameters();
    }

}
