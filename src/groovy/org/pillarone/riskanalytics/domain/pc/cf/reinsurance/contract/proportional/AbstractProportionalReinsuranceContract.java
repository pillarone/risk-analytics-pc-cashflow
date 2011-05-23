package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractProportionalReinsuranceContract extends AbstractReinsuranceContract implements IPropReinsuranceContract {

    protected ICommission commission;

    /**
     * @param cededUnderwritingInfos used as reference in order to attach resulting packets
     * @param netUnderwritingInfos used as reference in order to attach resulting packets
     */
    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos, boolean fillNet) {
        calculatePremium();
        calculateCommission();
        cededUnderwritingInfos.addAll(cededUwInfos);
        fillNetUnderwritingInfo(fillNet);
    }

    private void fillNetUnderwritingInfo(boolean fillNet) {
//        throw new NotImplementedException("net uw info not yet filled");
        // todo(sku): implement
//        if (fillNet) {
//            for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
//                UnderwritingInfoPacket netUnderwritingInfo = grossUnderwritingInfo.net(cededUnderwritingInfo);
//            }
//        }
    }

    abstract public void calculatePremium();

    public void calculateCommission() {
        commission.calculateCommission(cededClaims, cededUwInfos, false, false);
    }

}
