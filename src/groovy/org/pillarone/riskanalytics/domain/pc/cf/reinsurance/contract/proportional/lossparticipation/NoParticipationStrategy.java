package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoParticipationStrategy extends AbstractParameterObject implements ILossParticipationStrategy {
    public IParameterObjectClassifier getType() {
        return LossParticipationStrategyType.NOPARTICIPATION;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public ILossParticipation getLossParticpation() {
        return new NoLossParticipation();
    }
}
