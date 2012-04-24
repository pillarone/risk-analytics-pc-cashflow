package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateUpdatingOriginalUltimateMethodology extends AbstractParameterObject implements IAggregateUpdatingMethodologyStrategy {

    public Map getParameters() {
        return Collections.EMPTY_MAP;
    }

    public IParameterObjectClassifier getType() {
        return AggregateUpdatingMethodologyStrategyType.ORIGINALULTIMATE;
    }

    public List<ClaimRoot> updatingUltimate(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                            IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns) {
        List<ClaimRoot> baseClaimsWithAdjustedUltimate = new ArrayList<ClaimRoot>();
        for (IClaimRoot baseClaim : baseClaims) {
            // todo(sku): think about a switch to use either the occurrence or inception period
            int occurrencePeriod = baseClaim.getOccurrencePeriod(periodCounter);
            AggregateHistoricClaim historicClaim = actualClaims.historicClaims(occurrencePeriod, periodCounter, updateDate);
            double reportedToDate = historicClaim.reportedToDate(updateDate);
            double originalUltimate = baseClaim.getUltimate();
            double ultimate = Math.max(reportedToDate, originalUltimate);
            ClaimRoot adjustedBaseClaim = new ClaimRoot(ultimate, baseClaim);
            baseClaimsWithAdjustedUltimate.add(adjustedBaseClaim);
        }
        return baseClaimsWithAdjustedUltimate;
    }
}