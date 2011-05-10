package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 *  Common methods to calculate the effects of a reinsurance contract
 *  implemented by all reinsurance contract strategies.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContract {

    void initBookkeepingFigures(List<ClaimCashflowPacket> grossClaims);

    /**
     *  Calculates the claim covered of the loss net after contracts with
     *  a smaller inuring priority or preceding contracts in the net.
     */
    ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage);

    UnderwritingInfoPacket calculateUnderwritingInfoCeded(UnderwritingInfoPacket grossInfo);

}
