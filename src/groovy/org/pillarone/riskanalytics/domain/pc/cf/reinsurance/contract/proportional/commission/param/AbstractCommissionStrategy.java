package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractCommissionStrategy extends AbstractParameterObject implements ICommissionStrategy  {

    private static final String USE_CLAIMS = "useClaims";

    protected CommissionBase useClaims = CommissionBase.PAID;

    public Map getParameters() {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(USE_CLAIMS, useClaims);
        return map;
    }

    protected double sumPremiumPaid(List<CededUnderwritingInfoPacket> underwritingInfos) {
        double totalPremium = 0;
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            totalPremium += underwritingInfo.getPremiumPaid();
        }
        return totalPremium;
    }

    protected double sumClaims(List<ClaimCashflowPacket> claims) {
        double totalClaims = 0;
        if (useClaims.convert().equals(BasedOnClaimProperty.ULTIMATE_UNINDEXED)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.ultimate();
            }
        }
        else if (useClaims.convert().equals(BasedOnClaimProperty.PAID)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.getPaidIncrementalIndexed();
            }
        }
        else if (useClaims.convert().equals(BasedOnClaimProperty.REPORTED)) {
            for (ClaimCashflowPacket claim : claims) {
                totalClaims += claim.getReportedIncrementalIndexed();
            }
        }
        else {
            throw new NotImplementedException("CommissionBase " + useClaims.toString() + " not implemented.");
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

    @Override
    public DoubleValuePerPeriod getInitialLossCarriedForward() {
        return new DoubleValuePerPeriod();
    }
}
