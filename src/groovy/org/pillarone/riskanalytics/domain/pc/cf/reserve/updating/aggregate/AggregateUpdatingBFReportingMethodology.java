package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
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
public class AggregateUpdatingBFReportingMethodology extends AbstractParameterObject implements IAggregateUpdatingMethodologyStrategy {

    private ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker.class, "");

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("updatingPattern", updatingPattern);
        return parameters;
    }

    public IParameterObjectClassifier getType() {
        return AggregateUpdatingMethodologyStrategyType.BFREPORTING;
    }

    public List<ClaimRoot> updatingUltimate(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                            IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns) {
        PatternPacket pattern = PatternUtils.filterPattern(patterns, updatingPattern, IUpdatingPatternMarker.class);
        List<ClaimRoot> baseClaimsWithAdjustedUltimate = new ArrayList<ClaimRoot>();
        for (ClaimRoot baseClaim : baseClaims) {
            // todo(sku): think about a switch to use either the occurrence or inception period
            int occurrencePeriod = baseClaim.getOccurrencePeriod(periodCounter);
            DateTime periodStartDate = periodCounter.startOfPeriod(baseClaim.getOccurrenceDate());

            AggregateHistoricClaim historicClaim = actualClaims.historicClaims(occurrencePeriod, periodCounter, updateDate);
            if (historicClaim.noUpdates()) {
                baseClaimsWithAdjustedUltimate.add(baseClaim);
            }
            else {
                double reportedToDate = historicClaim.reportedToDate(updateDate);
                DateTime lastReportedDate = historicClaim.lastReportedDate(updateDate);
                double elapsedMonths = DateTimeUtilities.days360(periodStartDate, lastReportedDate) / 30d;
                double outstandingShare = pattern.outstandingShare(elapsedMonths);
                double originalUltimate = baseClaim.getUltimate();
                double ultimate = reportedToDate + outstandingShare * originalUltimate;
                ClaimRoot adjustedBaseClaim = new ClaimRoot(ultimate, baseClaim);
                baseClaimsWithAdjustedUltimate.add(adjustedBaseClaim);
            }
        }
        return baseClaimsWithAdjustedUltimate;
    }
}