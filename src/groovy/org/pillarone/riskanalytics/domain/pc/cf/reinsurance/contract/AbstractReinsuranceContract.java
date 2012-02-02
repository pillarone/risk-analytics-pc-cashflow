package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * Each instance is dealing with one period only!
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractReinsuranceContract implements IReinsuranceContract {

    protected IReinsuranceContractMarker contractMarker;

    protected List<ClaimCashflowPacket> grossClaims = new ArrayList<ClaimCashflowPacket>();
    protected List<ClaimCashflowPacket> cededClaims = new ArrayList<ClaimCashflowPacket>();
    protected List<UnderwritingInfoPacket> grossUwInfos = new ArrayList<UnderwritingInfoPacket>();
    protected List<CededUnderwritingInfoPacket> cededUwInfos = new ArrayList<CededUnderwritingInfoPacket>();

    public void initPeriod(List<FactorsPacket> inFactors) {
    }

    public void initPeriodClaims(List<ClaimCashflowPacket> grossClaim) {
    }

    protected void add(ClaimCashflowPacket grossClaim, ClaimCashflowPacket cededClaim) {
        grossClaims.add(grossClaim);
        cededClaims.add(cededClaim);
    }

    public void add(UnderwritingInfoPacket grossUnderwritingInfo) {
//        if (grossUnderwritingInfo.getPremiumWritten() + grossUnderwritingInfo.getPremiumPaid() < 0) {
        if (grossUnderwritingInfo instanceof CededUnderwritingInfoPacket) {
            UnderwritingInfoPacket convertedPacket = new UnderwritingInfoPacket((CededUnderwritingInfoPacket) grossUnderwritingInfo, -1);
            grossUwInfos.add(convertedPacket);
        }
        else {
            grossUwInfos.add(grossUnderwritingInfo);
        }
    }
}
