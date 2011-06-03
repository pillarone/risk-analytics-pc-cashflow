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
 * <ul>
 *     <li>Reinstatements are calculated on a paid base and cover is refilled permanently.</li>
 *     <li>order of application: (1) attachment point, limit, (2) aggregate deductible, (3) aggregate limit</li>
 *     <li>aggregate deductibles are re-init for every period</li>
 * </ul>
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class XLContract extends AbstractReinsuranceContract implements INonPropReinsuranceContract {

    private double cededPremiumFixed;
    /** used to make sure that fixed premium is paid only in first period */
    private boolean isStartCoverPeriod = true;

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

    /**
     * reintialization of the deductibles is required as calculations are base on cumulated values in XL contracts
     */
    @Override
    public void initPeriod() {
        super.initPeriod();
        periodDeductible.init();
    }

    // todo(sku): try to call this function only if isStartCoverPeriod
    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares(cededClaims, grossUnderwritingInfos);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage) {
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot();
        if (cededBaseClaim == null) {
            // first time this gross claim is treated by this contract
            double cededFactorUltimate = cededFactor(grossClaim.ultimate(), grossClaim.ultimate(),
                                                     BasedOnClaimProperty.ULTIMATE, storage);
            cededBaseClaim = storage.lazyInitCededClaimRoot(cededFactorUltimate, contractMarker);
        }

        double cededFactorReported = cededFactor(grossClaim.getReportedCumulated(), grossClaim.getReportedIncremental(),
                BasedOnClaimProperty.REPORTED, storage);

        double cededFactorPaid = cededFactor(grossClaim.getPaidCumulated(), grossClaim.getPaidIncremental(),
                BasedOnClaimProperty.PAID, storage);

        ClaimCashflowPacket cededClaim = grossClaim.withBaseClaimAndShare(cededBaseClaim, cededFactorReported,
                cededFactorPaid, grossClaim.ultimate() != 0);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    private double cededFactor(double claimPropertyCumulated, double claimPropertyIncremental,
                               BasedOnClaimProperty claimPropertyBase, ClaimStorage storage) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase);
        if (aggregateLimitValue > 0) {
            double ceded = Math.min(Math.max(-claimPropertyCumulated - attachmentPoint, 0), limit);
            double cededAfterAAD = Math.max(0, ceded - periodDeductible.get(claimPropertyBase));
            double reduceAAD = ceded - cededAfterAAD;
            periodDeductible.set(Math.max(0, periodDeductible.get(claimPropertyBase) - reduceAAD), claimPropertyBase);
            double incrementalCeded = Math.max(0, cededAfterAAD - storage.getCumulatedCeded(claimPropertyBase));
            double cededAfterAAL = aggregateLimitValue > incrementalCeded ? incrementalCeded : aggregateLimitValue;
            storage.update(cededAfterAAL, claimPropertyBase);
            periodLimit.plus(-cededAfterAAL, claimPropertyBase);
            return claimPropertyIncremental == 0 ? 0 : cededAfterAAL / claimPropertyIncremental;
        }
        else {
            storage.update(0, claimPropertyBase);
            return 0;
        }
    }

    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos, boolean fillNet) {
        if (isStartCoverPeriod) {
            initCededPremiumAllocation(cededClaims, grossUwInfos);
        }
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            double cededPremiumFixedShare = cededPremiumFixed * premiumAllocation.getShare(grossUnderwritingInfo);
            double cededPremiumVariable = cededPremiumFixedShare * reinstatements.calculateReinstatementPremiumFactor();
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
        buffer.append(attachmentPoint);
        buffer.append(", limit: ");
        buffer.append(limit);
        buffer.append(", aggregate deductible: ");
        buffer.append(", aggregate limit: ");
        return buffer.toString();
    }

}
