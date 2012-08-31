package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Collection;
import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidAllocation {

    List<ClaimCashflowPacket> allocatePaid(double paidInPeriod, List<ClaimCashflowPacket> allCashflows, List<CededClaimRoot> allCededClaims, PeriodScope periodScope, double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase);

}

