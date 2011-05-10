package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

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

    /**
     * @param cededShare should be negative
     * @param contractMarker
     * @return
     */
    public IClaimRoot getCededClaimRoot(double cededShare, IReinsuranceContractMarker contractMarker) {
        referenceCeded = referenceCeded == null ? reference.withScale(cededShare, contractMarker) : referenceCeded;
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
