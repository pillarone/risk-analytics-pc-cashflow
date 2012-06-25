package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;

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
    private IRIPremiumSplitStrategy riPremiumSplit;
    private ReinstatementsAndLimitStore reinstatements;
    protected double attachmentPoint;
    protected double limit;

    protected ThresholdStore periodDeductible;
    protected ThresholdStore periodLimit;

    protected IStabilizationStrategy stabilization;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param stabilization
     * @param reinstatementPremiumFactors
     * @param riPremiumSplit
     */
    public XLContract(double cededPremiumFixed, double attachmentPoint, double limit, double aggregateDeductible,
                      double aggregateLimit, IStabilizationStrategy stabilization,
                      List<Double> reinstatementPremiumFactors, IRIPremiumSplitStrategy riPremiumSplit) {
        this.cededPremiumFixed = cededPremiumFixed;
        this.attachmentPoint = attachmentPoint;
        this.limit = limit;
        this.riPremiumSplit = riPremiumSplit;
        periodDeductible = new ThresholdStore(aggregateDeductible);
        this.stabilization = stabilization;
        periodLimit = new ThresholdStore(aggregateLimit);
        reinstatements = new ReinstatementsAndLimitStore(periodLimit, limit, reinstatementPremiumFactors);
    }

    /**
     * reinitialization of the deductibles is required as calculations are base on cumulated values in XL contracts
     */
    @Override
    public void initPeriod(int period, List<FactorsPacket> inFactors) {
        super.initPeriod(period, inFactors);
        periodDeductible.init();
        stabilization.mergeFactors(inFactors);
    }

    // todo(sku): try to call this function only if isStartCoverPeriod
    public void initCededPremiumAllocation(List<ClaimCashflowPacket> cededClaims, List<UnderwritingInfoPacket> grossUnderwritingInfos) {
        riPremiumSplit.initSegmentShares(cededClaims, grossUnderwritingInfos);
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        double cededFactorUltimate = 0;
        IClaimRoot cededBaseClaim = storage.getCededClaimRoot();
        double stabilizationFactor = storage.stabilizationFactor(grossClaim, stabilization, periodCounter);
        if (cededBaseClaim == null) {
            // first time this gross claim is treated by this contract
            cededFactorUltimate = cededFactor(grossClaim.ultimate(), grossClaim.ultimate(),
                                                     BasedOnClaimProperty.ULTIMATE, storage, stabilizationFactor);
//            cededBaseClaim = storage.lazyInitCededClaimRoot(cededFactorUltimate);
        }
        double cededFactorReported = cededFactor(grossClaim.getReportedCumulatedIndexed(), grossClaim.getReportedIncrementalIndexed(),
                BasedOnClaimProperty.REPORTED, storage, stabilizationFactor);

        double cededFactorPaid = cededFactor(grossClaim.getPaidCumulatedIndexed(), grossClaim.getPaidIncrementalIndexed(),
                BasedOnClaimProperty.PAID, storage, stabilizationFactor);

        ClaimCashflowPacket cededClaim;
        cededClaim = cededClaimWithAdjustedReported(grossClaim, storage, cededFactorUltimate, stabilizationFactor,
                cededFactorReported, cededFactorPaid);

        add(grossClaim, cededClaim);
        return cededClaim;
    }

    protected ClaimCashflowPacket cededClaimWithAdjustedReported(ClaimCashflowPacket grossClaim, ClaimStorage storage,
                                                               double cededFactorUltimate, double stabilizationFactor,
                                                               double cededFactorReported, double cededFactorPaid) {
        ClaimCashflowPacket cededClaim;// PMO-1856: positive stabilization factor after reporting pattern end
        if (stabilizationFactor != 1 && cededFactorReported == 0 && grossClaim.getReportedIncrementalIndexed() == 0) {
            double cededReportedValue = cededValue(grossClaim.getReportedCumulatedIndexed(),
                BasedOnClaimProperty.REPORTED, storage, stabilizationFactor);
            cededClaim = ClaimUtils.getCededClaimReportedAbsolute(grossClaim, storage, cededFactorUltimate,
                    cededReportedValue, cededFactorPaid, false);
        }
        else {
            cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, cededFactorUltimate,
                cededFactorReported, cededFactorPaid, false);
        }
        return cededClaim;
    }

    private double cededFactor(double claimPropertyCumulated, double claimPropertyIncremental,
                               BasedOnClaimProperty claimPropertyBase, ClaimStorage storage,
                               double stabilizationFactor) {

        double cededAfterAAL = cededValue(claimPropertyCumulated, claimPropertyBase, storage, stabilizationFactor);
        return claimPropertyIncremental == 0 ? 0 : cededAfterAAL / claimPropertyIncremental;
    }

    protected double cededValue(double claimPropertyCumulated, BasedOnClaimProperty claimPropertyBase, ClaimStorage storage,
                               double stabilizationFactor) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase, stabilizationFactor);
        double ceded = Math.min(Math.max(-claimPropertyCumulated - attachmentPoint * stabilizationFactor, 0), limit * stabilizationFactor);
        double cededAfterAAD = Math.max(0, ceded - periodDeductible.get(claimPropertyBase, stabilizationFactor));
        double reduceAAD = ceded - cededAfterAAD;
        periodDeductible.set(Math.max(0, periodDeductible.get(claimPropertyBase) - reduceAAD), claimPropertyBase);
        double incrementalCeded = cededAfterAAD - storage.getCumulatedCeded(claimPropertyBase);
        double cededAfterAAL = aggregateLimitValue > incrementalCeded ? incrementalCeded : aggregateLimitValue;
        periodLimit.plus(-cededAfterAAL, claimPropertyBase);
        return cededAfterAAL;
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
            double cededPremiumVariable = cededPremiumFixedShare * reinstatements.calculateReinstatementPremiumFactor();
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
