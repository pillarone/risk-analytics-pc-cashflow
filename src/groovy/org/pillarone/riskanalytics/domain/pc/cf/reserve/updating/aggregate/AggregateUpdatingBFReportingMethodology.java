package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateUpdatingBFReportingMethodology extends AggregateUpdatingMethodologyWithCheckStrategyImpl
        implements IAggregateUpdatingMethodologyStrategy {

    private ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker.class, "");

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("updatingPattern", updatingPattern);
        return parameters;
    }

    public IParameterObjectClassifier getType() {
        return AggregateUpdatingMethodologyStrategyType.BFREPORTING;
    }



    public List<ClaimRoot> updateUltimatePostChecks(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                                    IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns,
                                                    DateTimeUtilities.Days360 days360, PayoutPatternBase base) {
        PatternPacket pattern = PatternUtils.filterPattern(patterns, updatingPattern, IUpdatingPatternMarker.class);
        List<ClaimRoot> baseClaimsWithAdjustedUltimate = new ArrayList<ClaimRoot>();
        ClaimRoot baseClaim = baseClaims.get(0);
        // todo(sku): think about a switch to use either the occurrence or inception period
        int occurrencePeriod = baseClaim.getOccurrencePeriod(periodCounter);
        DateTime periodStartDate = periodCounter.startOfPeriod(baseClaim.getOccurrenceDate());

        AggregateHistoricClaim historicClaim = actualClaims.historicClaims(occurrencePeriod, periodCounter, updateDate, base);
        if (historicClaim.noUpdates()) {
            baseClaimsWithAdjustedUltimate.add(baseClaim);
        } else {
            double reportedToDate = historicClaim.reportedToDate(updateDate);
            DateTime lastReportedDate = historicClaim.lastReportedDate(updateDate);
            double elapsedMonths = days360.days360 (periodStartDate, lastReportedDate) / 30d;
            double outstandingShare = pattern.outstandingShare(elapsedMonths);
            double originalUltimate = baseClaim.getUltimate();
            double ultimate = reportedToDate + outstandingShare * originalUltimate;
            ClaimRoot adjustedBaseClaim = new ClaimRoot(ultimate, baseClaim);
            baseClaimsWithAdjustedUltimate.add(adjustedBaseClaim);
        }
        return baseClaimsWithAdjustedUltimate;
    }
}