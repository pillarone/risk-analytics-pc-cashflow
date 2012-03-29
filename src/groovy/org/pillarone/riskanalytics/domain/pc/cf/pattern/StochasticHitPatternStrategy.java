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
public class StochasticHitPatternStrategy extends AbstractPatternStrategy implements IPatternStrategy {

    public static final String STOCHASTIC_HIT_PATTERN = "stochasticHitPattern";
    ConstrainedMultiDimensionalParameter stochasticHitPattern;

    private PatternPacket pattern;

    public IParameterObjectClassifier getType() {
        return PatternStrategyType.HIT_PROBABILITY;
    }

    public Map getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put(STOCHASTIC_HIT_PATTERN, stochasticHitPattern);
        return params;
    }

    public PatternPacket getPattern(Class<? extends IPatternMarker> patternMarker) {
        if (pattern == null) {
            int columnMonthIndex = stochasticHitPattern.getColumnIndex(PatternTableConstraints.MONTHS);
            List<Double> incrementalValues = getPatternValues(stochasticHitPattern, columnMonthIndex,
                    stochasticHitPattern.getColumnIndex(PatternStrategyType.INCREMENTS));
            List<Double> cumulativeValues = getCumulativePatternValues(incrementalValues);
            List<Period> cumulativePeriods = getCumulativePeriods(stochasticHitPattern, columnMonthIndex);
            pattern = new  PatternPacket(patternMarker, cumulativeValues, cumulativePeriods, true);
        }
        return pattern;
    }
}
