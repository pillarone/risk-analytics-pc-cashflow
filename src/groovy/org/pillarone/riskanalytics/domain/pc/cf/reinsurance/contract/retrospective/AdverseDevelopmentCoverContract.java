package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AggregateEventClaimsStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumSharesPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.INonPropReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;

import java.util.List;

/**
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AdverseDevelopmentCoverContract extends AbstractReinsuranceContract implements INonPropReinsuranceContract {

    private double cededPremiumFixed;
    /** used to make sure that fixed premium is paid only in first period */
    private boolean isStartCoverPeriod = true;

    private IRIPremiumSplitStrategy riPremiumSplit = new PremiumSharesPremiumSplitStrategy();

    private ThresholdStore periodAttachmentPoint;
    private ThresholdStore periodLimit;

    // todo(sku): check if reset is required!
    private AggregateEventClaimsStorage aggregateClaimStorage;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     */
    public AdverseDevelopmentCoverContract(double cededPremiumFixed, double attachmentPoint, double limit) {
        this.cededPremiumFixed = cededPremiumFixed;
        periodAttachmentPoint = new ThresholdStore(attachmentPoint);
        periodLimit = new ThresholdStore(limit);
    }

    @Override
    public void initPeriod(int period, List<FactorsPacket> inFactors) {
        super.initPeriod(period, inFactors);
    }

    @Override
    public void initBasedOnAggregateCalculations(List<ClaimCashflowPacket> grossClaims, List<UnderwritingInfoPacket> grossUnderwritingInfo) {
        if (aggregateClaimStorage != null) {
            aggregateClaimStorage.resetIncrementsAndFactors();
        }
        for (ClaimCashflowPacket grossClaim : grossClaims) {
            if (aggregateClaimStorage == null) {
                aggregateClaimStorage = new AggregateEventClaimsStorage();
            }
            aggregateClaimStorage.add(grossClaim);
        }

        cededFactor(BasedOnClaimProperty.ULTIMATE, aggregateClaimStorage);
        cededFactor(BasedOnClaimProperty.REPORTED, aggregateClaimStorage);
        cededFactor(BasedOnClaimProperty.PAID, aggregateClaimStorage);
    }



    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot();
        if (cededBaseClaim == null) {
            // first time this gross claim is treated by this contract
            cededBaseClaim = storage.lazyInitCededClaimRoot(aggregateClaimStorage.getCededFactorUltimate());
        }
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage,
                aggregateClaimStorage.getCededFactorUltimate(),
                aggregateClaimStorage.getCededFactorReported(),
                aggregateClaimStorage.getCededFactorPaid(), false);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    private void cededFactor(BasedOnClaimProperty claimPropertyBase, AggregateEventClaimsStorage storage) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase);
        if (aggregateLimitValue > 0) {
            double claimPropertyIncremental;
            if (claimPropertyBase.equals(BasedOnClaimProperty.ULTIMATE)) {
                claimPropertyIncremental = storage.getCumulated(claimPropertyBase);
            }
            else {
                claimPropertyIncremental = storage.getIncremental(claimPropertyBase);
            }
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
            double cededPremiumFixedShare = cededPremiumFixed * riPremiumSplit.getShare(grossUnderwritingInfo);
            cededPremiumFixedShare *= coveredByReinsurers;
            double cededPremiumVariable = 0;
            cededPremiumVariable *= coveredByReinsurers;
            double cededPremium = isStartCoverPeriod ? cededPremiumFixedShare + cededPremiumVariable : cededPremiumVariable;

            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.deriveCededPacketForNonPropContract(
                    grossUnderwritingInfo, contractMarker, -cededPremium, isStartCoverPeriod ? -cededPremiumFixedShare : 0,
                    -cededPremiumVariable);
            UnderwritingInfoUtils.applyMarkers(grossUnderwritingInfo, cededUnderwritingInfo);
            cededUwInfos.add(cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
            if (fillNet && isStartCoverPeriod) {
                netUnderwritingInfos.add(grossUnderwritingInfo.getNet(cededUnderwritingInfo, false));
            }
        }
        isStartCoverPeriod = false;
    }

    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        riPremiumSplit.initSegmentShares(cededClaims, grossUnderwritingInfos);
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
