package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimStorage {
    /** required in order to map with claim of previous period */
    private IClaimRoot reference;
    private IClaimRoot referenceCeded;
    private List<Double> inrementalReporteds = new ArrayList<Double>();
    private List<Double> inrementalPaids = new ArrayList<Double>();
    private double cumulatedReportedCeded;
    private double incrementalReportedCeded;
    private double cumulatedPaidCeded;
    private double incrementalPaidCeded;
    private double cumulatedStabilizedValue;

    public ClaimStorage(ClaimCashflowPacket claim) {
        if (claim.getNominalUltimate() > 0) {
            reference = ClaimUtils.scale(claim.getBaseClaim(), -1);
        }
        else {
            reference = claim.getBaseClaim();
        }
    }

    public void addIncrements(ClaimCashflowPacket claim) {
        inrementalPaids.add(claim.getPaidIncrementalIndexed());
        inrementalReporteds.add(claim.getReportedIncrementalIndexed());
    }

    public double getCumulatedCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return 0;
            case REPORTED:
                return cumulatedReportedCeded;
            case PAID:
                return cumulatedPaidCeded;
        }
        throw new NotImplementedException(claimProperty.toString());
    }


    public void update(double incrementalCeded, BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case PAID:
                incrementalPaidCeded = incrementalCeded;
                inrementalPaids.add(incrementalCeded);
                cumulatedPaidCeded += incrementalCeded;
                break;
            case REPORTED:
                incrementalReportedCeded = incrementalCeded;
                inrementalReporteds.add(incrementalCeded);
                cumulatedReportedCeded += incrementalCeded;
                break;
        }
    }

    public double getIncrementalPaidCeded() {
        return incrementalPaidCeded;
    }

    public double getIncrementalReportedCeded() {
        return incrementalReportedCeded;
    }


    public double cededReserves() {
        return referenceCeded.getUltimate() - cumulatedPaidCeded;
    }

    /**
     * @param cededShare should be negative
     * @return
     */
    public IClaimRoot lazyInitCededClaimRoot(double cededShare) {
        referenceCeded = referenceCeded == null ? reference.withScale(cededShare) : referenceCeded;
        return referenceCeded;
    }

    public IClaimRoot getCededClaimRoot() {
        return referenceCeded;
    }

    public boolean hasReferenceCeded() {
        return referenceCeded != null;
    }

    public double stabilizationFactor(ClaimCashflowPacket claim, IStabilizationStrategy stabilization, IPeriodCounter periodCounter) {
        double indexFactor = stabilization.indexFactor(claim, periodCounter);
        double claimPaidIncremental = claim.getPaidIncrementalIndexed() / indexFactor;
        cumulatedStabilizedValue += claimPaidIncremental;
        if (cumulatedStabilizedValue != 0 && stabilization.basedOnPaid()) {
            return claim.getPaidCumulatedIndexed() / cumulatedStabilizedValue;
        }
        else if (stabilization.basedOnReported()) {
            double outstanding = claim.outstandingIndexed() / indexFactor;
            if (outstanding + cumulatedStabilizedValue != 0) {
                return (claim.getPaidCumulatedIndexed() + claim.outstandingIndexed()) / (cumulatedStabilizedValue + outstanding);
            }
        }
        return 1;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(reference.toString());
        buffer.append(", cumulatedReportedCeded: ");
        buffer.append(cumulatedReportedCeded);
        buffer.append(", cumulatedPaidCeded: ");
        buffer.append(cumulatedPaidCeded);
        return buffer.toString();
    }

    public IClaimRoot getReference() {
        return reference;
    }

    public IClaimRoot getReferenceCeded() {
        return referenceCeded;
    }
}
