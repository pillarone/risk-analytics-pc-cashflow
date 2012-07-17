package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import static org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils.avoidNegativeZero;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * As ClaimCashflowPacket don't track any historic information/contains only incremental values and IReinsuranceContract
 * are operating on a cumulated bases this helper class is keeping track of a ClaimCashflowPacket using the keyClaim
 * as a link (reference, referenceCeded). Following our general concept a minimal set of information is kept in order to
 * keep these objects as lean
 * as possible.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimStorage {
    /** required in order to map with claim of previous period */
    private IClaimRoot reference;
    private IClaimRoot referenceCeded;

    /** required to calculate the ceded changeIn... properties */ 
    private List<Double> cededReserves = new ArrayList<Double>();
    private List<Double> cededIbnr = new ArrayList<Double>();
     
    private double cumulatedReportedCeded;
    private double incrementalReportedCeded;
    private double cumulatedPaidCeded;
    private double incrementalPaidCeded;
    private double cumulatedStabilizedValue;
    private double nominalUltimate;
    private double ultimate;
    private double cumulatedUltimateDevelopedCeded;

    public ClaimStorage(ClaimCashflowPacket claim) {
        // todo: same check is done twice in ClaimHistoryAndApplicableContract
        if (claim.getNominalUltimate() > 0) {
            reference = ClaimUtils.scale(claim.getBaseClaim(), -1);
        }
        else {
            reference = claim.getBaseClaim();
        }
    }

    /** helper method for more generic call interfaces */
    public double getCumulatedCeded(BasedOnClaimProperty claimProperty) {
        switch (claimProperty) {
            case ULTIMATE:
                return ultimate;
            case REPORTED:
                return cumulatedReportedCeded;
            case PAID:
                return cumulatedPaidCeded;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    /** helper method for more generic call interfaces */
    public void update(double incrementalCeded, BasedOnClaimProperty claimProperty) {
        incrementalCeded = avoidNegativeZero(incrementalCeded);
        switch (claimProperty) {
            case ULTIMATE:
                ultimate = incrementalCeded;
                if (ultimate != 0) {
                    nominalUltimate = ultimate;
                }
                if (referenceCeded.getClaimType().equals(ClaimType.AGGREGATED_RESERVES)) {
                    nominalUltimate = referenceCeded.getUltimate();
                }
                break;
            case PAID:
                incrementalPaidCeded = incrementalCeded;
                cumulatedPaidCeded += incrementalCeded;
                cededReserves.add(cededReserves());
                break;
            case REPORTED:
                incrementalReportedCeded = incrementalCeded;
                cumulatedReportedCeded += incrementalCeded;
                cededIbnr.add(cededIBNR());
                break;
            default:
                throw new NotImplementedException(claimProperty.toString());
        }
    }

    public void setCumulatedUltimateDevelopedCeded(double cumulatedUltimateDevelopedCeded) {
        if (Math.abs(cumulatedUltimateDevelopedCeded) < 1E-10) return;
        this.cumulatedUltimateDevelopedCeded = cumulatedUltimateDevelopedCeded;
    }

    public double getNominalUltimate() {
        return nominalUltimate;
    }

    public double getIncrementalPaidCeded() {
        return incrementalPaidCeded;
    }

    public double getIncrementalReportedCeded() {
        return incrementalReportedCeded;
    }

    public double cededReserves() {
        return nominalUltimate - cumulatedPaidCeded + cumulatedUltimateDevelopedCeded;
    }

    public double cededIBNR() {
        return cededReserves() - cumulatedReportedCeded + cumulatedPaidCeded;
    }

    public double changeInCededReserves() {
        return cededReserves() - previousCededReserves();
    }

    public double changeInCededIBNR() {
        return cededIBNR() - previousCededIBNR();
    }

    public double previousCededReserves() {
        return (cededReserves.size() == 1) ? 0 : cededReserves.get(cededReserves.size() - 2);
    }
    
    private double previousCededIBNR() {
        return (cededIbnr.size() == 1) ? 0 : cededIbnr.get(cededIbnr.size() - 2);
    }

    /**
     * @param cededShare has to be negative
     * @return
     */
    public IClaimRoot lazyInitCededClaimRoot(double cededShare) {
        referenceCeded = referenceCeded == null ? reference.withScale(cededShare * -Math.signum(reference.getUltimate())) : referenceCeded;
        if (referenceCeded.getUltimate() < 0 || cededShare > 0) {
            throw new IllegalArgumentException("cededShare has to be negative and the ultimate of a ceded reference claim positive");
        }
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
        if (stabilization.basedOnPaid()) {
            if (cumulatedStabilizedValue != 0) {
                return claim.getPaidCumulatedIndexed() / cumulatedStabilizedValue;
            }
            else {
                return indexFactor;
            }
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
        StringBuilder builder = new StringBuilder();
        builder.append(reference.toString());
        builder.append(", cumulatedReportedCeded: ");
        builder.append(cumulatedReportedCeded);
        builder.append(", cumulatedPaidCeded: ");
        builder.append(cumulatedPaidCeded);
        return builder.toString();
    }

    public IClaimRoot getReference() {
        return reference;
    }

    public IClaimRoot getReferenceCeded() {
        return referenceCeded;
    }

    /**
     * Factory method wrapping the @claim in a ClaimStorage object, adding it to the @storage and returning it
     * @param claim wrapped in a ClaimStorage and added to the storage
     * @param storage the @claim is added to this storage using is keyClaim property
     * @return claim wrapped in a ClaimStorage object
     */
    public static ClaimStorage makeStoredClaim(ClaimCashflowPacket claim, Map<IClaimRoot, ClaimStorage> storage) {
        ClaimStorage claimStorage = new ClaimStorage(claim);
        storage.put(claim.getKeyClaim(), claimStorage);
        return claimStorage;
    }
}
