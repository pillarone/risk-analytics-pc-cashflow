package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Doc: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1540
 * It contains all shared information of several ClaimCashflowPacket objects and is used as key.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): implement pattern shifts
public final class GrossClaimRoot implements IClaimRoot {

    private static Log LOG = LogFactory.getLog(GrossClaimRoot.class);

    private ClaimRoot claimRoot;

    private PatternPacket payoutPattern;
    private PatternPacket reportingPattern;

    private Boolean synchronizedPatterns;

    private FactorsPacket factors = new FactorsPacket();
    private double paidCumulatedIncludingAppliedFactors = 0d;
    private double reportedCumulatedIncludingAppliedFactors = 0d;

    /** counts the currently existing ClaimCashflowPacket referencing this instance */
    private int childCounter;

    public GrossClaimRoot(ClaimRoot claimRoot, PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this(payoutPattern, reportingPattern);
        this.claimRoot = claimRoot;
    }

    public GrossClaimRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate, DateTime occurrenceDate,
                          PatternPacket payoutPattern, PatternPacket reportingPattern) {
        this(payoutPattern, reportingPattern);
        claimRoot = new ClaimRoot(ultimate, claimType, exposureStartDate, occurrenceDate);
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

    public List<ClaimCashflowPacket> getClaimCashflowPackets(IPeriodCounter periodCounter, List<Factors> factors, boolean hasUltimate) {
        List<ClaimCashflowPacket> currentPeriodClaims = new ArrayList<ClaimCashflowPacket>();
        if (!hasTrivialPayout()) {
            List<DateFactors> payouts = payoutPattern.getDateFactorsForCurrentPeriod(claimRoot.getOccurrenceDate(), periodCounter);
            List<DateFactors> reports = reportingPattern != null ?
                    reportingPattern.getDateFactorsForCurrentPeriod(claimRoot.getOccurrenceDate(), periodCounter)
                    : null;
            for (int i = 0; i < payouts.size(); i++) {
                DateTime payoutDate = payouts.get(i).getDate();
                double factor = manageFactor(factors, payoutDate, periodCounter, claimRoot.getOccurrenceDate());
                double payoutIncrementalFactor = payouts.get(i).getFactorIncremental();
                double payoutCumulatedFactor = payouts.get(i).getFactorCumulated();
                double ultimate = claimRoot.getUltimate();
                double reserves = ultimate * (1 - payoutCumulatedFactor) * factor;
                double paidIncremental = ultimate * payoutIncrementalFactor * factor;
                double paidCumulated = paidCumulatedIncludingAppliedFactors + paidIncremental;
                paidCumulatedIncludingAppliedFactors = paidCumulated;
                ClaimCashflowPacket cashflowPacket;
                if (!hasIBNR()) {
                    cashflowPacket = new ClaimCashflowPacket(this, paidIncremental, paidCumulated,
                            reserves, payoutDate, periodCounter, hasUltimate);
                }
                else {
                    double reportedIncremental = reportedIncremental(ultimate, factor, reports, i);
                    double reportedCumulated = reportedCumulated(ultimate, paidCumulated, factor, payoutCumulatedFactor, reports, i);
                    reportedCumulatedIncludingAppliedFactors = reportedCumulated;
                    cashflowPacket = new ClaimCashflowPacket(this, paidIncremental, paidCumulated,
                            reportedIncremental, reportedCumulated, reserves, payoutDate, periodCounter, hasUltimate);
                }
                childCounter++;
                hasUltimate = false;    // a period may contain several payouts and only the first should contain the ultimate
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

    private double reportedIncremental(double ultimate, double factor, List<DateFactors> reports, int idx) {
        if (hasSynchronizedPatterns()) {
            // set reportsIncrementalFactor = 0 if payout pattern is longer than reported pattern
            double reportsIncrementalFactor = idx < reports.size() ? reports.get(idx).getFactorIncremental() : 0d;
            return ultimate * reportsIncrementalFactor * factor;
        }
        return 0;
    }

    private double reportedCumulated(double ultimate, double paidCumulated, double factor, double payoutCumulatedFactor,
                                     List<DateFactors> reports, int idx) {
        if (hasSynchronizedPatterns()) {
            // set reportsCumulatedFactor = 1 if payout pattern is longer than reported pattern
            double reportsCumulatedFactor = idx < reports.size() ? reports.get(idx).getFactorCumulated() : 1d;
            double outstanding = ultimate * (reportsCumulatedFactor - payoutCumulatedFactor) * factor;
            return outstanding + paidCumulated;
        }
        return 0;
    }

    private double manageFactor(List<Factors> factors, DateTime payoutDate, IPeriodCounter periodCounter, DateTime dateOfLoss) {
        Double productFactor = IndexUtils.aggregateFactor(factors, payoutDate, periodCounter, dateOfLoss);
        this.factors.add(payoutDate, productFactor);
        return productFactor;
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

    public ExposureInfo getExposure() {
        return claimRoot.getExposure();
    }

    public DateTime getExposureStartDate() {
        return claimRoot.getExposureStartDate();
    }

    public DateTime getOccurrenceDate() {
        return claimRoot.getOccurrenceDate();
    }

    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        return claimRoot.getOccurrencePeriod(periodCounter);
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
                synchronizedPatterns = PatternPacket.hasSameCumulativePeriods(payoutPattern, reportingPattern, true);
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

    public ClaimRoot withScale(double scaleFactor) {
        return claimRoot.withScale(scaleFactor);
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
        if (LOG.isDebugEnabled()) {
            if (childCounter == payoutPattern.size() & cashflowPacket.developedUltimate() != paidCumulatedIncludingAppliedFactors) {
                LOG.debug("developed ultimate: " + cashflowPacket.developedUltimate());
                LOG.debug("paid cumulated: " + paidCumulatedIncludingAppliedFactors);
            }
        }
    }
}
