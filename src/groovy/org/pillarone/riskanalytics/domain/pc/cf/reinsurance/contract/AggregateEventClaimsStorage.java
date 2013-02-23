package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateEventClaimsStorage {

    private double incrementalUltimateUnindexed;
    private double incrementalUltimateIndexed;
    private double cumulatedUltimateUnindexed;
    private double cumulatedUltimateIndexed;
    private double incrementalUltimateCededUnindexed;
    private double cumulatedUltimateCededUnindexed;
    private double incrementalUltimateCededIndexed;
    private double cumulatedUltimateCededIndexed;
    private double incrementalUltimateIndexedLast;
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
    private double cededFactorUltimateIndexed;
    private double cededFactorReported;
    private double cededFactorPaid;

    private double aadReductionInPeriodUltimateUnindexed;
    private double aadReductionInPeriodUltimateIndexed;
    private double aadReductionInPeriodReported;
    private double aadReductionInPeriodPaid;

    public AggregateEventClaimsStorage() {
    }

    /**
     * No check done if events are matching!
     * @param claim
     */
    public void add(ClaimCashflowPacket claim) {
        incrementalUltimateUnindexed = claim.ultimate();
        cumulatedUltimateUnindexed += claim.ultimate();
        incrementalUltimateIndexed = claim.totalIncrementalIndexed();
        incrementalUltimateIndexedLast = claim.totalIncrementalIndexed();
        cumulatedUltimateIndexed += claim.totalIncrementalIndexed();
        cumulatedReported += claim.getReportedIncrementalIndexed();
        incrementalReported += claim.getReportedIncrementalIndexed();
        incrementalReportedLast = claim.getReportedIncrementalIndexed();
        cumulatedPaid += claim.getPaidIncrementalIndexed();
        incrementalPaid += claim.getPaidIncrementalIndexed();
        incrementalPaidLast = claim.getPaidIncrementalIndexed();
    }

    /**
     * resets all properties to 0
     */
    public void resetIncrementsAndFactors() {
        incrementalUltimateUnindexed = 0;
        incrementalUltimateIndexed = 0;
        incrementalUltimateIndexedLast = 0;
        incrementalReported = 0;
        incrementalReportedLast = 0;
        incrementalPaid = 0;
        incrementalPaidLast = 0;
        cededFactorPaid = 0;
        cededFactorReported = 0;
        cededFactorUltimate = 0;
        aadReductionInPeriodUltimateUnindexed = 0;
        aadReductionInPeriodReported = 0;
        aadReductionInPeriodPaid = 0;
    }

    public double getCumulatedCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return cumulatedUltimateCededUnindexed;
            case ULTIMATE_INDEXED:
                return cumulatedUltimateCededIndexed;
            case REPORTED:
                return cumulatedReportedCeded;
            case PAID:
                return cumulatedPaidCeded;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getIncrementalCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return incrementalUltimateCededUnindexed;
            case ULTIMATE_INDEXED:
                return incrementalUltimateCededIndexed;
            case REPORTED:
                return incrementalReportedCeded;
            case PAID:
                return incrementalPaidCeded;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getAadReductionInPeriod(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return aadReductionInPeriodUltimateUnindexed;
            case ULTIMATE_INDEXED:
                return aadReductionInPeriodUltimateIndexed;
            case REPORTED:
                return aadReductionInPeriodReported;
            case PAID:
                return aadReductionInPeriodPaid;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public void addAadReductionInPeriod(BasedOnClaimProperty claimProperty, double aadIncrement) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                aadReductionInPeriodUltimateUnindexed += aadIncrement;
                break;
            case ULTIMATE_INDEXED:
                aadReductionInPeriodUltimateIndexed += aadIncrement;
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

    public void update(BasedOnClaimProperty claimProperty, double incrementCeded) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                incrementalUltimateCededUnindexed = incrementCeded;
                cumulatedUltimateCededUnindexed += incrementCeded;
                break;
            case ULTIMATE_INDEXED:
                incrementalUltimateCededIndexed = incrementCeded;
                cumulatedUltimateCededIndexed += incrementCeded;
                break;
            case PAID:
                incrementalPaidCeded = incrementCeded;
                cumulatedPaidCeded += incrementCeded;
                break;
            case REPORTED:
                incrementalReportedCeded = incrementCeded;
                cumulatedReportedCeded += incrementCeded;
                break;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getCededFactor(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return cededFactorUltimate;
            case ULTIMATE_INDEXED:
                return cededFactorUltimateIndexed;
            case PAID:
                return cededFactorPaid;
            case REPORTED:
                return cededFactorReported;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public void setCededFactor(BasedOnClaimProperty claimProperty, double factor) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                cededFactorUltimate = factor;
                break;
            case ULTIMATE_INDEXED:
                cededFactorUltimateIndexed = factor;
                break;
            case PAID:
                cededFactorPaid = factor;
                break;
            case REPORTED:
                cededFactorReported = factor;
                break;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getIncremental(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return incrementalUltimateUnindexed;
            case ULTIMATE_INDEXED:
                return incrementalUltimateIndexed;
            case PAID:
                return incrementalPaid;
            case REPORTED:
                return incrementalReported;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getIncrementalLast(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return incrementalUltimateUnindexed;
            case ULTIMATE_INDEXED:
                return incrementalUltimateIndexedLast;
            case PAID:
                return incrementalPaidLast;
            case REPORTED:
                return incrementalReportedLast;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public double getCumulated(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return cumulatedUltimateUnindexed;
            case ULTIMATE_INDEXED:
                return cumulatedUltimateIndexed;
            case PAID:
                return cumulatedPaid;
            case REPORTED:
                return cumulatedReported;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
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
