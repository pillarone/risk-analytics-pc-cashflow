package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPatternStrategy extends IParameterObject {

    PatternPacket getPattern(Class<? extends IPatternMarker> patternMarker);
}
