package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoSingleActualClaimsStrategy extends AbstractParameterObject implements ISingleActualClaimsStrategy {

    public IParameterObjectClassifier getType() {
        return SingleActualClaimsStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase) {
    }

    public List<GrossClaimRoot> claimWithAdjustedPattern(PatternPacket originalPayoutPattern, PayoutPatternBase base,
                                                         DateTime updateDate, DateTimeUtilities.Days360 days360, int currentPeriod, boolean sanityChecks) {
        return Collections.emptyList();
    }

//    public List<SingleHistoricClaim> historicClaims(int period, IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase) {
//        return Collections.emptyList();
//    }

//    public void checkClaimRootOccurrenceAgainstFirstActualPaid(List<ClaimRoot> baseClaims, int contractPeriod,
//                                                              IPeriodCounter periodCounter, DateTime updateDate,
//                                                              PayoutPatternBase payoutPatternBase) {
////        There are no actual claims by definition of this class so this method is not useful...
//    }

    public DateTime lastReportedDate(IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase) {
        return periodCounter.startOfFirstPeriod();
    }

}
