package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PayoutReportingCombinedAgeToAgePatternStrategy
        extends AbstractPatternStrategy
        implements IPayoutReportingCombinedPatternStrategy {

    public static final String AGE_TO_AGE_PATTERN = "ageToAgePattern";

    private ConstrainedMultiDimensionalParameter ageToAgePattern;
    private PatternPacket reportingPattern;
    private PatternPacket payoutPattern;

    public IParameterObjectClassifier getType() {
        return PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(AGE_TO_AGE_PATTERN, ageToAgePattern);
        return params;
    }

    public PatternPacket getReportingPattern() {
        if (reportingPattern == null) {
            reportingPattern = getAgeToAgePattern(ageToAgePattern,
                    PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_REPORTED, IReportingPatternMarker.class);
        }
        return reportingPattern;
    }

    public PatternPacket getPayoutPattern() {
        if (payoutPattern == null) {
            payoutPattern = getAgeToAgePattern(ageToAgePattern,
                    PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_PAYOUT, IPayoutPatternMarker.class);
        }
        return payoutPattern;
    }


}
