package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AggregateEventClaimsStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IPremiumAllocationStrategy;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CXLContract extends XLContract {

    protected Map<EventPacket, AggregateEventClaimsStorage> cededShareByEvent = new LinkedHashMap<EventPacket, AggregateEventClaimsStorage>();

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     *
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param reinstatementPremiumFactors
     * @param premiumAllocation
     */
    public CXLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                       double aggregateLimit, List<Double> reinstatementPremiumFactors,
                       IPremiumAllocationStrategy premiumAllocation) {
        super(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit, reinstatementPremiumFactors,
            premiumAllocation);
    }

    public void initPeriodClaims(List<ClaimCashflowPacket> grossClaims) {
        for (AggregateEventClaimsStorage storage : cededShareByEvent.values()) {
            storage.resetIncrementsAndFactors();
        }
        for (ClaimCashflowPacket grossClaim : grossClaims) {
            if (grossClaim.hasEvent()) {
                AggregateEventClaimsStorage claimStorage = getClaimsStorage(grossClaim);
                if (claimStorage == null) {
                    claimStorage = new AggregateEventClaimsStorage(grossClaim);
                    cededShareByEvent.put(grossClaim.getEvent(), claimStorage);
                }
                claimStorage.add(grossClaim);
            }
        }
        // todo(sku): check if ceding is done according to event date (first happened, first served)
        for (AggregateEventClaimsStorage storage : cededShareByEvent.values()) {
            cededFactor(BasedOnClaimProperty.ULTIMATE, storage);
            cededFactor(BasedOnClaimProperty.REPORTED, storage);
            cededFactor(BasedOnClaimProperty.PAID, storage);
            storage.printFactors();
        }
    }

    protected AggregateEventClaimsStorage getClaimsStorage(ClaimCashflowPacket grossClaim) {
        return cededShareByEvent.get(grossClaim.getEvent());
    }

    protected void cededFactor(BasedOnClaimProperty claimPropertyBase, AggregateEventClaimsStorage storage) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase);
        if (aggregateLimitValue > 0) {
            double claimPropertyCumulated = storage.getCumulated(claimPropertyBase);
            double claimPropertyIncremental = storage.getIncremental(claimPropertyBase);
            double ceded = Math.min(Math.max(-claimPropertyCumulated - attachmentPoint, 0), limit);
            double cededAfterAAD = Math.max(0, ceded - periodDeductible.get(claimPropertyBase));
            double reduceAAD = ceded - cededAfterAAD;
            periodDeductible.set(Math.max(0, periodDeductible.get(claimPropertyBase) - reduceAAD), claimPropertyBase);
            double incrementalCeded = Math.max(0, cededAfterAAD - storage.getCumulatedCeded(claimPropertyBase));
            double cededAfterAAL = aggregateLimitValue > incrementalCeded ? incrementalCeded : aggregateLimitValue;
            periodLimit.plus(-cededAfterAAL, claimPropertyBase);
            double factor = claimPropertyIncremental == 0 ? 0 : cededAfterAAL / claimPropertyIncremental;
            storage.setCededFactor(claimPropertyBase, factor);
            storage.update(claimPropertyBase, cededAfterAAL);
        }
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        if (isClaimTypeCovered(grossClaim)) {
            double cededFactorUltimate = 0;
            IClaimRoot cededBaseClaim = storage.getCededClaimRoot();
            AggregateEventClaimsStorage eventStorage = getClaimsStorage(grossClaim);
            if (cededBaseClaim == null) {
                // first time this gross claim is treated by this contract
                cededFactorUltimate = eventStorage.getCededFactorUltimate();
                cededBaseClaim = storage.lazyInitCededClaimRoot(cededFactorUltimate);
            }
            double cededFactorReported = eventStorage.getCededFactorReported();
            double cededFactorPaid = eventStorage.getCededFactorPaid();

            ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, cededFactorUltimate,
                    cededFactorReported, cededFactorPaid, false);
            add(grossClaim, cededClaim);
            return cededClaim;
        }
        return new ClaimCashflowPacket();
    }

    protected boolean isClaimTypeCovered(ClaimCashflowPacket grossClaim) {
        return grossClaim.getBaseClaim().getClaimType().equals(ClaimType.EVENT)
                || grossClaim.getBaseClaim().getClaimType().equals(ClaimType.AGGREGATED_EVENT);
    }

}
