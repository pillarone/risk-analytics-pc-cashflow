package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;

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
    private double lossCarriedForward = 0d;


    public ProfitCommission(double profitCommissionRatio, double commissionRatio, double costRatio,
                            boolean lossCarriedForwardEnabled, double initialLossCarriedForward,
                            BasedOnClaimProperty useClaims) {
        this.profitCommissionRatio = profitCommissionRatio;
        this.commissionRatio = commissionRatio;
        this.costRatio = costRatio;
        this.lossCarriedForwardEnabled = lossCarriedForwardEnabled;
        this.initialLossCarriedForward = initialLossCarriedForward;
        super.useClaims = useClaims;
    }

    public void calculateCommission(List<ClaimCashflowPacket> claims, List<CededUnderwritingInfoPacket> underwritingInfos,
                                    boolean isFirstPeriod, boolean isAdditive) {
        if (lossCarriedForwardEnabled && isFirstPeriod) {
            lossCarriedForward = initialLossCarriedForward;
        }
        double summedClaims = sumClaims(claims);
        double totalPremiumPaid = sumPremiumPaid(underwritingInfos);
        double fixedCommission = commissionRatio * -totalPremiumPaid; // calculate 'prior' fixed commission
        double currentProfit = -totalPremiumPaid * (1d - costRatio) - fixedCommission - summedClaims;
        double commissionableProfit = Math.max(0d, currentProfit - lossCarriedForward);
        double variableCommission = profitCommissionRatio * commissionableProfit;
        double totalCommission = fixedCommission + variableCommission;
        lossCarriedForward = lossCarriedForwardEnabled ? Math.max(0d, lossCarriedForward - currentProfit) : 0d;

        double commissionRate = totalCommission / -totalPremiumPaid ;
        double fixedCommissionRate = fixedCommission / -totalPremiumPaid;
        double variableCommissionRate = variableCommission / -totalPremiumPaid;
        adjustCommissionProperties(underwritingInfos, isAdditive, commissionRate, fixedCommissionRate, variableCommissionRate);
    }
}
