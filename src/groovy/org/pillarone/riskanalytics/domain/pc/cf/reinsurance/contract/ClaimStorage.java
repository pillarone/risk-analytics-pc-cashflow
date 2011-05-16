package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

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
    private double cumulatedPaidCeded;

    public ClaimStorage(ClaimCashflowPacket claim) {
        reference = claim.getBaseClaim();
    }

    public void addIncrements(ClaimCashflowPacket claim) {
        inrementalPaids.add(claim.getPaidIncremental());
        inrementalReporteds.add(claim.getReportedIncremental());
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

    /**
     * @param cumulatedPaidCeded
     * @return incrementalPaidCeded
     */
    public double updatePaid(double cumulatedPaidCeded) {
        double incrementalPaid = cumulatedPaidCeded - this.cumulatedPaidCeded;
        this.cumulatedPaidCeded = cumulatedPaidCeded;
        inrementalPaids.add(incrementalPaid);
        return incrementalPaid;
    }

    /**
     * @param cumulatedReportedCeded
     * @return incrementalReportedCeded
     */
    public double updateReported(double cumulatedReportedCeded) {
        double incrementalReported = cumulatedReportedCeded - this.cumulatedReportedCeded;
        this.cumulatedReportedCeded = cumulatedReportedCeded;
        inrementalReporteds.add(incrementalReported);
        return incrementalReported;
    }

    /**
     * @param cededShare should be negative
     * @param contractMarker
     * @return
     */
    public IClaimRoot lazyInitCededClaimRoot(double cededShare, IReinsuranceContractMarker contractMarker) {
        referenceCeded = referenceCeded == null ? reference.withScale(cededShare, contractMarker) : referenceCeded;
        return referenceCeded;
    }

    public IClaimRoot getCededClaimRoot() {
        return referenceCeded;
    }

    public boolean hasReferenceCeded() {
        return referenceCeded != null;
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
}
