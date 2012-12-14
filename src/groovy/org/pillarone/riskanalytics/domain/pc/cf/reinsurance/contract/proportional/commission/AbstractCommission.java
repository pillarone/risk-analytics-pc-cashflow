package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractCommission implements ICommission {

    protected BasedOnClaimProperty useClaims;

    protected double sumPremium(List<? extends UnderwritingInfoPacket> underwritingInfos) {
        double totalPremium = 0;
        if (useClaims.equals(BasedOnClaimProperty.ULTIMATE) || useClaims.equals(BasedOnClaimProperty.REPORTED)) {
            for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                totalPremium += underwritingInfo.getPremiumWritten();
            }
            return totalPremium;
        }
        else if (useClaims.equals(BasedOnClaimProperty.PAID)) {
            for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                totalPremium += underwritingInfo.getPremiumPaid();
            }
            return totalPremium;
        }
        else {
            throw new NotImplementedException("BasedOnClaimProperty " + useClaims.toString() + " not implemented.");
        }
    }

    /**
     *
     * @param claims
     * @return sum of cumulated values according to the useClaims settings
     */
    protected double sumCumulatedClaims(List<ClaimCashflowPacket> claims) {
        double totalClaims = 0;
        if (useClaims.equals(BasedOnClaimProperty.ULTIMATE)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.ultimate() + claim.developmentResultCumulative();
            }
        }
        else if (useClaims.equals(BasedOnClaimProperty.PAID)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.getPaidCumulatedIndexed();
            }
        }
        else if (useClaims.equals(BasedOnClaimProperty.REPORTED)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.getReportedCumulatedIndexed();
            }
        }
        else {
            throw new NotImplementedException("BasedOnClaimProperty " + useClaims.toString() + " not implemented.");
        }
        return totalClaims;
    }

    protected void adjustCommissionProperties(List<CededUnderwritingInfoPacket> underwritingInfos, boolean isAdditive,
                         double commission, double fixedCommission, double variableCommission) {
        double totalCededPremium = 0;
        for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            totalCededPremium += useClaims.premium(underwritingInfo);
        }
        for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            double premiumRatio = useClaims.premium(underwritingInfo) / totalCededPremium;
            double commissionShare = premiumRatio * commission;
            double fixedCommissionShare = premiumRatio * fixedCommission;
            double variableCommissionShare = premiumRatio * variableCommission;
            if (isAdditive) {
                underwritingInfo.add(commissionShare, fixedCommissionShare, variableCommissionShare);
            }
            else {
                underwritingInfo.apply(commissionShare, fixedCommissionShare, variableCommissionShare);
            }
        }
    }
}
