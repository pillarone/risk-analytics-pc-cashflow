package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoUpdatingMethodology extends AggregateUpdatingMethodologyWithCheckStrategyImpl implements IAggregateUpdatingMethodologyStrategy {

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public IParameterObjectClassifier getType() {
        return AggregateUpdatingMethodologyStrategyType.PLEASESELECT;
    }


    /**
     * @param baseClaims  ignored in this strategy
     * @param actualClaims ignored in this strategy
     * @param periodCounter ignored in this strategy
     * @param updateDate ignored in this strategy
     * @param patterns  ignored in this strategy
     * @return provided baseClaims
     */
    public List<ClaimRoot> updateUltimatePostChecks(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                                    IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns) {
        return baseClaims;
    }

    @Override
    protected void doSomeChecks(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims, IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns) {
        super.doSomeChecks(baseClaims, actualClaims, periodCounter, updateDate, patterns);
        if(updateDate.isAfter(periodCounter.startOfFirstPeriod())) {
            throw new IllegalArgumentException("The update date is " + DateTimeUtilities.formatDate.print(updateDate) + " and the start of the simulation is " +
                    "" + DateTimeUtilities.formatDate.print(periodCounter.startOfFirstPeriod()) + ". Have you forgotten to select an updating strategy?"
            );
        }
    }
}