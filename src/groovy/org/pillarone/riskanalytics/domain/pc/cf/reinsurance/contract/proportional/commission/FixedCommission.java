package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;

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
                                    boolean isAdditive, Integer occurrencePeriod) {
        if (isAdditive) {
            for (CededUnderwritingInfoPacket underwritingInfo : cededUnderwritingInfos) {
                double premiumPaid = underwritingInfo.getPremiumPaid();
                underwritingInfo.setCommission(underwritingInfo.getCommission() - premiumPaid * commission);
                underwritingInfo.setCommissionFixed(underwritingInfo.getCommissionFixed() - premiumPaid * commission);
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
