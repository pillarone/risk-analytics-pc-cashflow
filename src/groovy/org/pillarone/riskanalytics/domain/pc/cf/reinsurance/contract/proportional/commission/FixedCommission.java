package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FixedCommission implements ICommission {

    private double commission = 0;
//    private boolean isStartCoverPeriod = true;

    public FixedCommission(double commission) {
        this.commission = commission;
    }

    public void calculateCommission(List<ClaimCashflowPacket> claims, List<CededUnderwritingInfoPacket> underwritingInfos,
                                    boolean isFirstPeriod, boolean isAdditive) {
//        if (!isStartCoverPeriod) return;
//        isStartCoverPeriod = false;
        if (isAdditive) {
            for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                double premiumWritten = underwritingInfo.getPremiumWritten();
                underwritingInfo.setCommission(underwritingInfo.getCommission() - premiumWritten * commission);
                underwritingInfo.setCommissionFixed(underwritingInfo.getCommissionFixed() - premiumWritten * commission);
                underwritingInfo.setCommissionVariable(underwritingInfo.getCommissionVariable());
            }
        }
        else {
            for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                underwritingInfo.setCommission(-underwritingInfo.getPremiumWritten() * commission);
                underwritingInfo.setCommissionFixed(underwritingInfo.getCommission());
                underwritingInfo.setCommissionVariable(0d);
            }
        }
    }
}
