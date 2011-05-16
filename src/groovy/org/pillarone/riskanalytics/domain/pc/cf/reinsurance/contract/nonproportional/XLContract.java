package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IPremiumAllocationStrategy;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class XLContract extends AbstractReinsuranceContract implements INonPropReinsuranceContract {

    private double cededPremiumFixed;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    // todo: business object instead of parameter
    private IPremiumAllocationStrategy premiumAllocation;
    private ReinstatementsAndLimitStore reinstatements;
    private double attachmentPoint;
    private double limit;


    private ThresholdStore periodDeductible;
    private ThresholdStore periodLimit;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param reinstatementPremiumFactors
     * @param premiumAllocation
     */
    public XLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                      double aggregateLimit, List<Double> reinstatementPremiumFactors, IPremiumAllocationStrategy premiumAllocation) {
        this.cededPremiumFixed = cededPremiumFixed;
        this.attachmentPoint = attachmentPoint;
        this.limit = limit;
        this.premiumAllocation = premiumAllocation;
        periodDeductible = new ThresholdStore(aggregateDeductible);
        periodLimit = new ThresholdStore(aggregateLimit);
        reinstatements = new ReinstatementsAndLimitStore(periodLimit, limit, reinstatementPremiumFactors);
    }

    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares(cededClaims, grossUnderwritingInfos);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot();
        if (cededBaseClaim == null) {
            // first time this gross claim is treated by the contract
            double cededUltimate = cumulatedCededValue(grossClaim.ultimate(), BasedOnClaimProperty.ULTIMATE,
                    storage, periodDeductible, periodLimit);
            periodLimit.plus(-cededUltimate, BasedOnClaimProperty.ULTIMATE);
            double cededFactorUltimate = grossClaim.ultimate() == 0 ? 0 : cededUltimate / grossClaim.ultimate();
            cededBaseClaim = storage.lazyInitCededClaimRoot(cededFactorUltimate, contractMarker);
        }

        double cededReportedCumulated = cumulatedCededValue(grossClaim.getReportedCumulated(),
                BasedOnClaimProperty.REPORTED, storage, periodDeductible, periodLimit);
        double cededReportedIncremental = storage.updateReported(cededReportedCumulated);
        double cededFactorReported = grossClaim.getReportedIncremental() == 0 ? 0 : cededReportedIncremental / grossClaim.getReportedIncremental();
        periodLimit.plus(-cededReportedIncremental, BasedOnClaimProperty.REPORTED);

        double cededPaidCumulated = cumulatedCededValue(grossClaim.getPaidCumulated(), BasedOnClaimProperty.PAID, storage,
                periodDeductible, periodLimit);
        double cededPaidIncremental = storage.updatePaid(cededPaidCumulated);
        double cededFactorPaid = grossClaim.getPaidIncremental() == 0 ? 0 : cededPaidIncremental / grossClaim.getPaidIncremental();
        periodLimit.plus(-cededPaidIncremental, BasedOnClaimProperty.PAID);

        ClaimCashflowPacket cededClaim = grossClaim.withBaseClaimAndShare(cededBaseClaim, cededFactorReported,
                cededFactorPaid, grossClaim.ultimate() != 0);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    private double cumulatedCededValue(double claimProperty, BasedOnClaimProperty claimPropertyBase, ClaimStorage storage,
                                       ThresholdStore aggregateDeductible, ThresholdStore aggregateLimit) {
        double aggregateLimitValue = aggregateLimit.get(claimPropertyBase);
        if (aggregateLimitValue > 0) {
            double ceded = Math.min(Math.max(-claimProperty - attachmentPoint, 0), limit);
            double cededAfterAAD = Math.max(0, ceded - aggregateDeductible.get(claimPropertyBase));
            double incrementalCeded = ceded - storage.getCumulatedCeded(claimPropertyBase);
            aggregateDeductible.set(Math.max(0, aggregateDeductible.get(claimPropertyBase) - incrementalCeded), claimPropertyBase);
            double cededAfterAAL = aggregateLimitValue > cededAfterAAD ? cededAfterAAD : aggregateLimitValue;
            return cededAfterAAL;
        }
        else {
            return 0;
        }
    }

    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos, List<UnderwritingInfoPacket> netUnderwritingInfos, boolean fillNet) {
        initCededPremiumAllocation(cededClaims, grossUwInfos);
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            double cededPremiumFixedShare = cededPremiumFixed * premiumAllocation.getShare(grossUnderwritingInfo);
            double cededPremiumVariable = cededPremiumFixedShare * reinstatements.calculateReinstatementPremiumFactor();
            double cededPremium = cededPremiumFixedShare + cededPremiumVariable;

            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.deriveCededPacketForNonPropContract(
                    grossUnderwritingInfo, contractMarker, -cededPremium, -cededPremiumFixed, -cededPremiumVariable);
            cededUwInfos.add(cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("attachment point: ");
        buffer.append(attachmentPoint);
        buffer.append(", limit: ");
        buffer.append(limit);
        buffer.append(", aggregate deductible: ");
        buffer.append(", aggregate limit: ");
        return buffer.toString();
    }

}
