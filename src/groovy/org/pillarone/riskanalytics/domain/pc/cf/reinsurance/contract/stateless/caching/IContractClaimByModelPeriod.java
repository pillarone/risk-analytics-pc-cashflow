package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.SetMultimap;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;

import java.util.Collection;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IContractClaimByModelPeriod {

    Collection<ClaimCashflowPacket> allClaimCashflowPacketsInModelPeriod(Collection<ClaimCashflowPacket> allCashflows, PeriodScope periodScope, ContractCoverBase base, Integer anInt);

    Set<IClaimRoot> allIncurredClaimsInModelPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase);

    Set<IClaimRoot> allIncurredClaimsCurrentModelPeriod(PeriodScope periodScope, ContractCoverBase coverBase);

    void cacheClaims(Collection<ClaimCashflowPacket> claims);

}
