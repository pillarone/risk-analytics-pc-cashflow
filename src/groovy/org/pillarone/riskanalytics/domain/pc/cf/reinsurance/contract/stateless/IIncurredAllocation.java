package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IIncurredAllocation {

    AllClaimsRIOutcome allocateClaims(double incurredInPeriod, IAllContractClaimCache claimStore, PeriodScope periodScope, ContractCoverBase base);

}
