package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.ReserveVolatility;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SingleActualClaimsStrategy extends AbstractParameterObject implements ISingleActualClaimsStrategy {

    private ConstrainedMultiDimensionalParameter history = getDefaultHistory();
    private ReserveVolatility reserveVolatility = ReserveVolatility.NONE;

    public IParameterObjectClassifier getType() {
        return SingleActualClaimsStrategyType.SINGLE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("history", history);
        parameters.put("reserveVolatility", reserveVolatility);
        return parameters;
    }

    public static ConstrainedMultiDimensionalParameter getDefaultHistory() {
        return SingleHistoricClaimsConstraints.getDefault();
    }

    /**
     * key: contractPeriod, all updates are kept in the contract period of the first occurrence
     */
    private Map<Integer, List<SingleHistoricClaim>> historicClaimsPerContractPeriod;
    private DateTime lastReportedDate;

    /**
     * The content of the history table is read and the historicClaimsPerContractPeriod and lastReportedDate variable
     * filled. The content of the reported amount column is ignored.
     *
     * @param periodCounter required for date calculations
     * @param updateDate    all reported claims after the updateDate are ignored
     */
    public void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase) {
        // key: contractPeriod
        if (historicClaimsPerContractPeriod == null) {
            lastReportedDate = periodCounter.startOfFirstPeriod();
            historicClaimsPerContractPeriod = new HashMap<Integer, List<SingleHistoricClaim>>();
            Map<String, SingleHistoricClaim> historicClaimsPerID = new HashMap<String, SingleHistoricClaim>();
            for (int row = history.getTitleRowCount(); row < history.getRowCount(); row++) {
                DateTime reportedDate = (DateTime) history.getValueAt(row, SingleHistoricClaimsConstraints.REPORT_DATE_INDEX);
                if(!reportedDate.isAfter(periodCounter.startOfFirstPeriod())) {
                    throw new SimulationException("Please check reported date in row " + (row + 1) + "of your table. The reported date appears to be before the start of the simulation."  );
                }
                if (reportedDate.isAfter(updateDate)) continue; // ignore any claim update after updateDate
                lastReportedDate = lastReportedDate.isBefore(reportedDate) ? reportedDate : lastReportedDate;
                String claimID = (String) history.getValueAt(row, SingleHistoricClaimsConstraints.ID_COLUMN_INDEX);
                DateTime occurrenceDate = (DateTime) history.getValueAt(row, SingleHistoricClaimsConstraints.OCCURRENCE_DATE_INDEX);
                Integer contractPeriod = InputFormatConverter.getInt(history.getValueAt(row, SingleHistoricClaimsConstraints.CONTRACT_PERIOD_INDEX)) - 1; // -1 : difference between UI and internal sight
                SingleHistoricClaim claim = historicClaimsPerID.get(claimID);
                boolean firstReportOfClaim = claim == null;
                if (firstReportOfClaim) {
                    claim = new SingleHistoricClaim(claimID, contractPeriod, occurrenceDate, periodCounter);
                    historicClaimsPerID.put(claimID, claim);
                    List<SingleHistoricClaim> historicClaimsOfContractPeriod = historicClaimsPerContractPeriod.get(contractPeriod);
                    if (historicClaimsOfContractPeriod == null) {
                        historicClaimsOfContractPeriod = new ArrayList<SingleHistoricClaim>();
                        historicClaimsPerContractPeriod.put(contractPeriod, historicClaimsOfContractPeriod);
                    }
                    historicClaimsOfContractPeriod.add(claim);
                }
                double cumulativePaid = InputFormatConverter.getDouble(history.getValueAt(row, SingleHistoricClaimsConstraints.PAID_AMOUNT_INDEX));
                double cumulativeReported = InputFormatConverter.getDouble(history.getValueAt(row, SingleHistoricClaimsConstraints.REPORTED_AMOUNT_INDEX));
                claim.add(reportedDate, cumulativePaid, cumulativeReported, firstReportOfClaim, periodCounter, claimID, occurrenceDate);
            }
        }
    }

    public List<GrossClaimRoot> claimWithAdjustedPattern(PatternPacket originalPayoutPattern, PayoutPatternBase base,
                                                         DateTime updateDate, DateTimeUtilities.Days360 days360, int currentPeriod) {
        List<SingleHistoricClaim> historicClaimsOfCurrentPeriod = historicClaimsPerContractPeriod.get(currentPeriod);
        List<GrossClaimRoot> historicClaimsWithAdjustedPattern = new ArrayList<GrossClaimRoot>();
        if (historicClaimsOfCurrentPeriod != null) {
            for (SingleHistoricClaim historicClaim : historicClaimsOfCurrentPeriod) {
                historicClaimsWithAdjustedPattern.add(historicClaim.claimWithAdjustedPattern(originalPayoutPattern, base, updateDate, days360));
            }
        }
        return historicClaimsWithAdjustedPattern;

    }

    /**
     * Correct exposure start date if first paid date is before.
     * @param baseClaims
     * @param contractPeriod
     * @param periodCounter
     * @param updateDate
     * @param base
     */
    public void checkClaimRootOccurrenceAgainstFirstActualPaid(List<ClaimRoot> baseClaims, int contractPeriod,
                                                               IPeriodCounter periodCounter, DateTime updateDate,
                                                               PayoutPatternBase base) {
        lazyInitHistoricClaimsPerContractPeriod(periodCounter, updateDate, base);
        List<SingleHistoricClaim> singleHistoricClaim = historicClaimsPerContractPeriod.get(contractPeriod);
        if (singleHistoricClaim == null) return;
//        Known payments before the exposure start date cannot be real. Invent a new root claim with a new exposure start
//        date. This is a rare case, in general this method should do nothing!
        for (SingleHistoricClaim claim : singleHistoricClaim) {
            ClaimRoot claimRoot = baseClaims.get(0); // todo(sku): shouldn't we rather use an iterator
            if (claim.firstActualPaidDateOrNull() != null && claimRoot.getExposureStartDate().isAfter(claim.firstActualPaidDateOrNull())) {
                ClaimRoot claimRoot1 = new ClaimRoot(claimRoot.getUltimate(), claimRoot.getClaimType(), claim.firstActualPaidDateOrNull(), claimRoot.getOccurrenceDate());
                baseClaims.clear();
                baseClaims.add(claimRoot1);
            }
        }

    }

    public DateTime lastReportedDate(IPeriodCounter periodCounter, DateTime updateDate, PayoutPatternBase payoutPatternBase) {
        lazyInitHistoricClaimsPerContractPeriod(periodCounter, updateDate, payoutPatternBase);
        return lastReportedDate;
    }

}
