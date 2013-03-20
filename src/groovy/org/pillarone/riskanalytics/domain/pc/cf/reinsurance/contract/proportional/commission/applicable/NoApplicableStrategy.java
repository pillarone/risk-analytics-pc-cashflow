package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.applicable;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class NoApplicableStrategy extends AbstractParameterObject implements IApplicableStrategy {

    public IParameterObjectClassifier getType() {
        return ApplicableStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}