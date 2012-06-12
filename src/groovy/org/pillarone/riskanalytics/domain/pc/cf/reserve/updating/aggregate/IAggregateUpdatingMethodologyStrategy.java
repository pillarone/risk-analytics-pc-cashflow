package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IAggregateUpdatingMethodologyStrategy extends IParameterObject {

    List<ClaimRoot> updatingUltimate(List<ClaimRoot> baseClaims, IAggregateActualClaimsStrategy actualClaims,
                                     IPeriodCounter periodCounter, DateTime updateDate, List<PatternPacket> patterns, int contractPeriod, DateTimeUtilities.Days360 days360);

}
