package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ClaimRIOutcome;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimRIOutcome;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public class AllContractPaidTestImpl implements IAllContractClaimCache {

    Collection<ClaimCashflowPacket> someClaims;

    Map<Integer/* Sim period */ , Map<Integer /* UW period */ , Collection<ClaimCashflowPacket>>> simPUwPerCashflow = Maps.newHashMap();

    public AllContractPaidTestImpl( Collection<ClaimCashflowPacket> claims ) {
        this.someClaims = claims;
    }

    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInModelPeriod(Integer uwPeriod, PeriodScope periodScope, ContractCoverBase base) {
        throw new SimulationException("");
    }

    public Set<IClaimRoot> allIncurredClaimsInModelPeriod(Integer anInt, PeriodScope periodScope, ContractCoverBase coverBase) {
        Set<IClaimRoot> roots = RIUtilities.incurredClaims(someClaims, IncurredClaimBase.BASE);
        return RIUtilities.incurredClaimsByPeriod(anInt, periodScope.getPeriodCounter(), roots, coverBase);
    }

    public Set<IClaimRoot> allIncurredClaimsCurrentModelPeriodForAllocation(PeriodScope periodScope, ContractCoverBase coverBase) {
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
    public Collection<ClaimCashflowPacket> allCashflowClaimsUpToSimulationPeriod(Integer simulationPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        throw new SimulationException("");
    }

    @Override
    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInSimulationPeriod(Integer anInt, PeriodScope periodScope, ContractCoverBase base) {
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
    public void cacheClaims(Collection<ClaimCashflowPacket> newClaims, Integer simulationPeriod) {
        throw new SimulationException("");
    }

    public Collection<ClaimCashflowPacket> cashflowsByUnderwritingPeriodUpToSimulationPeriod(Integer simulationPeriod, Integer underwritingPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        return simPUwPerCashflow.get(simulationPeriod).get(underwritingPeriod);
    }

    public void updateSimPeriodUWPeriodCashflowMap(Integer simPeriod, Integer uwPeriod, Collection<ClaimCashflowPacket> coll) {

        if (simPUwPerCashflow.get(simPeriod) == null) {
            Map<Integer, Collection<ClaimCashflowPacket>> aMap = Maps.newHashMap();
            simPUwPerCashflow.put(simPeriod, aMap);
        }
        Map<Integer, Collection<ClaimCashflowPacket>> uwPMap = simPUwPerCashflow.get(simPeriod);
        uwPMap.put(uwPeriod, coll);
    }

    public void addCededPackets(List<ClaimCashflowPacket> moreCededCashflows) {
        this.cededCashflows.addAll(moreCededCashflows);
    }

    public void addCededIncurred(List<IncurredClaimRIOutcome> someIncurred) {
        this.cededIncurred.addAll(someIncurred);
    }

    private List<ClaimCashflowPacket> cededCashflows = Lists.newArrayList();
    private List<IncurredClaimRIOutcome> cededIncurred = Lists.newArrayList();

    public List<ClaimCashflowPacket> allCededCashlowsToDate() {
        return cededCashflows;
    }

    @Override
    public List<ICededRoot> allCededRootClaimsToDate() {
        throw new SimulationException("");
    }

    @Override
    public void cacheCededClaims(final List<ClaimRIOutcome> cededCashflows, final List<IncurredClaimRIOutcome> cededIncurred) {
        throw new SimulationException("");
    }

    @Override
    public List<ClaimRIOutcome> allRIOutcomesToDate() {
        throw new SimulationException("");
    }

    @Override
    public Collection<IncurredClaimRIOutcome> allIncurredRIOutcomesToDate() {
        return cededIncurred;
    }

    public ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByBaseClaim() {
        throw new SimulationException("");
    }
}
