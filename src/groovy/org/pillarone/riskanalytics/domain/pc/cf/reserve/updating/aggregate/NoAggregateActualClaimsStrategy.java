package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.Collections;
import java.util.List;
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

    public void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate,
                                                        PayoutPatternBase payoutPatternBase, boolean sanityChecks) {
    }

    public GrossClaimRoot claimWithAdjustedPattern(ClaimRoot claimRoot, int contractPeriod, PatternPacket payoutPattern,
                                                   PeriodScope periodScope, DateTime updateDate, DateTimeUtilities.Days360 days360,
                                                   boolean sanityChecks, PayoutPatternBase payoutPatternBase) {
        if(!periodScope.getPeriodCounter().startOfFirstPeriod().equals(updateDate)) {
            throw new SimulationException("Non-inception model must have an actual claims strategy");
        }
        DateTime startDateForPatterns = payoutPatternBase.startDateForPayouts(claimRoot, periodScope.getCurrentPeriodStartDate(), null);
        return new GrossClaimRoot(claimRoot, payoutPattern, startDateForPatterns);
    }

    public AggregateHistoricClaim historicClaims(int period, IPeriodCounter periodCounter, DateTime updateDate,
                                                 PayoutPatternBase payoutPatternBase, boolean sanityChecks) {
        return new AggregateHistoricClaim(period, periodCounter, PayoutPatternBase.PERIOD_START_DATE);
    }

    public void checkClaimRootOccurrenceAgainstFirstActualPaid(List<ClaimRoot> baseClaims, int contractPeriod,
                                                               IPeriodCounter periodCounter, DateTime updateDate,
                                                               PayoutPatternBase payoutPatternBase, boolean sanityChecks) {
//        There are no actual claims by definition of this class so this method is not useful...
    }
}
