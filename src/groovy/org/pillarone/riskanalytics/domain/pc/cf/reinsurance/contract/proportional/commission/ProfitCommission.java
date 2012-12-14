package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;

import java.util.List;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ProfitCommission extends AbstractCommission {

    private double profitCommissionRatio = 0d;
    private double commissionRatio = 0d; // for "prior" fixed commission
    private double costRatio = 0d;
    private boolean lossCarriedForwardEnabled = true;
    private double initialLossCarriedForward = 0d;
    /**
     * not a parameter but updated during calculateCommission() to avoid side effect for the parameter variable
     */
    private DoubleValue lossCarriedForward;
    private double previousCumulatedFixCommission = 0d;
    private double previousCumulatedProfitCommission = 0d;
    private double totalPremiumCeded = 0;
    private double previousReinsuranceResult = 0;


    public ProfitCommission(double profitCommissionRatio, double commissionRatio, double costRatio,
                            boolean lossCarriedForwardEnabled, double initialLossCarriedForward, DoubleValue lossCarriedForward,
                            BasedOnClaimProperty useClaims) {
        this.profitCommissionRatio = profitCommissionRatio;
        this.commissionRatio = commissionRatio;
        this.costRatio = costRatio;
        this.lossCarriedForwardEnabled = lossCarriedForwardEnabled;
        this.initialLossCarriedForward = initialLossCarriedForward;
        this.lossCarriedForward = lossCarriedForward;
        super.useClaims = useClaims;
    }

    public void calculateCommission(List<ClaimCashflowPacket> cededClaims,
                                    List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                    boolean isAdditive) {
        double summedClaimsCeded = sumCumulatedClaims(cededClaims);
        totalPremiumCeded += sumPremium(cededUnderwritingInfos);

        double fixCommission = -totalPremiumCeded * commissionRatio;
        double incrementalFixCommission = fixCommission - previousCumulatedFixCommission;
        previousCumulatedFixCommission = fixCommission;

        double reinsuranceResult = -totalPremiumCeded * (1 + summedClaimsCeded / totalPremiumCeded - commissionRatio - costRatio);
        double reinsuranceResultAfterLCF = reinsuranceResult - previousReinsuranceResult + lossCarriedForward.value;
        if (lossCarriedForwardEnabled) {
            if (reinsuranceResultAfterLCF != reinsuranceResult && reinsuranceResult > 0) {
                lossCarriedForward.plus(reinsuranceResultAfterLCF - lossCarriedForward.value);
            }
        }
        previousReinsuranceResult = reinsuranceResult;
        double cumulatedProfitCommission = profitCommissionRatio * Math.max(0, reinsuranceResultAfterLCF);
        double incrementalProfitCommission = cumulatedProfitCommission - previousCumulatedProfitCommission;
        previousCumulatedProfitCommission = cumulatedProfitCommission;

        if (incrementalFixCommission + incrementalProfitCommission > 0) {
            adjustCommissionProperties(cededUnderwritingInfos, isAdditive, incrementalFixCommission + incrementalProfitCommission,
                    incrementalFixCommission, incrementalProfitCommission);
        }
    }
}
