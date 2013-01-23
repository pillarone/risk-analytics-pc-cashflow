package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod;

import java.util.List;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ProfitCommission extends AbstractCommission {

    private double profitCommissionRatio = 0d;
    private double commissionRatio = 0d; // for "prior" fixed commission
    private double costRatio = 0d;
    private boolean lossCarriedForwardEnabled = true;
    /**
     * not a parameter but updated during calculateCommission() to avoid side effect for the parameter variable
     */
    private DoubleValuePerPeriod lossCarriedForward;
    private double previousCumulatedFixCommission = 0d;
    private double previousCumulatedProfitCommission = 0d;
    private double totalPremiumCeded = 0;
    private double previousReinsuranceResult = 0;


    public ProfitCommission(double profitCommissionRatio, double commissionRatio, double costRatio,
                            boolean lossCarriedForwardEnabled, DoubleValuePerPeriod lossCarriedForward,
                            BasedOnClaimProperty useClaims) {
        this.profitCommissionRatio = profitCommissionRatio;
        this.commissionRatio = commissionRatio;
        this.costRatio = costRatio;
        this.lossCarriedForwardEnabled = lossCarriedForwardEnabled;
        this.lossCarriedForward = lossCarriedForward;
        super.useClaims = useClaims;
    }

    public void calculateCommission(List<ClaimCashflowPacket> cededClaims,
                                    List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                    boolean isAdditive) {
        double summedClaimsCeded = sumCumulatedClaims(cededClaims);
        totalPremiumCeded += sumPremium(cededUnderwritingInfos);
        if (totalPremiumCeded == 0) return;

        double fixCommission = -totalPremiumCeded * commissionRatio;
        double incrementalFixCommission = fixCommission - previousCumulatedFixCommission;
        previousCumulatedFixCommission = fixCommission;

        double reinsuranceResult = -totalPremiumCeded * (1 + summedClaimsCeded / totalPremiumCeded - commissionRatio - costRatio);
        if (lossCarriedForwardEnabled) {
            DateTime dateOfCurrentPeriod = null;
            if (cededClaims.size() > 0) {
                dateOfCurrentPeriod = cededClaims.get(0).getUpdateDate();
            }
            else if (cededUnderwritingInfos.size() > 0) {
                dateOfCurrentPeriod = cededUnderwritingInfos.get(0).getDate();
            }
            if (dateOfCurrentPeriod != null) {
                double reinsuranceResultAfterLCF = reinsuranceResult + lossCarriedForward.getValue(dateOfCurrentPeriod);
                if (reinsuranceResultAfterLCF != reinsuranceResult && reinsuranceResult > 0) {
                    Double usedLossCarriedForward = reinsuranceResult - reinsuranceResultAfterLCF;
                    lossCarriedForward.plus(usedLossCarriedForward);
                }
                reinsuranceResult = reinsuranceResultAfterLCF;
            }
        }
        previousReinsuranceResult = reinsuranceResult;
        double cumulatedProfitCommission = profitCommissionRatio * Math.max(0, reinsuranceResult);
        double incrementalProfitCommission = cumulatedProfitCommission - previousCumulatedProfitCommission;
        previousCumulatedProfitCommission = cumulatedProfitCommission;

        if (incrementalFixCommission != 0 || incrementalProfitCommission != 0) {
            adjustCommissionProperties(cededUnderwritingInfos, isAdditive, incrementalFixCommission + incrementalProfitCommission,
                    incrementalFixCommission, incrementalProfitCommission);
        }
    }
}
