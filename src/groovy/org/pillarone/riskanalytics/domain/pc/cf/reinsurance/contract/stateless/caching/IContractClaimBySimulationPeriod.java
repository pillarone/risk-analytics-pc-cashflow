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

    Collection<ClaimCashflowPacket>     allCashflowClaimsUpToSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase);

    Collection<ClaimCashflowPacket> allClaimCashflowPacketsInSimulationPeriod(Collection<ClaimCashflowPacket> allCashflows, PeriodScope periodScope, ContractCoverBase base, Integer anInt);

    Collection<IClaimRoot> allIncurredClaimsInSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase);

    Collection<IClaimRoot> allIncurredClaimsCurrentSimulationPeriod(PeriodScope periodScope, ContractCoverBase coverBase);

    void cacheClaims(Collection<ClaimCashflowPacket> claims, Integer simulationPeriod);

}
