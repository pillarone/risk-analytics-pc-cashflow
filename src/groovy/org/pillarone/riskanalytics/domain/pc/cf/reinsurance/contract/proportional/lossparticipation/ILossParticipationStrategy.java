package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ILossParticipationStrategy extends IParameterObject {

    ILossParticipation getLossParticpation();
}
