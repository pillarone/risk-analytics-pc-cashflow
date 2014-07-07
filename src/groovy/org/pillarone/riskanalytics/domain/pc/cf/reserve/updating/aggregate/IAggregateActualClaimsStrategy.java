package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.id.IIdGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IAggregateActualClaimsStrategy extends IParameterObject {

    void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate,
                                                 PayoutPatternBase payoutPatternBase, boolean sanityChecks);

    GrossClaimRoot claimWithAdjustedPattern(ClaimRoot claimRoot, int contractPeriod, PatternPacket payoutPattern,
                                            PeriodScope periodScope, DateTime updateDate, DateTimeUtilities.Days360 days360,
                                            boolean sanityChecks, PayoutPatternBase base, IIdGenerator idGenerator);

    AggregateHistoricClaim historicClaims(int period, IPeriodCounter periodCounter, DateTime updateDate,
                                          PayoutPatternBase base, boolean sanityChecks);

    void checkClaimRootOccurrenceAgainstFirstActualPaid(List<ClaimRoot> baseClaims, int contractPeriod, IPeriodCounter periodCounter,
                                                        DateTime updateDate, PayoutPatternBase base, boolean sanityChecks);
}
