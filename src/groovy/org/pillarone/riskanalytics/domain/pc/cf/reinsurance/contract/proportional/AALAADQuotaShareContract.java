package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.ILimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AALAADQuotaShareContract extends QuotaShareContract {

    private ThresholdStore periodLimit;
    private ThresholdStore periodDeductible;
    private AADReduction aadReduction;



    public AALAADQuotaShareContract(double quotaShare, ICommission commission, ILimitStrategy limit) {
        super(quotaShare, commission);
        periodLimit = new ThresholdStore(limit.getAAL());
        periodDeductible = new ThresholdStore(limit.getAAD());
        aadReduction = new AADReduction();
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        double quotaShareUltimate = 0;
        if (!storage.hasReferenceCeded()) {
            quotaShareUltimate = adjustedQuote(grossClaim.developedUltimate(), grossClaim.nominalUltimate(),
                    BasedOnClaimProperty.ULTIMATE, storage);
            storage.lazyInitCededClaimRoot(quotaShareUltimate);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedCumulatedIndexed(),
                grossClaim.getReportedIncrementalIndexed(), BasedOnClaimProperty.REPORTED, storage);
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidCumulatedIndexed(),
                grossClaim.getPaidIncrementalIndexed(), BasedOnClaimProperty.PAID, storage);
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                quotaShareReported, quotaSharePaid, true);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    private double adjustedQuote(double claimPropertyCumulated, double claimPropertyIncremental,
                                 BasedOnClaimProperty claimPropertyBase, ClaimStorage storage) {
        double grossAfterAAD = Math.max(0, -claimPropertyCumulated - periodDeductible.get(claimPropertyBase));
        double reduceAAD = -claimPropertyCumulated - grossAfterAAD;
        periodDeductible.set(Math.max(0, periodDeductible.get(claimPropertyBase) - reduceAAD), claimPropertyBase);
        Double previousPeriodAADReduction = aadReduction.previousAADReduction(storage.getCededClaimRoot(), claimPropertyBase);
        double ceded = (grossAfterAAD - previousPeriodAADReduction) * quotaShare;
        if (reduceAAD > 0) {
            aadReduction.increaseAADReduction(storage.getCededClaimRoot(), claimPropertyBase, reduceAAD);
        }
        double incrementalCeded = ceded - storage.getCumulatedCeded(claimPropertyBase);
        double cededAfterAAL = periodLimit.get(claimPropertyBase) > incrementalCeded ? incrementalCeded : periodLimit.get(claimPropertyBase);
        periodLimit.plus(-cededAfterAAL, claimPropertyBase);
        return claimPropertyIncremental == 0 ? 0 : cededAfterAAL / claimPropertyIncremental;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append(", AAL ultimate: ");
        builder.append(periodLimit.get(BasedOnClaimProperty.ULTIMATE));
        builder.append(", AAL reported: ");
        builder.append(periodLimit.get(BasedOnClaimProperty.REPORTED));
        builder.append(", AAL paid: ");
        builder.append(periodLimit.get(BasedOnClaimProperty.PAID));
        builder.append(", AAD ultimate: ");
        builder.append(periodDeductible.get(BasedOnClaimProperty.ULTIMATE));
        builder.append(", AAD reported: ");
        builder.append(periodDeductible.get(BasedOnClaimProperty.REPORTED));
        builder.append(", AAD paid: ");
        builder.append(periodDeductible.get(BasedOnClaimProperty.PAID));
        return builder.toString();
    }

    private class AADReduction {
//        private Map<IClaimRoot, Double> ultimate;
        private Map<IClaimRoot, Double> reported;
        private Map<IClaimRoot, Double> paid;

        public AADReduction() {
            init();
        }

        public void init() {
//            ultimate = new HashMap<IClaimRoot, Double>();
            reported = new HashMap<IClaimRoot, Double>();
            paid = new HashMap<IClaimRoot, Double>();
        }

        public void increaseAADReduction(IClaimRoot keyClaim, BasedOnClaimProperty base, double incrementalReduction) {
            switch (base) {
                case ULTIMATE: {
//                    Double previousReduction = ultimate.get(keyClaim);
//                    ultimate.put(keyClaim, incrementalReduction + ((previousReduction != null) ? previousReduction : 0));
                    break;
                }
                case REPORTED: {
                    Double previousReduction = reported.get(keyClaim);
                    reported.put(keyClaim, incrementalReduction + ((previousReduction != null) ? previousReduction : 0));
                    break;
                }
                case PAID: {
                    Double previousReduction = paid.get(keyClaim);
                    paid.put(keyClaim, incrementalReduction + ((previousReduction != null) ? previousReduction : 0));
                    break;
                }
                default: {
                    throw new NotImplementedException(base.toString());
                }
            }
        }

        public double previousAADReduction(IClaimRoot keyClaim, BasedOnClaimProperty base) {
            Double previousReduction = null;
            switch (base) {
                case ULTIMATE: {
//                    previousReduction = ultimate.get(keyClaim);
                    break;
                }
                case REPORTED: {
                    previousReduction = reported.get(keyClaim);
                    break;
                }
                case PAID: {
                    previousReduction = paid.get(keyClaim);
                    break;
                }
                default: {
                    throw new NotImplementedException(base.toString());
                }
            }
            return ((previousReduction == null) ? 0d : previousReduction);
        }
    }
}
