package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateEventClaimsStorage {

    private double incrementalUltimate;
    private double cumulatedUltimate;
    private double incrementalUltimateCeded;
    private double cumulatedUltimateCeded;
    private double cumulatedReported;
    private double cumulatedReportedCeded;
    private double incrementalReported;
    private double incrementalReportedLast;
    private double incrementalReportedCeded;
    private double cumulatedPaid;
    private double cumulatedPaidCeded;
    private double incrementalPaid;
    private double incrementalPaidLast;
    private double incrementalPaidCeded;

    private double cededFactorUltimate;
    private double cededFactorReported;
    private double cededFactorPaid;

    private double aadReductionInPeriodUltimate;
    private double aadReductionInPeriodReported;
    private double aadReductionInPeriodPaid;

    public AggregateEventClaimsStorage() {
    }

    /**
     * No check done if events are matching!
     * @param claim
     */
    public void add(ClaimCashflowPacket claim) {
        incrementalUltimate = claim.ultimate();
        cumulatedUltimate += claim.ultimate();
        cumulatedReported += claim.getReportedIncrementalIndexed();
        incrementalReported += claim.getReportedIncrementalIndexed();
        incrementalReportedLast = claim.getReportedIncrementalIndexed();
        cumulatedPaid += claim.getPaidIncrementalIndexed();
        incrementalPaid += claim.getPaidIncrementalIndexed();
        incrementalPaidLast = claim.getPaidIncrementalIndexed();
    }

    public void resetIncrementsAndFactors() {
        incrementalUltimate = 0;
        incrementalReported = 0;
        incrementalReportedLast = 0;
        incrementalPaid = 0;
        incrementalPaidLast = 0;
        cededFactorPaid = 0;
        cededFactorReported = 0;
        cededFactorUltimate = 0;
        aadReductionInPeriodUltimate = 0;
        aadReductionInPeriodReported = 0;
        aadReductionInPeriodPaid = 0;
    }

    public double getCumulatedCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return cumulatedUltimateCeded;
            case REPORTED:
                return cumulatedReportedCeded;
            case PAID:
                return cumulatedPaidCeded;
        }
        throw new NotImplementedException(claimProperty.toString());
    }

    public double getIncrementalCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return incrementalUltimateCeded;
            case REPORTED:
                return incrementalReportedCeded;
            case PAID:
                return incrementalPaidCeded;
        }
        throw new NotImplementedException(claimProperty.toString());
    }

    public double getAadReductionInPeriod(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return aadReductionInPeriodUltimate;
            case REPORTED:
                return aadReductionInPeriodReported;
            case PAID:
                return aadReductionInPeriodPaid;
        }
        throw new NotImplementedException(claimProperty.toString());
    }

    public void addAadReductionInPeriod(BasedOnClaimProperty claimProperty, double aadIncrement) {
        switch (claimProperty) {
            case ULTIMATE:
                aadReductionInPeriodUltimate += aadIncrement;
                break;
            case REPORTED:
                aadReductionInPeriodReported += aadIncrement;
                break;
            case PAID:
                aadReductionInPeriodPaid += aadIncrement;
                break;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getCededFactorUltimate() {
        return cededFactorUltimate;
    }

    public double getCededFactorReported() {
        return cededFactorReported;
    }

    public double getCededFactorPaid() {
        return cededFactorPaid;
    }

    public void update(BasedOnClaimProperty claimProperty, double incrementCeded) {
        switch (claimProperty) {
            case ULTIMATE:
                incrementalUltimateCeded = incrementCeded;
                cumulatedUltimateCeded += incrementCeded;
                break;
            case PAID:
                incrementalPaidCeded = incrementCeded;
                cumulatedPaidCeded += incrementCeded;
                break;
            case REPORTED:
                incrementalReportedCeded = incrementCeded;
                cumulatedReportedCeded += incrementCeded;
                break;
        }
    }

    public void setCededFactor(BasedOnClaimProperty claimProperty, double factor) {
        switch (claimProperty) {
            case ULTIMATE:
                cededFactorUltimate = factor;
                break;
            case PAID:
                cededFactorPaid = factor;
                break;
            case REPORTED:
                cededFactorReported = factor;
                break;
        }
    }

    public double getIncremental(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return incrementalUltimate;
            case PAID:
                return incrementalPaid;
            case REPORTED:
                return incrementalReported;
        }
        return 0;
    }

    public double getIncrementalLast(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return incrementalUltimate;
            case PAID:
                return incrementalPaidLast;
            case REPORTED:
                return incrementalReportedLast;
        }
        return 0;
    }

    public double getCumulated(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return cumulatedUltimate;
            case PAID:
                return cumulatedPaid;
            case REPORTED:
                return cumulatedReported;
        }
        return 0;
    }

    public void printFactors() {
        StringBuilder builder = new StringBuilder();
        builder.append("factors, ultimate: ");
        builder.append(cededFactorUltimate);
        builder.append(", reported: ");
        builder.append(cededFactorReported);
        builder.append(", paid: ");
        builder.append(cededFactorPaid);
        System.out.println(builder.toString());
    }
}
