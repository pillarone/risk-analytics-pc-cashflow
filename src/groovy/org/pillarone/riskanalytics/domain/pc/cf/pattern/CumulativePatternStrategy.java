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
public class CumulativePatternStrategy extends AbstractPatternStrategy {

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

    public PatternPacket getPattern() {
        if (pattern == null) {
            int columnMonthIndex = cumulativePattern.getColumnIndex(PatternTableConstraints.MONTHS);
            List<Double> cumulativeValues = getPatternValues(cumulativePattern, columnMonthIndex,
                    cumulativePattern.getColumnIndex(PatternStrategyType.CUMULATED));
            List<Period> cumulativePeriods = getCumulativePeriods(cumulativePattern, columnMonthIndex);
            pattern = new  PatternPacket(cumulativeValues, cumulativePeriods);
        }
        return pattern;
    }
}
