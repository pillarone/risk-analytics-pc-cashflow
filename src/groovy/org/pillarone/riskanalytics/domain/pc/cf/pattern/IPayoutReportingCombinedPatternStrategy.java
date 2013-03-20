package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPayoutReportingCombinedPatternStrategy extends IParameterObject {

    PatternPacket getReportingPattern();
    PatternPacket getPayoutPattern();
}
