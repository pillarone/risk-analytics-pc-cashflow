package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Doc: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1540
 * It contains all shared information of several ClaimCashflowPacket objects and is used as key.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): implement payout only
// todo(sku): implement pattern shifts
// todo(sku): clarify index application order and effect on reported
public final class GrossClaimRoot implements IClaimRoot {

    private static Log LOG = LogFactory.getLog(GrossClaimRoot.class);

    private ClaimRoot claimRoot;

    // todo(sku): move to a derive GrossClaimRoot class
    private PatternPacket payoutPattern;
    private PatternPacket reportingPattern;

    private Boolean synchronizedPatterns;

    private Factors factors = new Factors();
    private double paidCumulatedIncludingAppliedFactors = 0d;
    private double reportedCumulatedIncludingAppliedFactors = 0d;

    /** counts the currently existing ClaimCashflowPacket referencing this instance */
    private int childCounter;

    public GrossClaimRoot(ClaimRoot claimRoot, PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this(payoutPattern, reportingPattern);
        this.claimRoot = claimRoot;
    }

    public GrossClaimRoot(double ultimate, EventPacket event, ClaimType claimType, ExposureInfo exposureInfo,
                          DateTime occurrenceDate, PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this(payoutPattern, reportingPattern);
        claimRoot = new ClaimRoot(ultimate, event, claimType, exposureInfo, occurrenceDate);
    }

