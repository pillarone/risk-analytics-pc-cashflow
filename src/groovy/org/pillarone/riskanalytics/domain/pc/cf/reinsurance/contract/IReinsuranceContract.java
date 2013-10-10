package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;

import java.io.Serializable;
import java.util.List;

/**
 *  Common methods to calculate the effects of a reinsurance contract
 *  implemented by all reinsurance contract strategies.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContract extends Serializable{

    /** used to reset deductibles, limits if required
     * @param period
     * @param inFactors
     */
    void initPeriod(int period, List<FactorsPacket> inFactors);


    void add(UnderwritingInfoPacket grossUnderwritingInfo);

    /**
     *  This function is used if some pre-processing steps are required before calculating ceded claims on an individual
     *  level, ie CXL, SL. As it gets the whole list of covered claims this method can also be used to implement specific
     *  ceded value allocation strategies by preparing weights used afterwards in calculateClaimCeded.
     *  Default implementation is void.
     * @param grossClaim
     * @param grossUnderwritingInfo
     */
    void initBasedOnAggregateCalculations(List<ClaimCashflowPacket> grossClaim, List<UnderwritingInfoPacket> grossUnderwritingInfo);

    /**
     *  Calculates the claim covered
     *  @param grossClaim
     *  @param storage
     *  @return
     */
    ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter);


    /**
     * @param cededUnderwritingInfos
     * @param netUnderwritingInfos
     * @param coveredByReinsurers
     * @param fillNet if true the second list is filled too
     */
    void calculateUnderwritingInfo(List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                   List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers,
                                   boolean fillNet);

}
