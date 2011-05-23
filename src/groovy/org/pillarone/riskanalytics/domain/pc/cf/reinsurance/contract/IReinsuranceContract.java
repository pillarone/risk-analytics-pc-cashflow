package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 *  Common methods to calculate the effects of a reinsurance contract
 *  implemented by all reinsurance contract strategies.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContract {

    /** used to reset deductibles, limits if required */
    void initPeriod();


    void add(UnderwritingInfoPacket grossUnderwritingInfo);

    /**
     *  Calculates the claim covered of the loss net after contracts with
     *  a smaller inuring priority or preceding contracts in the net.
     *  @param grossClaim
     *  @param storage
     *  @return
     */
    ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage);


    /**
     * @param cededUnderwritingInfos
     * @param netUnderwritingInfos
     * @param fillNet if true the second list is filled too
     */
    void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                   List<UnderwritingInfoPacket> netUnderwritingInfos, boolean fillNet);

}
