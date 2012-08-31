package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * author simon.parten @ art-allianz . com
 */
public class SimplePaidAllocation implements IPaidAllocation {

    public List<ClaimCashflowPacket> allocatePaid(double paidInPeriod, List<ClaimCashflowPacket> claimsInContractPeriod,
                          List<CededClaimRoot> cededClaims, PeriodScope periodScope, double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase) {

        List<ClaimCashflowPacket> cededCashflows = cededClaims.collectAll { it ->
            new ClaimCashflowPacket(it, it.getGrossClaim())
        }

        return cededCashflows


    }
}