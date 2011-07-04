package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AggregateEventClaimsStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IPremiumAllocationStrategy;

import java.util.List;

/**
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class StopLossContract extends AbstractReinsuranceContract implements INonPropReinsuranceContract {

    private double cededPremiumFixed;
    /** used to make sure that fixed premium is paid only in first period */
    private boolean isStartCoverPeriod = true;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    // todo: business object instead of parameter
    private IPremiumAllocationStrategy premiumAllocation;

    private ThresholdStore periodAttachmentPoint;
    private ThresholdStore periodLimit;

//    private double factor;
    private double totalCededPremium;

    // todo(sku): check if reset is required!
    private AggregateEventClaimsStorage aggregateClaimStorage;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param premiumAllocation
     */
    public StopLossContract(double cededPremiumFixed, double attachmentPoint, double limit,
                            IPremiumAllocationStrategy premiumAllocation) {
        this.cededPremiumFixed = cededPremiumFixed;
        this.premiumAllocation = premiumAllocation;
        periodAttachmentPoint = new ThresholdStore(attachmentPoint);
        periodLimit = new ThresholdStore(limit);
    }

    @Override
    public void initPeriod() {
        super.initPeriod();
    }

    @Override
    public void initPeriodClaims(List<ClaimCashflowPacket> grossClaims) {
        if (aggregateClaimStorage != null) {
            aggregateClaimStorage.resetIncrementsAndFactors();
        }
        for (ClaimCashflowPacket grossClaim : grossClaims) {
            if (aggregateClaimStorage == null) {
                aggregateClaimStorage = new AggregateEventClaimsStorage(grossClaim);
            }
            aggregateClaimStorage.add(grossClaim);
        }

        cededFactor(BasedOnClaimProperty.ULTIMATE, aggregateClaimStorage);
        cededFactor(BasedOnClaimProperty.REPORTED, aggregateClaimStorage);
        cededFactor(BasedOnClaimProperty.PAID, aggregateClaimStorage);
    }

    // todo(sku): try to call this function only if isStartCoverPeriod
    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares(cededClaims, grossUnderwritingInfos);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot();
        if (cededBaseClaim == null) {
            // first time this gross claim is treated by this contract
            cededBaseClaim = storage.lazyInitCededClaimRoot(aggregateClaimStorage.getCededFactorUltimate());
        }
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage,
                aggregateClaimStorage.getCededFactorUltimate(),
                aggregateClaimStorage.getCededFactorReported(),
                aggregateClaimStorage.getCededFactorPaid());
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    private void cededFactor(BasedOnClaimProperty claimPropertyBase, AggregateEventClaimsStorage storage) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase);
        if (aggregateLimitValue > 0) {
            double claimPropertyCumulated = storage.getCumulated(claimPropertyBase);
            double claimPropertyIncremental = storage.getIncremental(claimPropertyBase);
            double attachmentPoint = periodAttachmentPoint.get(claimPropertyBase);
            double limit = periodLimit.get(claimPropertyBase);
            double ceded = Math.min(Math.max(-claimPropertyIncremental - attachmentPoint, 0), limit);
            if (ceded > 0) {
                periodAttachmentPoint.set(0, claimPropertyBase);
            }
            else {
                periodAttachmentPoint.plus(claimPropertyIncremental, claimPropertyBase);
            }
            periodLimit.plus(-ceded, claimPropertyBase);
            double factor = claimPropertyIncremental == 0 ? 0 : ceded / claimPropertyIncremental;
            storage.setCededFactor(claimPropertyBase, factor);
            storage.update(claimPropertyBase, ceded);
        }
    }

    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos,
                                          double coveredByReinsurers, boolean fillNet) {
        if (isStartCoverPeriod) {
            initCededPremiumAllocation(cededClaims, grossUwInfos);
        }
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            double cededPremiumFixedShare = cededPremiumFixed * premiumAllocation.getShare(grossUnderwritingInfo);
            cededPremiumFixedShare *= coveredByReinsurers;
            double cededPremiumVariable = 0;
            cededPremiumVariable *= coveredByReinsurers;
            double cededPremium = isStartCoverPeriod ? cededPremiumFixedShare + cededPremiumVariable : cededPremiumVariable;

            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.deriveCededPacketForNonPropContract(
                    grossUnderwritingInfo, contractMarker, -cededPremium, isStartCoverPeriod ? -cededPremiumFixedShare : 0,
                    -cededPremiumVariable);
            cededUwInfos.add(cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
            if (fillNet && isStartCoverPeriod) {
                netUnderwritingInfos.add(grossUnderwritingInfo.getNet(cededUnderwritingInfo, false));
            }
        }
        isStartCoverPeriod = false;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("attachment point: ");
        buffer.append(periodAttachmentPoint);
        buffer.append(", limit: ");
        buffer.append(periodLimit);
        return buffer.toString();
    }

}
