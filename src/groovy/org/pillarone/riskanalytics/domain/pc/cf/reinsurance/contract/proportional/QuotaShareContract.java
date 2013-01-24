package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.NoneLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.ILossParticipation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.NoLossParticipation;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContract extends AbstractProportionalReinsuranceContract {

    protected double quotaShare = 0;

    public QuotaShareContract(double quotaShare, ICommission commission) {
        this.quotaShare = quotaShare;
        this.commission = commission;
        this.lossParticipation = new NoLossParticipation();
    }

    public QuotaShareContract(double quotaShare, ICommission commission, ILossParticipation lossParticipation) {
        this(quotaShare, commission);
        this.lossParticipation = lossParticipation;
    }

    @Override
    public void initBasedOnAggregateCalculations(List<ClaimCashflowPacket> grossClaim, List<UnderwritingInfoPacket> grossUnderwritingInfo) {
        lossParticipation.initPeriod(grossClaim, grossUnderwritingInfo, new NoneLimitStrategy());
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        ClaimCashflowPacket cededClaim;
        if (lossParticipation.noLossParticipation()) {
            cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, -quotaShare, -quotaShare, -quotaShare, true);
        }
        else {
            cededClaim = lossParticipation.cededClaim(quotaShare, grossClaim, storage, true);
        }
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    public void calculatePremium(List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers, boolean fillNet) {
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.scale(grossUnderwritingInfo,
                    contractMarker, 1, quotaShare * coveredByReinsurers, 1);
            UnderwritingInfoUtils.applyMarkers(grossUnderwritingInfo, cededUnderwritingInfo);
            cededUwInfos.add(cededUnderwritingInfo);
            netUnderwritingInfos.add(grossUnderwritingInfo.getNet(cededUnderwritingInfo, true));
        }
    }


     @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(quotaShare);
        buffer.append(", ");
        buffer.append(commission.toString());
        return buffer.toString();
    }
}
