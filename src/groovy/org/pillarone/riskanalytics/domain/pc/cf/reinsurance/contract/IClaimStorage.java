package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimStorage {

    /**
     * add incremental reported and paid values to internal history
     * @param claim its reported and paid value are added to history
     */
    void addIncrements(ClaimCashflowPacket claim);

    /**
     * @param claimProperty
     * @return cumulated ceded value corresponding to claimProperty
     */
    double getCumulatedCeded(BasedOnClaimProperty claimProperty);

    /**
     * similar to addIncrements but updates additionally corresponding incremental and cumulated values
     * // todo(sku) merge with addIncrements
     * @param incrementalCeded
     * @param claimProperty
     */
    void update(double incrementalCeded, BasedOnClaimProperty claimProperty);

    double getIncrementalPaidCeded();

    double getIncrementalReportedCeded();

    /**
     * @return reference ceded ultimate - cumulated ceded paid
     */
    double cededReserves();

    /**
     * @param cededShare should be negative
     * @return
     */
    IClaimRoot lazyInitCededClaimRoot(double cededShare);

    IClaimRoot getCededClaimRoot();

    boolean hasReferenceCeded();

    /**
     * Calculates the stabilization factor according to the selected strategy. Needs to update stabilized member variables.
     * @param claim
     * @param stabilization
     * @param periodCounter
     * @return
     */
    double stabilizationFactor(ClaimCashflowPacket claim, IStabilizationStrategy stabilization, IPeriodCounter periodCounter);

    IClaimRoot getReference();

    IClaimRoot getReferenceCeded();

    // ============================= Aggregate Event methods ===========================
    // todo(sku): merge with addIncrements/update above
    void add(ClaimCashflowPacket claim);

    void resetIncrementsAndFactors();
    double getIncrementalCeded(BasedOnClaimProperty claimProperty);
    double getCededFactorUltimate();
    double getCededFactorReported();
    double getCededFactorPaid();
    void update(BasedOnClaimProperty claimProperty, double incrementCeded);
    void setCededFactor(BasedOnClaimProperty claimProperty, double factor);
    double getIncremental(BasedOnClaimProperty claimProperty);
    double getIncrementalLast(BasedOnClaimProperty claimProperty);
    double getCumulated(BasedOnClaimProperty claimProperty);
}
