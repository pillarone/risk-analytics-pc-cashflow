package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

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

    public void initPeriod() {
    }

    protected void add(ClaimCashflowPacket grossClaim, ClaimCashflowPacket cededClaim) {
        grossClaims.add(grossClaim);
        cededClaims.add(cededClaim);
    }

    public void add(UnderwritingInfoPacket grossUnderwritingInfo) {
        grossUwInfos.add(grossUnderwritingInfo);
    }
}
