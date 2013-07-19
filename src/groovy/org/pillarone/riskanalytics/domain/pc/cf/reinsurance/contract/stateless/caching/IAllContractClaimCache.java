package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;

import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IAllContractClaimCache extends IContractClaimStore, IContractClaimByModelPeriod, IContractClaimBySimulationPeriod, ICededClaimStore {

    Collection<ClaimCashflowPacket> cashflowsByUnderwritingPeriodUpToSimulationPeriod(Integer simulationPeriod, Integer underwritingPeriod, PeriodScope periodScope, ContractCoverBase coverBase);

}
