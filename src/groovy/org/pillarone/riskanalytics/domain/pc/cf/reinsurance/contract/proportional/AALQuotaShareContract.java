package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.AalLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AALQuotaShareContract extends AALAADQuotaShareContract {

//    private ThresholdStore periodLimit;

    public AALQuotaShareContract(double quotaShare, ICommission commission, AalLimitStrategy limit) {
        super(quotaShare, commission, limit);
//        periodLimit = new ThresholdStore(limit.getAAL());
    }

//    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
//        double quotaShareUltimate = 0;
//        if (!storage.hasReferenceCeded()) {
//            quotaShareUltimate = adjustedQuote(grossClaim.developedUltimate(), grossClaim.nominalUltimate(),
//                    BasedOnClaimProperty.ULTIMATE, storage);
//            storage.lazyInitCededClaimRoot(quotaShareUltimate);
//        }
//
//        double quotaShareReported = adjustedQuote(grossClaim.getReportedCumulatedIndexed(),
//                grossClaim.getReportedIncrementalIndexed(), BasedOnClaimProperty.REPORTED, storage);
//        double quotaSharePaid = adjustedQuote(grossClaim.getPaidCumulatedIndexed(),
//                grossClaim.getPaidIncrementalIndexed(), BasedOnClaimProperty.PAID, storage);
//        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
//                quotaShareReported, quotaSharePaid, true);
//        add(grossClaim, cededClaim);
//        return cededClaim;
//    }
//
//    protected double adjustedQuote(double claimPropertyCumulated, double claimPropertyIncremental,
//                                   BasedOnClaimProperty claimPropertyBase, ClaimStorage storage) {
//        double limit = periodLimit.get(claimPropertyBase);
//        double ceded = -claimPropertyCumulated * quotaShare;
//        double incrementalCeded = ceded - storage.getCumulatedCeded(claimPropertyBase);
//        double cededAfterAAL = limit > incrementalCeded ? incrementalCeded : limit;
//        periodLimit.plus(-cededAfterAAL, claimPropertyBase);
//        return claimPropertyIncremental == 0 ? 0 : cededAfterAAL / claimPropertyIncremental;
//    }
//
//    @Override
//    public String toString() {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(super.toString());
//        buffer.append(", AAL ultimate: ");
//        buffer.append(periodLimit.get(BasedOnClaimProperty.ULTIMATE));
//        buffer.append(", AAL reported: ");
//        buffer.append(periodLimit.get(BasedOnClaimProperty.REPORTED));
//        buffer.append(", AAL paid: ");
//        buffer.append(periodLimit.get(BasedOnClaimProperty.PAID));
//        return buffer.toString();
//    }
}
