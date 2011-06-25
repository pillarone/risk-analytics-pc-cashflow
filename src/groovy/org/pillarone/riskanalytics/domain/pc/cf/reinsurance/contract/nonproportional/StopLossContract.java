package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IPremiumAllocationStrategy;

import java.util.List;

/**
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class StopLossContract extends AbstractReinsuranceContract implements INonPropReinsuranceContract {

    private double cededPremiumFixed;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    // todo: business object instead of parameter
    private IPremiumAllocationStrategy premiumAllocation;
    private double attachmentPoint;
    private double limit;

    private ThresholdStore periodAttachmentPoint;
    private ThresholdStore periodLimit;

    private double factor;
    private double totalCededPremium;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param premiumAllocation
     */
    public StopLossContract(double cededPremiumFixed, double attachmentPoint, double limit, IPremiumAllocationStrategy premiumAllocation) {
        this.cededPremiumFixed = cededPremiumFixed;
        this.attachmentPoint = attachmentPoint;
        this.limit = limit;
        this.premiumAllocation = premiumAllocation;
        periodAttachmentPoint = new ThresholdStore(attachmentPoint);
        periodLimit = new ThresholdStore(limit);
    }

    @Override
    public void initPeriod() {
        super.initPeriod();
    }

    // todo(sku): try to call this function only if isStartCoverPeriod
    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares(cededClaims, grossUnderwritingInfos);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        return new ClaimCashflowPacket();
    }

    private double cededFactor(double claimPropertyCumulated, double claimPropertyIncremental,
                               BasedOnClaimProperty claimPropertyBase, ClaimStorage storage) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase);
        if (aggregateLimitValue > 0) {
            double ceded = Math.min(Math.max(-claimPropertyCumulated - attachmentPoint, 0), limit);
            double cededAfterAAD = Math.max(0, ceded - periodAttachmentPoint.get(claimPropertyBase));
            double reduceAAD = ceded - cededAfterAAD;
            periodAttachmentPoint.set(Math.max(0, periodAttachmentPoint.get(claimPropertyBase) - reduceAAD), claimPropertyBase);
            double incrementalCeded = Math.max(0, cededAfterAAD - storage.getCumulatedCeded(claimPropertyBase));
            double cededAfterAAL = aggregateLimitValue > incrementalCeded ? incrementalCeded : aggregateLimitValue;
            periodLimit.plus(-cededAfterAAL, claimPropertyBase);
            return claimPropertyIncremental == 0 ? 0 : cededAfterAAL / claimPropertyIncremental;
        }
        else {
            return 0;
        }
    }

    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos,
                                          double coveredByReinsurers, boolean fillNet) {

    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("attachment point: ");
        buffer.append(attachmentPoint);
        buffer.append(", limit: ");
        buffer.append(limit);
        return buffer.toString();
    }

}