    public GrossClaimRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate, DateTime occurrenceDate,
                          PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this(payoutPattern, reportingPattern);
        claimRoot = new ClaimRoot(ultimate, claimType, exposureStartDate, occurrenceDate);
    }

    public GrossClaimRoot(double ultimate, EventPacket event, ClaimType claimType, DateTime exposureStartDate,
                          DateTime occurrenceDate, PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this(payoutPattern, reportingPattern);
        claimRoot = new ClaimRoot(ultimate, event, claimType, exposureStartDate, occurrenceDate);
    }

    public GrossClaimRoot(PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this.payoutPattern = payoutPattern;
        this.reportingPattern = reportingPattern;
    }

    /**
     * Utility method to derive cashflow claim of a base claim using its ultimate and pattern
     * @param periodCounter
     * @return
     */
    public List<ClaimCashflowPacket> getClaimCashflowPackets(IPeriodCounter periodCounter, boolean hasUltimate) {
        return getClaimCashflowPackets(periodCounter, null, hasUltimate);
    }

    public List<ClaimCashflowPacket> getClaimCashflowPackets(IPeriodCounter periodCounter, Factors factors, boolean hasUltimate) {
        List<ClaimCashflowPacket> currentPeriodClaims = new ArrayList<ClaimCashflowPacket>();
        // todo(sku): refactor to avoid code duplication
        if (hasSynchronizedPatterns()) {
            List<DateFactors> payouts = payoutPattern.getDateFactorsForCurrentPeriod(claimRoot.getOccurrenceDate(), periodCounter);
            List<DateFactors> reports = reportingPattern.getDateFactorsForCurrentPeriod(claimRoot.getOccurrenceDate(), periodCounter);
            for (int i = 0; i < payouts.size(); i++) {
                DateTime payoutDate = payouts.get(i).getDate();
                double factor = manageFactor(factors, payoutDate);
                double payoutIncrementalFactor = payouts.get(i).getFactorIncremental();
                double payoutCumulatedFactor = payouts.get(i).getFactorCumulated();
                double reportsIncrementalFactor = reports.get(i).getFactorIncremental();
                double reportsCumulatedFactor = reports.get(i).getFactorCumulated();
                double ultimate = claimRoot.getUltimate();
                double reserves = ultimate * (1 - payoutCumulatedFactor) * factor;

                double paidIncremental = ultimate * payoutIncrementalFactor * factor;
                double paidCumulated = paidCumulatedIncludingAppliedFactors + paidIncremental;
                paidCumulatedIncludingAppliedFactors = paidCumulated;
                double reportedIncremental = ultimate * reportsIncrementalFactor * factor;
                double outstanding = ultimate * (reportsCumulatedFactor - payoutCumulatedFactor) * factor;
                double reportedCumulated = outstanding + paidCumulated;
                reportedCumulatedIncludingAppliedFactors = reportedCumulated;

                childCounter++;
                ClaimCashflowPacket cashflowPacket = new ClaimCashflowPacket(this, paidIncremental, paidCumulated,
                        reportedIncremental, reportedCumulated, reserves, payoutDate, periodCounter, hasUltimate);
                hasUltimate = false;    // a period may contain several payouts and only the first should contain the ultimate
                checkCorrectDevelopment(cashflowPacket);
                currentPeriodClaims.add(cashflowPacket);
            }
        }
        else if (!hasTrivialPayout() && !hasIBNR()) {
            List<DateFactors> payouts = payoutPattern.getDateFactorsForCurrentPeriod(claimRoot.getOccurrenceDate(), periodCounter);
            for (int i = 0; i < payouts.size(); i++) {
                DateTime payoutDate = payouts.get(i).getDate();
                double factor = manageFactor(factors, payoutDate);
                double payoutIncrementalFactor = payouts.get(i).getFactorIncremental();
                double payoutCumulatedFactor = payouts.get(i).getFactorCumulated();

                double ultimate = claimRoot.getUltimate();
                double paidIncremental = ultimate * payoutIncrementalFactor * factor;
                double paidCumulated = paidCumulatedIncludingAppliedFactors + paidIncremental;
                paidCumulatedIncludingAppliedFactors = paidCumulated;
                double reserves = ultimate * (1 - payoutCumulatedFactor) * factor;

                childCounter++;
                ClaimCashflowPacket cashflowPacket = new ClaimCashflowPacket(this, paidIncremental, paidCumulated,
                        0, 0, reserves, payoutDate, periodCounter, hasUltimate);
                checkCorrectDevelopment(cashflowPacket);
                currentPeriodClaims.add(cashflowPacket);
            }
        }
        else {
            ClaimCashflowPacket cashflowPacket = new ClaimCashflowPacket(this);
            currentPeriodClaims.add(cashflowPacket);
        }
        return currentPeriodClaims;
    }

    private double manageFactor(Factors factors, DateTime payoutDate) {
        if (factors == null) return 1d;
        Double factor = factors.getFactorAtDate(payoutDate);
        if (factor == null) {
            return 1d;
        }
        else {
            this.factors.add(payoutDate, factor);
            return factor;
        }
    }

    public double getUltimate() {
        return claimRoot.getUltimate();
    }

    public boolean hasEvent() {
        return claimRoot.hasEvent();
    }

    public EventPacket getEvent() {
        return claimRoot.getEvent();
    }

    public ClaimType getClaimType() {
        return claimRoot.getClaimType();
    }

    public ExposureInfo getExposureInfo() {
        return claimRoot.getExposureInfo();
    }

    public DateTime getExposureStartDate() {
        return claimRoot.getExposureStartDate();
    }

    public DateTime getOccurrenceDate() {
        return claimRoot.getOccurrenceDate();
    }

    /**
     * @return payout and reported pattern have the same period entries. True even if one of them is null
     */
    public boolean hasSynchronizedPatterns() {
        if (synchronizedPatterns == null) {
            if (reportingPattern == null || payoutPattern == null) {
                synchronizedPatterns = false;
            }
            else {
                synchronizedPatterns = reportingPattern.hasSameCumulativePeriods(payoutPattern);
            }
        }
        return synchronizedPatterns;
    }

    public boolean hasTrivialPayout() {
        return payoutPattern == null || payoutPattern.isTrivial();
    }

    public boolean hasIBNR() {
        return reportingPattern != null && !reportingPattern.isTrivial();
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(getUltimate());
        result.append(separator);
        result.append(getClaimType());
        result.append(separator);
        result.append(getOccurrenceDate());
        return result.toString();
    }

    /**
     * check that a claim is fully reported and paid
     * @param cashflowPacket
     */
    private void checkCorrectDevelopment(ClaimCashflowPacket cashflowPacket) {
        // todo(msp): why is log level not working correctly?
        if (LOG.isDebugEnabled()) {
//            developedUltimate = cashflowPacket.developedUltimate();
//            cumulatedPaid += cashflowPacket.getPaidIncremental();
            if (childCounter == payoutPattern.size()) {
                LOG.debug("developed ultimate: " + cashflowPacket.developedUltimate());
                LOG.debug("paid cumulated: " + paidCumulatedIncludingAppliedFactors);
//                System.out.println("   developed ultimate: " + cashflowPacket.developedUltimate());
//                System.out.println("   paid cumulated: " + paidCumulatedIncludingAppliedFactors);
//                System.out.println("   reported cumulated: " + reportedCumulatedIncludingAppliedFactors);

            }
        }
    }
}
