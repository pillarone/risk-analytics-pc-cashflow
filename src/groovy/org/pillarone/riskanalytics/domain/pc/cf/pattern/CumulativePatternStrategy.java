package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CumulativePatternStrategy extends AbstractPatternStrategy implements IPatternStrategy {

    public static final String CUMULATIVE_PATTERN = "cumulativePattern";

    private ConstrainedMultiDimensionalParameter cumulativePattern;
    private PatternPacket pattern;

    public IParameterObjectClassifier getType() {
        return PatternStrategyType.CUMULATIVE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(CUMULATIVE_PATTERN, cumulativePattern);
        return params;
    }

    public PatternPacket getPattern(Class<? extends IPatternMarker> patternMarker) {
        if (pattern == null) {
            pattern = getCumulativePattern(cumulativePattern, PatternStrategyType.CUMULATIVE2, patternMarker);
        }
        return pattern;
    }
}
