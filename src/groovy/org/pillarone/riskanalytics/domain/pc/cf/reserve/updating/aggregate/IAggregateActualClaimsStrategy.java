package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IAggregateActualClaimsStrategy extends IParameterObject {

    void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate);

    GrossClaimRoot claimWithAdjustedPattern(ClaimRoot claimRoot, int contractPeriod, PatternPacket payoutPattern,
                                            IPeriodCounter periodCounter, DateTime updateDate);

    AggregateHistoricClaim historicClaims(int period, IPeriodCounter periodCounter, DateTime updateDate);

    void checkClaimRootOccurenceAgainstFirstActualPaid(List<ClaimRoot> baseClaims, int contractPeriod, IPeriodCounter periodCounter, DateTime updateDate);
}
