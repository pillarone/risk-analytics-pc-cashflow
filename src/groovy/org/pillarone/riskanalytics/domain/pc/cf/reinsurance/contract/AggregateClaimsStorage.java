package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateClaimsStorage {

    private double ultimate;
    private double ultimateCeded;
    private double totalCumulated;
    private double totalIncremental;
    private double cumulatedTotalCeded;
    private double incrementalTotalCeded;
    private double cumulatedReported;
    private double cumulatedReportedCeded;
    private double incrementalReported;
    private double incrementalReportedCeded;
    private double cumulatedPaid;
    private double cumulatedPaidCeded;
    private double incrementalPaid;
    private double incrementalPaidCeded;

    private double cededFactorUltimate;
    private double cededFactorTotal;
    private double cededFactorReported;
    private double cededFactorPaid;

    public AggregateClaimsStorage(ClaimCashflowPacket claim) {
    }

    /**
     * No check done if events are matching!
     * @param claim
     */
    public void add(ClaimCashflowPacket claim) {
        ultimate += claim.ultimate();
        totalIncremental += claim.totalIncrementalIndexed();
        totalCumulated += claim.totalCumulatedIndexed();
        cumulatedReported += claim.getReportedIncrementalIndexed();
        incrementalReported += claim.getReportedIncrementalIndexed();
        cumulatedPaid += claim.getPaidIncrementalIndexed();
        incrementalPaid += claim.getPaidIncrementalIndexed();
    }

    public void resetIncrementsAndFactors() {
        totalIncremental = 0;
        incrementalReported = 0;
        incrementalPaid = 0;
        cededFactorPaid = 0;
        cededFactorReported = 0;
        cededFactorUltimate = 0;
    }

    public double getCumulatedCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return 0;
            case ULTIMATE_INDEXED:
                return cumulatedTotalCeded;
            case REPORTED:
                return cumulatedReportedCeded;
            case PAID:
                return cumulatedPaidCeded;
        }
        throw new NotImplementedException(claimProperty.toString());
    }

    public double getIncrementalCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return 0;
            case ULTIMATE_INDEXED:
                return incrementalTotalCeded;
            case REPORTED:
                return incrementalReportedCeded;
            case PAID:
                return incrementalPaidCeded;
        }
        throw new NotImplementedException(claimProperty.toString());
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
            case ULTIMATE_UNINDEXED:
                ultimateCeded += incrementCeded;
                break;
            case ULTIMATE_INDEXED:
                cumulatedTotalCeded += incrementCeded;
                incrementalTotalCeded = incrementCeded;
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
            case ULTIMATE_UNINDEXED:
                cededFactorUltimate = factor;
                break;
            case ULTIMATE_INDEXED:
                cededFactorTotal = factor;
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
            case ULTIMATE_UNINDEXED:
                return ultimate;
            case ULTIMATE_INDEXED:
                return totalIncremental;
            case PAID:
                return incrementalPaid;
            case REPORTED:
                return incrementalReported;
        }
        return 0;
    }

    public double getCumulated(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE_UNINDEXED:
                return ultimate;
            case ULTIMATE_INDEXED:
                return totalCumulated;
            case PAID:
                return cumulatedPaid;
            case REPORTED:
                return cumulatedReported;
        }
        return 0;
    }

    public void printFactors() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("factors, ultimate: ");
        buffer.append(cededFactorUltimate);
        buffer.append(", reported: ");
        buffer.append(cededFactorReported);
        buffer.append(", paid: ");
        buffer.append(cededFactorPaid);
        System.out.println(buffer.toString());
    }
}
