package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;


import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot

/**
 * author simon.parten @ art-allianz . com
 */
public class SimplePaidAllocation implements IPaidAllocation {

    public List<ClaimCashflowPacket> allocatePaid(Map<Integer, Double> incrementalPaidByPeriod, List<ClaimCashflowPacket> claimsInContractPeriod, List<ClaimCashflowPacket> cededCashflowsToDate, PeriodScope periodScope, double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase, List<ICededRoot> incurredCededClaims) {

        List<ClaimCashflowPacket> cededCashflows = cededClaims.collectAll { it ->
            new ClaimCashflowPacket(it, it.getGrossClaim())
        }

        return cededCashflows


    }
}