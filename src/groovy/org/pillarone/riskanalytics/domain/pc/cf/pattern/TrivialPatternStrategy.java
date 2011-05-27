package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialPatternStrategy extends AbstractPatternStrategy implements IPatternStrategy {

    public IParameterObjectClassifier getType() {
        return PatternStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public PatternPacket getPattern(Class<? extends IPatternMarker> patternMarker) {
        return new PatternPacket.TrivialPattern(patternMarker);
    }
}
