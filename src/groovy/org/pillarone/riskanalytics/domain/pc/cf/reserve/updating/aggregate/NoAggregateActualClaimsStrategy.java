package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoAggregateActualClaimsStrategy extends AbstractParameterObject implements IAggregateActualClaimsStrategy {

    public IParameterObjectClassifier getType() {
        return AggregateActualClaimsStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate) {
    }

    public GrossClaimRoot claimWithAdjustedPattern(ClaimRoot claimRoot, int contractPeriod, PatternPacket payoutPattern,
                                                   IPeriodCounter periodCounter, DateTime updateDate) {
        return new GrossClaimRoot(claimRoot, payoutPattern);
    }

    public AggregateHistoricClaim historicClaims(int period, IPeriodCounter periodCounter, DateTime updateDate) {
        return null;
    }

}
