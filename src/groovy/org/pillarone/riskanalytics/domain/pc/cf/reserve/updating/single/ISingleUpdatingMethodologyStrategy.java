package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ISingleUpdatingMethodologyStrategy extends IParameterObject {

    /**
     * Keeps as many claims of baseClaims as necessary and adds them together with actual claims to the returned list
     * @param baseClaims
     * @param actualClaims
     * @param periodCounter
     * @param updateDate
     * @param patterns
     * @param contractPeriod
     * @param days360
     * @param base
     * @param sanityChecks
     * @return
     */
    List<GrossClaimRoot> updatingClaims(List<ClaimRoot> baseClaims, ISingleActualClaimsStrategy actualClaims,
                                     IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns,
                                     int contractPeriod, DateTimeUtilities.Days360 days360, PayoutPatternBase base,
                                     PatternPacket payoutPattern, boolean sanityChecks);

}
