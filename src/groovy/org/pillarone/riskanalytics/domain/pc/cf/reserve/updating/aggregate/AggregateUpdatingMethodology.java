package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateUpdatingMethodology extends AbstractParameterObject implements IAggregateUpdatingMethodologyStrategy {

    ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker.class, "");
    AggregateUpdatingMethod methodology = AggregateUpdatingMethod.ORIGINAL_ULTIMATE;

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("updatingPattern", updatingPattern);
        parameters.put("methodology", methodology);
        return parameters;
    }

    public IParameterObjectClassifier getType() {
        return AggregateUpdatingMethodologyStrategyType.AGGREGATE;
    }

    public List<ClaimRoot> updatingUltimate(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                            IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns) {
        return methodology.update(baseClaims, actualClaims, periodCounter, updateDate, patterns, updatingPattern);
    }
}