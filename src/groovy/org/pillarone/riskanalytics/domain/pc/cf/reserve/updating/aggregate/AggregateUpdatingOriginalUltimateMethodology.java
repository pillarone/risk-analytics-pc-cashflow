package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateUpdatingOriginalUltimateMethodology extends AggregateUpdatingMethodologyWithCheckStrategyImpl {

    public Map getParameters() {
        return Collections.EMPTY_MAP;
    }

    public IParameterObjectClassifier getType() {
        return AggregateUpdatingMethodologyStrategyType.ORIGINALULTIMATE;
    }

    public List<ClaimRoot> updateUltimatePostChecks(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                                    IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns, DateTimeUtilities.Days360 days360, PayoutPatternBase base, boolean sanityChecks) {
        List<ClaimRoot> baseClaimsWithAdjustedUltimate = new ArrayList<ClaimRoot>();
        IClaimRoot baseClaim = baseClaims.get(0);
        int occurrencePeriod = baseClaim.getInceptionPeriod(periodCounter);
        AggregateHistoricClaim historicClaim = actualClaims.historicClaims(occurrencePeriod, periodCounter, updateDate, base, sanityChecks);
        if(historicClaim.noUpdates()) {
            baseClaimsWithAdjustedUltimate.add((ClaimRoot) baseClaim);
            return baseClaimsWithAdjustedUltimate;
        } else {
            double reportedToDate = historicClaim.reportedToDate(updateDate);
            double originalUltimate = baseClaim.getUltimate();
            double ultimate = Math.max(reportedToDate, originalUltimate);
            ClaimRoot adjustedBaseClaim = new ClaimRoot(ultimate, baseClaim);
            baseClaimsWithAdjustedUltimate.add(adjustedBaseClaim);
            return baseClaimsWithAdjustedUltimate;
        }
    }
}