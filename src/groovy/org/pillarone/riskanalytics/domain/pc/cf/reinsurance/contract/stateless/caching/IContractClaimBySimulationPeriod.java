package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;

import java.util.Collection;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IContractClaimBySimulationPeriod {

    Collection<IClaimRoot> allIncurredClaimsUpToSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase);

    Collection<ClaimCashflowPacket>     allCashflowClaimsUpToSimulationPeriod(Integer simulationPeriod, PeriodScope periodScope, ContractCoverBase coverBase);

    Collection<ClaimCashflowPacket> allClaimCashflowPacketsInSimulationPeriod(Integer anInt, PeriodScope periodScope, ContractCoverBase base);

    Collection<IClaimRoot> allIncurredClaimsInSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase);

    Collection<IClaimRoot> allIncurredClaimsCurrentSimulationPeriod(PeriodScope periodScope, ContractCoverBase coverBase);

    void cacheClaims(Collection<ClaimCashflowPacket> newClaims, Integer simulationPeriod);

}
