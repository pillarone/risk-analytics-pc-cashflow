package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PayoutReportingCombinedCumulativePatternStrategy
        extends AbstractPatternStrategy
        implements IPayoutReportingCombinedPatternStrategy {

    public static final String CUMULATIVE_PATTERN = "cumulativePattern";

    private ConstrainedMultiDimensionalParameter cumulativePattern;
    private PatternPacket reportingPattern;
    private PatternPacket payoutPattern;

    public IParameterObjectClassifier getType() {
        return PayoutReportingCombinedPatternStrategyType.CUMULATIVE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(CUMULATIVE_PATTERN, cumulativePattern);
        return params;
    }

    public PatternPacket getReportingPattern() {
        if (reportingPattern == null) {
            reportingPattern = getCumulativePattern(cumulativePattern, PayoutReportingCombinedPatternStrategyType.CUMULATIVE_REPORTED,
                IReportingPatternMarker.class);
        }
        return reportingPattern;
    }

    public PatternPacket getPayoutPattern() {
        if (payoutPattern == null) {
            payoutPattern = getCumulativePattern(cumulativePattern, PayoutReportingCombinedPatternStrategyType.CUMULATIVE_PAYOUT,
                IPayoutPatternMarker.class);
        }
        return payoutPattern;
    }
}
