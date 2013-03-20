package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.*;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This strategy is only allowed if the updating date and projection start date are the same. In this case the generated
 * claims are returned without any modification. If the condition is not fulfilled an exception is thrown and the simulation
 * ist stopped.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoUpdatingMethodology extends AbstractParameterObject implements ISingleUpdatingMethodologyStrategy {

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public IParameterObjectClassifier getType() {
        return SingleUpdatingMethodologyStrategyType.NONE;
    }


    public GrossClaimAndRandomDraws updatingClaims(List<ClaimRoot> baseClaims, ISingleActualClaimsStrategy actualClaims,
                                                   IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns,
                                                   int contractPeriod, DateTimeUtilities.Days360 days360, PayoutPatternBase base,
                                                   PatternPacket payoutPattern, boolean sanityChecks) {
        if(updateDate.isAfter(periodCounter.startOfFirstPeriod())) {
            throw new IllegalArgumentException("The update date is " + DateTimeUtilities.formatDate.print(updateDate)
                    + " and the start of the simulation is "
                    + DateTimeUtilities.formatDate.print(periodCounter.startOfFirstPeriod())
                    + ". Have you forgotten to select an updating strategy?"
            );
        }
        List<GrossClaimRoot> claims = new ArrayList<GrossClaimRoot>();
        for (ClaimRoot baseClaim : baseClaims) {
//            This pushes the pattern calculation through the updating code. It should result in a single source of pattern problems, maybe a small performance impact.
            DateTime startDateforPatterns = base.startDateForPayouts(baseClaim, periodCounter.getCurrentPeriodStart(), null);
            PatternPacket packet = base.patternAccordingToPayoutBaseNoUpdates(payoutPattern, startDateforPatterns, updateDate);
            claims.add(new GrossClaimRoot(baseClaim, packet,
                    new PatternPacket.TrivialPattern(IReportingPatternMarker.class),
                    startDateforPatterns));
        }
        return new GrossClaimAndRandomDraws(claims, new SingleUpdatingMethod.ClaimAndRandomDraws(new ArrayList<ClaimRoot>()));
    }
}
