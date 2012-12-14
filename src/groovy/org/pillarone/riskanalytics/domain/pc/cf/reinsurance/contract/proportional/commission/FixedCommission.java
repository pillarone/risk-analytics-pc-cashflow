package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FixedCommission implements ICommission {

    private double commission = 0;

    public FixedCommission(double commission) {
        this.commission = commission;
    }

    public void calculateCommission(List<ClaimCashflowPacket> grossClaims,
                                    List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                    boolean isAdditive) {
        if (isAdditive) {
            for (CededUnderwritingInfoPacket underwritingInfo : cededUnderwritingInfos) {
                double premiumWritten = underwritingInfo.getPremiumWritten();
                underwritingInfo.setCommission(underwritingInfo.getCommission() - premiumWritten * commission);
                underwritingInfo.setCommissionFixed(underwritingInfo.getCommissionFixed() - premiumWritten * commission);
                underwritingInfo.setCommissionVariable(underwritingInfo.getCommissionVariable());
            }
        }
        else {
            for (CededUnderwritingInfoPacket underwritingInfo : cededUnderwritingInfos) {
                underwritingInfo.setCommission(-underwritingInfo.getPremiumPaid() * commission);
                underwritingInfo.setCommissionFixed(underwritingInfo.getCommission());
                underwritingInfo.setCommissionVariable(0d);
            }
        }
    }
}
