package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AgeToAgePatternStrategy extends AbstractPatternStrategy implements IPatternStrategy {

    public static final String AGE_TO_AGE_PATTERN = "ageToAgePattern";

    private ConstrainedMultiDimensionalParameter ageToAgePattern;
    private PatternPacket pattern;

    public IParameterObjectClassifier getType() {
        return PatternStrategyType.AGE_TO_AGE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(AGE_TO_AGE_PATTERN, ageToAgePattern);
        return params;
    }

    public PatternPacket getPattern(Class<? extends IPatternMarker> patternMarker) {
        if (pattern == null) {
            pattern = getAgeToAgePattern(ageToAgePattern, PatternStrategyType.LINK_RATIOS, patternMarker);
        }
        return pattern;
    }
}
