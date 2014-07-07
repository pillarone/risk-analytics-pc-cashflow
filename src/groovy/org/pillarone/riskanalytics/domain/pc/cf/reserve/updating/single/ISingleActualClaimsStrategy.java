package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.id.IIdGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ISingleActualClaimsStrategy extends IParameterObject {
    void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase);

    List<GrossClaimRoot> claimWithAdjustedPattern(PatternPacket originalPayoutPattern, PayoutPatternBase base, DateTime updateDate,
                                                  DateTimeUtilities.Days360 days360, int currentPeriod, boolean sanityChecks,
                                                  IIdGenerator idGenerator);

//    void checkClaimRootOccurrenceAgainstFirstActualPaid(List<ClaimRoot> baseClaims, int contractPeriod, IPeriodCounter periodCounter,
//                                                       DateTime updateDate, PayoutPatternBase base);

    DateTime lastReportedDate(IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase);
}
