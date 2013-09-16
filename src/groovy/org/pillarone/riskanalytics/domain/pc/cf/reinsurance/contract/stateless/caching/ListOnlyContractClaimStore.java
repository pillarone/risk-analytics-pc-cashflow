package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class ListOnlyContractClaimStore implements IAllContractClaimCache {

    private final Collection<ClaimCashflowPacket> allCashflows = new ArrayList<ClaimCashflowPacket>();
    private final Map<Integer, Collection<ClaimCashflowPacket>> claimsBySimulationPeriod = new HashMap<Integer, Collection<ClaimCashflowPacket>>();


    public Collection<ClaimCashflowPacket> allClaimCashflowPackets() {
        return allCashflows;
    }

    public Set<IClaimRoot> allIncurredClaims() {
        return RIUtilities.incurredClaims(this.allCashflows, IncurredClaimBase.BASE);
    }

    public SetMultimap<IClaimRoot, IClaimRoot> incurredClaimsByKey() {
        return RIUtilities.incurredClaims(this.allCashflows);
    }

    public void cacheClaims(Collection<ClaimCashflowPacket> claims) {
        throw new SimulationException("Inappropriate caching mechanism used");
    }

    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInModelPeriod(Integer uwPeriod, PeriodScope periodScope, ContractCoverBase base) {
        return GRIUtilities.cashflowsCoveredInModelPeriod(this.allCashflows, periodScope, base, uwPeriod);
    }

    public Set<IClaimRoot> allIncurredClaimsInModelPeriod(Integer anInt, PeriodScope periodScope, ContractCoverBase coverBase) {
        return RIUtilities.incurredClaimsByPeriod(anInt, periodScope.getPeriodCounter() ,
                incurredClaimsByKey().values(), coverBase);
    }

    public Set<IClaimRoot> allIncurredClaimsCurrentModelPeriodForAllocation(PeriodScope periodScope, ContractCoverBase coverBase) {
        return RIUtilities.incurredClaimsByPeriod(
                periodScope.getCurrentPeriod(), periodScope.getPeriodCounter(), allIncurredClaims(), coverBase);
    }

    public Collection<IClaimRoot> allIncurredClaimsUpToSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        Collection<ClaimCashflowPacket> cashflowsBeforeSimPeriod = allCashflowClaimsUpToSimulationPeriod(period, periodScope, coverBase);
        return RIUtilities.incurredClaims(cashflowsBeforeSimPeriod, IncurredClaimBase.BASE);
    }

    public Collection<ClaimCashflowPacket> allCashflowClaimsUpToSimulationPeriod(Integer simulationPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        Collection<ClaimCashflowPacket> claimsBeforeSimPeriod = new ArrayList<ClaimCashflowPacket>();
        for (int i = 0; i <= simulationPeriod; i++) {
            claimsBeforeSimPeriod.addAll(claimsBySimulationPeriod.get(i));
        }
        return claimsBeforeSimPeriod;
    }

    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInSimulationPeriod(Integer anInt, PeriodScope periodScope, ContractCoverBase base) {
        return claimsBySimulationPeriod.get(anInt);
    }

    public Collection<IClaimRoot> allIncurredClaimsInSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        Collection<ClaimCashflowPacket> cashflowPackets = claimsBySimulationPeriod.get(period);
        return RIUtilities.incurredClaims(cashflowPackets, IncurredClaimBase.BASE);
    }

    public Collection<IClaimRoot> allIncurredClaimsCurrentSimulationPeriod(PeriodScope periodScope, ContractCoverBase coverBase) {
        return allIncurredClaimsInSimulationPeriod(periodScope.getCurrentPeriod(), periodScope, coverBase);
    }

    public void cacheClaims(Collection<ClaimCashflowPacket> newClaims, Integer simulationPeriod) {
        final Collection<ClaimCashflowPacket> cashflowPackets = new ArrayList<ClaimCashflowPacket>();
        cashflowPackets.addAll(newClaims);
        allCashflows.addAll(newClaims);
        if (claimsBySimulationPeriod.get(simulationPeriod) != null) {
            throw new SimulationException("Attempted to overwrite claimsBySimulationPeriod cache in claim store. Contact development");
        }
        claimsBySimulationPeriod.put(simulationPeriod, cashflowPackets );
    }

    public Collection<ClaimCashflowPacket> cashflowsByUnderwritingPeriodUpToSimulationPeriod(Integer simulationPeriod, Integer underwritingPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        Collection<ClaimCashflowPacket> claimsToSimPeriod = allCashflowClaimsUpToSimulationPeriod(simulationPeriod, periodScope, coverBase);
        Collection<ClaimCashflowPacket> cashflowsPaidAgainsThisModelPeriod = GRIUtilities.cashflowsCoveredInModelPeriod(claimsToSimPeriod, periodScope, coverBase, underwritingPeriod);
        return cashflowsPaidAgainsThisModelPeriod;
    }

    private List<ClaimRIOutcome> allCededCashflows = Lists.newArrayList();
    private List<IncurredClaimRIOutcome> allCededIncured = Lists.newArrayList();

    @Override
    public void cacheCededClaims(final List<ClaimRIOutcome> cededCashflows, final List<IncurredClaimRIOutcome> cededIncurred) {
        allCededCashflows.addAll(cededCashflows);
        allCededIncured.addAll(cededIncurred);
    }

    @Override
    public List<ClaimCashflowPacket> allCededCashlowsToDate() {
        final List<ClaimCashflowPacket> packets = Lists.newArrayList();
        for (ClaimRIOutcome allCededCashflow : allCededCashflows) {
            packets.add(allCededCashflow.getCededClaim());
        }
        return packets;
    }

    @Override
    public List<ClaimRIOutcome> allRIOutcomesToDate() {
        return allCededCashflows;
    }

    @Override
    public List<ICededRoot> allCededRootClaimsToDate() {
        return new ArrayList<ICededRoot>( RIUtilities.incurredCededClaims(allCededCashlowsToDate(), IncurredClaimBase.BASE) );
    }

    @Override
    public Collection<IncurredClaimRIOutcome> allIncurredRIOutcomesToDate() {
        return allCededIncured;
    }

    public ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByBaseClaim() {
        ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByBaseClaim = ArrayListMultimap.create();
        for (ClaimCashflowPacket aCashflow : allCashflows) {
            cashflowsByBaseClaim.put(aCashflow.getBaseClaim(), aCashflow);
        }
        return cashflowsByBaseClaim;

    }
}
