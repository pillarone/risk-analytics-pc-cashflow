package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractCommission implements ICommission {

    protected BasedOnClaimProperty useClaims;

    // the following two properties allow an approximation if commission is paid after ceded uw info is updated
    // it is required for a trivial allocation.
    private ISegmentMarker firstSegment;
    private ILegalEntityMarker firstLegalEntity;

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

    protected CededUnderwritingInfoPacket extraPacketForCommission(double fixedCommission, double variableCommission,
                                                                   DateTime inceptionDate, Integer inceptionPeriod) {
        CededUnderwritingInfoPacket underwritingInfo = new CededUnderwritingInfoPacket();
        underwritingInfo.setExposure(new ExposureInfo(inceptionDate, inceptionPeriod));
        underwritingInfo.setDate(inceptionDate);
        underwritingInfo.setCommission(fixedCommission + variableCommission);
        underwritingInfo.setCommissionFixed(fixedCommission);
        underwritingInfo.setCommissionVariable(variableCommission);
        underwritingInfo.setMarker(firstSegment);
        underwritingInfo.setMarker(firstLegalEntity);
        return underwritingInfo;
    }

    protected void adjustCommissionProperties(List<CededUnderwritingInfoPacket> underwritingInfos, boolean isAdditive,
                         double commission, double fixedCommission, double variableCommission) {
        if (firstSegment == null && !underwritingInfos.isEmpty()) {
            firstSegment = underwritingInfos.get(0).segment();
            firstLegalEntity = underwritingInfos.get(0).legalEntity();
        }
        double totalCededPremium = 0;
        for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            totalCededPremium += useClaims.premium(underwritingInfo);
        }
        for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            if (totalCededPremium == 0) {
                if (isAdditive) {
                    underwritingInfo.add(commission, fixedCommission, variableCommission);
                }
                else {
                    underwritingInfo.apply(commission, fixedCommission, variableCommission);
                }
                break;  // no distribution of commission among several packets
            }
            else {
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
}
