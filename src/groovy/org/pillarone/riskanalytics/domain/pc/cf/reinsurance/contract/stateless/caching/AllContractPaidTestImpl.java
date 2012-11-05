package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.SetMultimap;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.Collection;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public class AllContractPaidTestImpl implements IAllContractClaimCache {

    Collection<ClaimCashflowPacket> someClaims;

    public AllContractPaidTestImpl( Collection<ClaimCashflowPacket> claims ) {
        this.someClaims = claims;
    }

    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInModelPeriod(Collection<ClaimCashflowPacket> allCashflows, PeriodScope periodScope, ContractCoverBase base, Integer anInt) {
        throw new SimulationException("");
    }

    public Set<IClaimRoot> allIncurredClaimsInModelPeriod(Integer anInt, PeriodScope periodScope, ContractCoverBase coverBase) {
        Set<IClaimRoot> roots = RIUtilities.incurredClaims(someClaims, IncurredClaimBase.BASE);
        return RIUtilities.incurredClaimsByPeriod(anInt, periodScope.getPeriodCounter(), roots, coverBase);
    }

    public Set<IClaimRoot> allIncurredClaimsCurrentModelPeriod(PeriodScope periodScope, ContractCoverBase coverBase) {
        throw new SimulationException("");
    }

    public Collection<ClaimCashflowPacket> allClaimCashflowPackets() {
        return someClaims;
    }

    public Set<IClaimRoot> allIncurredClaims() {
        return RIUtilities.incurredClaims(someClaims, IncurredClaimBase.BASE);
    }

    public SetMultimap<IClaimRoot, IClaimRoot> incurredClaimsByKey() {
        throw new SimulationException("");
    }

    public void cacheClaims(Collection<ClaimCashflowPacket> claims) {
        throw new SimulationException("");
    }

    @Override
    public Collection<IClaimRoot> allIncurredClaimsUpToSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        throw new SimulationException("");
    }

    @Override
    public Collection<ClaimCashflowPacket> allCashflowClaimsUpToSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        throw new SimulationException("");
    }

    @Override
    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInSimulationPeriod(Collection<ClaimCashflowPacket> allCashflows, PeriodScope periodScope, ContractCoverBase base, Integer anInt) {
        throw new SimulationException("");
    }

    @Override
    public Collection<IClaimRoot> allIncurredClaimsInSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        throw new SimulationException("");
    }

    @Override
    public Collection<IClaimRoot> allIncurredClaimsCurrentSimulationPeriod(PeriodScope periodScope, ContractCoverBase coverBase) {
        throw new SimulationException("");
    }

    @Override
    public void cacheClaims(Collection<ClaimCashflowPacket> claims, Integer simulationPeriod) {
        throw new SimulationException("");
    }
}
