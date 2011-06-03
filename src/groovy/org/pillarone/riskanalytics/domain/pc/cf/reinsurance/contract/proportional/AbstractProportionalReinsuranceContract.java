package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

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

    /** used to make sure that fixed premium is paid only in first period */
    private boolean isStartCoverPeriod = true;


    /**
     * @param cededUnderwritingInfos used as reference in order to attach resulting packets
     * @param netUnderwritingInfos used as reference in order to attach resulting packets
     */
    // todo(sku): commission paid over several periods? This would require a different approach than isStartCoverPeriod
    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos, boolean fillNet) {
        if (!isStartCoverPeriod) return;
        isStartCoverPeriod = false;
        calculatePremium(netUnderwritingInfos, fillNet);
        calculateCommission();
        cededUnderwritingInfos.addAll(cededUwInfos);
    }

    abstract public void calculatePremium(List<UnderwritingInfoPacket> netUnderwritingInfos, boolean fillNet);

    public void calculateCommission() {
        commission.calculateCommission(cededClaims, cededUwInfos, false, false);
    }

}
