package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.ICededClaimStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPaidAllocation {

    AllCashflowClaimsRIOutcome allocatePaid(Map<Integer, Double> incrementalPaidByPeriod, List<ClaimCashflowPacket> grossCashflowsThisPeriod, ICededClaimStore claimStore, PeriodScope periodScope, ContractCoverBase coverageBase, AllClaimsRIOutcome incurredCededClaims, boolean sanityChecks);

}

