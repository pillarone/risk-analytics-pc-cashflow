package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PayoutReportingCombinedTrivialPatternStrategy
        extends AbstractPatternStrategy
        implements IPayoutReportingCombinedPatternStrategy {

    public IParameterObjectClassifier getType() {
        return PayoutReportingCombinedPatternStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public PatternPacket getReportingPattern() {
        return new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    }

    public PatternPacket getPayoutPattern() {
        return new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);
    }
}
