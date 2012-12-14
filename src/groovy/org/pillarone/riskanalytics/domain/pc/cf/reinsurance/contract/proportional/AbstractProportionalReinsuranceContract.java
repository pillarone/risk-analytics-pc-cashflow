package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AbstractReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.ILossParticipation;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractProportionalReinsuranceContract extends AbstractReinsuranceContract implements IPropReinsuranceContract {

    protected ICommission commission;
    protected ProportionalPremiumBase premiumBase;
    protected ILossParticipation lossParticipation;

    /**
     * Clearing grossUwInfos and cededUwInfos lists
     * @param period
     * @param inFactors
     */
    @Override
    public void initPeriod(int period, List<FactorsPacket> inFactors) {
        super.initPeriod(period, inFactors);
        grossUwInfos.clear();
        cededUwInfos.clear();
        cededClaims.clear();
    }

    /**
     * @param cededUnderwritingInfos used as reference in order to attach resulting packets
     * @param netUnderwritingInfos used as reference in order to attach resulting packets
     */
    public void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                          List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers,
                                          boolean fillNet) {
        calculatePremium(netUnderwritingInfos, coveredByReinsurers, fillNet);
        calculateCommission();
        cededUnderwritingInfos.addAll(cededUwInfos);
    }

    abstract public void calculatePremium(List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers, boolean fillNet);

    public void calculateCommission() {
        // todo(sku): check whether all is fine regarding coveredByReinsurers and commissions
        // todo(sku): reduce argument numbers
        commission.calculateCommission(cededClaims, cededUwInfos, false);
    }

    public ProportionalPremiumBase premiumBase() {
        return premiumBase;
    }
}
