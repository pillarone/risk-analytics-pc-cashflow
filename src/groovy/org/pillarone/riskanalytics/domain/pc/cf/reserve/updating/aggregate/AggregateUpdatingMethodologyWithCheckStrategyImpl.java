package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
abstract class AggregateUpdatingMethodologyWithCheckStrategyImpl extends AbstractParameterObject
        implements IAggregateUpdatingMethodologyStrategy {


    public List<ClaimRoot> updatingUltimate(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims, IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns, int contractPeriod) {
        doSomeChecks(baseClaims, actualClaims, periodCounter, updateDate, patterns, contractPeriod);
        return updateUltimatePostChecks(baseClaims, actualClaims, periodCounter, updateDate, patterns);
    }

    /**
     * This method should be used to do any sanity checks on the updating strategy.
     * Override it and call super for strategy specific checks.
     * @param baseClaims
     * @param actualClaims
     * @param periodCounter
     * @param updateDate
     * @param patterns
     * @param contractPeriod
     */
    protected void doSomeChecks(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims, IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns, int contractPeriod) {
        if (baseClaims.size() != 1) {
            throw new IllegalArgumentException("Aggregate updating strategy recieved different to one claim. Claims recieved: " + baseClaims.size());
        }
        actualClaims.checkClaimRootOccurenceAgainstFirstActualPaid(baseClaims, contractPeriod, periodCounter, updateDate);
    }

    protected abstract List<ClaimRoot> updateUltimatePostChecks(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims, IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns);

}
