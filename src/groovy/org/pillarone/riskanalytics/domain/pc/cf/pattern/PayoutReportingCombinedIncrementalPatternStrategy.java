package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PayoutReportingCombinedIncrementalPatternStrategy
        extends AbstractPatternStrategy
        implements IPayoutReportingCombinedPatternStrategy {

    public static final String INCREMENTAL_PATTERN = "incrementalPattern";

    private ConstrainedMultiDimensionalParameter incrementalPattern;
    private PatternPacket reportingPattern;
    private PatternPacket payoutPattern;

    public IParameterObjectClassifier getType() {
        return PatternStrategyType.INCREMENTAL;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(INCREMENTAL_PATTERN, incrementalPattern);
        return params;
    }

    public PatternPacket getReportingPattern() {
        if (reportingPattern == null) {
            reportingPattern = getIncrementalPattern(incrementalPattern,
                    PayoutReportingCombinedPatternStrategyType.INCREMENTS_REPORTED, IReportingPatternMarker.class);
        }
        return reportingPattern;
    }

    public PatternPacket getPayoutPattern() {
        if (payoutPattern == null) {
            payoutPattern = getIncrementalPattern(incrementalPattern,
                    PayoutReportingCombinedPatternStrategyType.INCREMENTS_PAYOUT, IPayoutPatternMarker.class);
        }
        return payoutPattern;
    }


}
