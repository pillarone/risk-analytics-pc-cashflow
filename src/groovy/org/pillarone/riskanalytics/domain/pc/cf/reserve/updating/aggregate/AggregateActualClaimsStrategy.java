package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateActualClaimsStrategy extends AbstractParameterObject implements IAggregateActualClaimsStrategy {

    private ConstrainedMultiDimensionalParameter history = getDefaultHistory();

    public IParameterObjectClassifier getType() {
        return AggregateActualClaimsStrategyType.AGGREGATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("history", history);
        return parameters;
    }

    public static ConstrainedMultiDimensionalParameter getDefaultHistory() {
        return AggregateHistoricClaimsConstraints.getDefault();
    }

    private Map<Integer, AggregateHistoricClaim> historicClaimsPerContractPeriod;

    /**
     * This function has to be called during period 0 in order to fill the periodStore correctly! The content of the
     * reported amount column is ignored. historicClaimsPerContractPeriod is filled.
     * @param periodCounter required for date calculations
     * @param updateDate all reported claims after the updateDate are ignored
     */
    public void lazyInitHistoricClaimsPerContractPeriod(IPeriodCounter periodCounter, DateTime updateDate) {
        // key: contractPeriod
        if (historicClaimsPerContractPeriod == null) {
            historicClaimsPerContractPeriod = new HashMap<Integer, AggregateHistoricClaim>();
            for (int row = history.getTitleRowCount(); row < history.getRowCount(); row++) {
                DateTime reportedDate = (DateTime) history.getValueAt(row, AggregateHistoricClaimsConstraints.REPORT_DATE_INDEX);
                if (reportedDate.isAfter(updateDate)) continue; // ignore any claim update after updateDate
                Integer contractPeriod = InputFormatConverter.getInt(history.getValueAt(row, AggregateHistoricClaimsConstraints.CONTRACT_PERIOD_INDEX)) - 1; // -1 : difference between UI and internal sight
                // as this method is called always for initialization in the first iteration and period
                // iterationStore access without arguments is always reading and writing to period 0
                AggregateHistoricClaim claim = historicClaimsPerContractPeriod.get(contractPeriod);
                if (claim == null) {
                    claim = new AggregateHistoricClaim(contractPeriod, periodCounter, PayoutPatternBase.PERIOD_START_DATE);
                    historicClaimsPerContractPeriod.put(contractPeriod, claim);
                }
                double cumulativePaid = InputFormatConverter.getDouble(history.getValueAt(row, AggregateHistoricClaimsConstraints.PAID_AMOUNT_INDEX));
                double cumulativeReported = InputFormatConverter.getDouble(history.getValueAt(row, AggregateHistoricClaimsConstraints.REPORTED_AMOUNT_INDEX));
                claim.add(reportedDate, cumulativeReported, cumulativePaid);
            }
        }
    }

    /**
     * Creates a GrossClaimRoot. It's payout pattern is modified if the current period end is before the update date and
     * there exists a AggregateHistoricClaim for contractPeriod.
     * @param claimRoot providing the ultimate and occurrence date
     * @param contractPeriod
     * @param payoutPattern original payout pattern
     * @param periodCounter
     * @param updateDate
     * @return a GrossClaimRoot with a possibly modified payoutPattern
     */
    public GrossClaimRoot claimWithAdjustedPattern(ClaimRoot claimRoot, int contractPeriod, PatternPacket payoutPattern,
                                                   IPeriodCounter periodCounter, DateTime updateDate) {
        if (!periodCounter.getCurrentPeriodEnd().isAfter(updateDate)) {
            lazyInitHistoricClaimsPerContractPeriod(periodCounter, updateDate);
            AggregateHistoricClaim historicClaim = historicClaimsPerContractPeriod.get(contractPeriod);
            if (historicClaim != null) {
                return historicClaim.claimWithAdjustedPattern(payoutPattern, claimRoot, updateDate);
            }
        }
        return new GrossClaimRoot(claimRoot, payoutPattern);
    }

    public AggregateHistoricClaim historicClaims(int period, IPeriodCounter periodCounter, DateTime updateDate) {
        lazyInitHistoricClaimsPerContractPeriod(periodCounter, updateDate);
        return historicClaimsPerContractPeriod.get(period);
    }

    /**
     * @return true if the update date is within or before the current period, false if globalUpdateDate is null
     */
    protected boolean beforeUpdateDate(DateTime nextPeriodStartDate, DateTime updateDate) {
        if (updateDate == null) {
            return false;
        }
        else {
            return nextPeriodStartDate.isBefore(updateDate);
        }
    }
}
