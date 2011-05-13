package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionBasedOnClaims;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractCommission implements ICommission {

    protected CommissionBasedOnClaims useClaims;

    protected double sumPremiumPaid(List<CededUnderwritingInfoPacket> underwritingInfos) {
        double totalPremium = 0;
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            totalPremium += underwritingInfo.getPremiumPaid();
        }
        return totalPremium;
    }

    protected double sumClaims(List<ClaimCashflowPacket> claims) {
        double totalClaims = 0;
        if (useClaims.equals(CommissionBasedOnClaims.ULTIMATE)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.ultimate();
            }
        }
        else if (useClaims.equals(CommissionBasedOnClaims.PAID)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.getPaidIncremental();
            }
        }
        else if (useClaims.equals(CommissionBasedOnClaims.REPORTED)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.getReportedIncremental();
            }
        }
        return totalClaims;
    }

    protected void adjustCommissionProperties(List<CededUnderwritingInfoPacket> underwritingInfos, boolean isAdditive,
                         double commissionFactor, double fixedCommissionFactor, double variableCommissionFactor) {

        if (isAdditive) {
            for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                underwritingInfo.adjustCommissionProperties(commissionFactor, fixedCommissionFactor, variableCommissionFactor);
            }
        }
        else {
            for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                underwritingInfo.setCommissionProperties(commissionFactor, fixedCommissionFactor, variableCommissionFactor);
            }
        }
    }
}
