package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SingleUpdatingMethodology extends AbstractParameterObject implements ISingleUpdatingMethodologyStrategy {

    ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker.class, "");
    SingleUpdatingMethod methodology = SingleUpdatingMethod.ORIGINAL_ULTIMATE;

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("updatingPattern", updatingPattern);
        parameters.put("methodology", methodology);
        return parameters;
    }

    public IParameterObjectClassifier getType() {
        return SingleUpdatingMethodologyStrategyType.SINGLE;
    }

    /**
     * This methods combines actual and IBNR claims and adjusts their payout pattern.
     * @param baseClaims generated IBNR claims being filtered according to methodology
     * @param actualClaims
     * @param periodCounter
     * @param updateDate
     * @param patterns the selected updatingPattern will be filtered from this list
     * @param contractPeriod
     * @param days360
     * @param base
     * @param sanityChecks
     * @return
     */
    public GrossClaimAndRandomDraws updatingClaims(List<ClaimRoot> baseClaims, ISingleActualClaimsStrategy actualClaims,
                                                   IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns,
                                                   int contractPeriod, DateTimeUtilities.Days360 days360, PayoutPatternBase base,
                                                   PatternPacket payoutPattern, boolean sanityChecks) {
        List<GrossClaimRoot> modifiedGeneratedAndActualClaims = new ArrayList<GrossClaimRoot>();
        // todo(sku): where do we need the updating pattern, where the payout pattern
        PatternPacket updatePattern = PatternUtils.filterPattern(patterns, updatingPattern, IUpdatingPatternMarker.class);
        DateTime lastReportedDate = actualClaims.lastReportedDate(periodCounter, updateDate, base);
        SingleUpdatingMethod.ClaimAndRandomDraws updatingResult = methodology.filterIBNRClaims(baseClaims, updateDate, lastReportedDate, updatePattern, periodCounter);
        List<ClaimRoot> ibnrClaims = updatingResult.getUpdatedClaims();
        for (ClaimRoot ibnrClaim : ibnrClaims) {
            // last arg null as these are generated claims
            DateTime startDateForPatterns = base.startDateForPayouts(ibnrClaim, periodCounter.getCurrentPeriodStart(), null);
            PatternPacket adjustedPattern = PatternUtils.adjustForNoClaimUpdates(payoutPattern, startDateForPatterns, updateDate);
            modifiedGeneratedAndActualClaims.add(new GrossClaimRoot(ibnrClaim, adjustedPattern, startDateForPatterns));
        }
        int currentPeriod = periodCounter.belongsToPeriod(periodCounter.getCurrentPeriodStart());
        modifiedGeneratedAndActualClaims.addAll(actualClaims.claimWithAdjustedPattern(payoutPattern, base, updateDate, days360, currentPeriod));
        return new GrossClaimAndRandomDraws(modifiedGeneratedAndActualClaims, updatingResult);
    }

}
