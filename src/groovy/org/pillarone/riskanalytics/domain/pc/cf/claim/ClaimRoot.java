package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Pattern;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.segment.ISegmentMarker;

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
public final class ClaimRoot {

    private static Log LOG = LogFactory.getLog(ClaimRoot.class);

    private double ultimate;
    private EventPacket event;
    private ClaimType claimType;
    private ExposureInfo exposureInfo;
    private DateTime exposureStartDate;
    private DateTime occurrenceDate;
    private Pattern payoutPattern;
    private Pattern reportingPattern;

    private Boolean synchronizedPatterns;

    private Factors factors = new Factors();
    private double paidCumulatedIncludingAppliedFactors = 0d;
    private double reportedCumulatedIncludingAppliedFactors = 0d;

    /** counts the currently existing ClaimCashflowPacket referencing this instance */
    private int childCounter;

    private IPerilMarker peril;
    private ISegmentMarker segment;
    private IReinsuranceContractMarker reinsuranceContract;
    private ILegalEntityMarker legalEntity;



    public ClaimRoot(double ultimate, EventPacket event, ClaimType claimType, ExposureInfo exposureInfo,
                     DateTime occurrenceDate, Pattern payoutPattern, Pattern reportingPattern) {
        this(ultimate, event, claimType, exposureInfo.getDate(), occurrenceDate, payoutPattern, reportingPattern);
        this.exposureInfo = exposureInfo;
    }

    public ClaimRoot(double ultimate, EventPacket event, ClaimType claimType, DateTime exposureStartDate,
                     DateTime occurrenceDate, Pattern payoutPattern, Pattern reportingPattern) {
        this(ultimate, claimType, exposureStartDate, occurrenceDate, payoutPattern, reportingPattern);
        this.event = event;
    }

    public ClaimRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate,
                     DateTime occurrenceDate, Pattern payoutPattern, Pattern reportingPattern) {
        this(ultimate, claimType, exposureStartDate, occurrenceDate);
        this.payoutPattern = payoutPattern;
        this.reportingPattern = reportingPattern;
    }

    public ClaimRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate, DateTime occurrenceDate) {
        this.ultimate = ultimate;
        this.claimType = claimType;
        this.exposureStartDate = exposureStartDate;
        this.occurrenceDate = occurrenceDate;
    }

    /**
     * Utility method to derive cashflow claim of a base claim using its ultimate and pattern
     * @param periodCounter
     * @return
     */
    public List<ClaimCashflowPacket> getClaimCashflowPackets(IPeriodCounter periodCounter) {
        return getClaimCashflowPackets(periodCounter, null, true);
    }

    public List<ClaimCashflowPacket> getClaimCashflowPackets(IPeriodCounter periodCounter, Factors factors,
                                                             boolean currentPeriodOnly) {
        List<ClaimCashflowPacket> currentPeriodClaims = new ArrayList<ClaimCashflowPacket>();
        if (hasSynchronizedPatterns()) {
            List<DateFactors> payouts = payoutPattern.getDateFactors(occurrenceDate, periodCounter, currentPeriodOnly);
            List<DateFactors> reports = reportingPattern.getDateFactors(occurrenceDate, periodCounter, currentPeriodOnly);
            for (int i = 0; i < payouts.size(); i++) {
                DateTime payoutDate = payouts.get(i).getDate();
                double factor = manageFactor(factors, payoutDate);
                double payoutIncrementalFactor = payouts.get(i).getFactorIncremental();
                double payoutCumulatedFactor = payouts.get(i).getFactorCumulated();
                double reportsIncrementalFactor = reports.get(i).getFactorIncremental();
                double reportsCumulatedFactor = reports.get(i).getFactorCumulated();

                double paidIncremental = ultimate * payoutIncrementalFactor * factor;
                double paidCumulated = paidCumulatedIncludingAppliedFactors + paidIncremental;
                paidCumulatedIncludingAppliedFactors = paidCumulated;
                double reportedIncremental = ultimate * reportsIncrementalFactor * factor;
                double outstanding = ultimate * (reportsCumulatedFactor - payoutCumulatedFactor) * factor;
                double reportedCumulated = outstanding + paidCumulated;
                reportedCumulatedIncludingAppliedFactors = reportedCumulated;
                double reserves = ultimate * (1 - payoutCumulatedFactor) * factor;

                childCounter++;
                ClaimCashflowPacket cashflowPacket = new ClaimCashflowPacket(this, paidIncremental, paidCumulated,
                        reportedIncremental, reportedCumulated, reserves, payoutDate, periodCounter, childCounter);
                checkCorrectDevelopment(cashflowPacket);
                currentPeriodClaims.add(cashflowPacket);
            }
        }
        else {
            throw new NotImplementedException();
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
        return ultimate;
    }

    public boolean hasEvent() {
        return event != null;
    }

    public EventPacket getEvent() {
        return event;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public ExposureInfo getExposureInfo() {
        return exposureInfo;
    }

    public DateTime getExposureStartDate() {
        return exposureStartDate;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    /**
     * @return payout and reported pattern have the same period entries. True even if one of them is null
     */
    public boolean hasSynchronizedPatterns() {
        if (synchronizedPatterns == null) {
            synchronizedPatterns = reportingPattern.hasSameCumulativePeriods(payoutPattern);
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
        result.append(ultimate);
        result.append(separator);
        result.append(claimType);
        result.append(separator);
        result.append(occurrenceDate);
        return result.toString();
    }

    /**
     * check that a claim is fully reported and paid
     * @param cashflowPacket
     */
    private void checkCorrectDevelopment(ClaimCashflowPacket cashflowPacket) {
        // todo(msp): why is log level not working correctly?
//        if (LOG.isDebugEnabled()) {
//            developedUltimate = cashflowPacket.developedUltimate();
//            cumulatedPaid += cashflowPacket.getPaidIncremental();
            if (childCounter == payoutPattern.size()) {
//                LOG.debug("developed ultimate: " + cashflowPacket.developedUltimate());
//                LOG.debug("paid cumulated: " + paidCumulatedIncludingAppliedFactors);
                System.out.println("developed ultimate: " + cashflowPacket.developedUltimate());
                System.out.println("paid cumulated: " + paidCumulatedIncludingAppliedFactors);
                System.out.println("reported cumulated: " + reportedCumulatedIncludingAppliedFactors);

            }
//        }
    }
}
