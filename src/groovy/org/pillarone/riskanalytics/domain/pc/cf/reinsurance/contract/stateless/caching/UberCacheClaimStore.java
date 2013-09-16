package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.*;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
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
public class UberCacheClaimStore implements IAllContractClaimCache {

    private final Collection<ClaimCashflowPacket> allCashflows = Lists.newArrayList();
    private final Map<Integer /* sim period */, Collection<ClaimCashflowPacket>> claimsBySimulationPeriod = Maps.newHashMap();

    public Collection<ClaimCashflowPacket> allClaimCashflowPackets() {
        return allCashflows;
    }

    /* Incurred claim store ! -----------------------------------------------------------------------------*/
    private Set<IClaimRoot> cacheAllIncurredClaims = new HashSet<IClaimRoot>();
    public Set<IClaimRoot> allIncurredClaims() {
        return cacheAllIncurredClaims;
    }

    private void cacheIncurredClaims(Collection<ClaimCashflowPacket> newCashflows){
        Set<IClaimRoot> incurredClaims = RIUtilities.incurredClaims(newCashflows, IncurredClaimBase.BASE);
        cacheAllIncurredClaims.addAll(incurredClaims);
    }
    /* END INCURRED CLAIM STORE ! ---------------------------------------------------------------------------*/
    /* ------------------------------------------------------------------------------------------------------*/

    /* Incurred key Cache ! -----------------------------------------------------------------------------*/
    private SetMultimap<IClaimRoot, IClaimRoot> incurredClaimsByKey = HashMultimap.create();
    public SetMultimap<IClaimRoot, IClaimRoot> incurredClaimsByKey() {
        return incurredClaimsByKey;
    }
    /* END INCURRED KEY STORE ! ---------------------------------------------------------------------------*/
    /* ------------------------------------------------------------------------------------------------------*/

    private void cacheIncurredClaimsByKey(){
        incurredClaimsByKey = RIUtilities.incurredClaims(this.allCashflows);
    }


    public void cacheClaims(Collection<ClaimCashflowPacket> claims) {
        throw new SimulationException("Inappropriate caching mechanism used");
    }

    /* Cashflows by UW period. Must be cleaned out every new period ! */
    private Map<Integer /* uwPeriod */, Collection<ClaimCashflowPacket>> cashflowsByUWPeriod = Maps.newHashMap();
    public Collection<ClaimCashflowPacket> allClaimCashflowPacketsInModelPeriod(Integer uwPeriod, PeriodScope periodScope, ContractCoverBase base) {
        if(cashflowsByUWPeriod.get(uwPeriod) != null) {
            return cashflowsByUWPeriod.get(uwPeriod);
        }
        Collection<ClaimCashflowPacket> cashflowsInUWPeriod = GRIUtilities.cashflowsCoveredInModelPeriod(allCashflows, periodScope, base, uwPeriod);
        cashflowsByUWPeriod.put(uwPeriod, cashflowsInUWPeriod);
        return cashflowsInUWPeriod;
    }
    private void cleanCashflowsByUWPeriod() {
        cashflowsByUWPeriod = Maps.newHashMap();
        incurredClaimsByUWPeriod = Maps.newHashMap();
    }

    /* Incurred claims by underwriting period. Must be cleaned every period */
    private Map<Integer /* underwriting period */, Set<IClaimRoot>> incurredClaimsByUWPeriod = Maps.newHashMap();
    public Set<IClaimRoot> allIncurredClaimsInModelPeriod(Integer uwPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        if(incurredClaimsByUWPeriod.get(uwPeriod) != null) {
            return incurredClaimsByUWPeriod.get(uwPeriod);
        }
        Set<IClaimRoot> incurredClaimsInUWPeriod = RIUtilities.incurredClaimsByPeriod(uwPeriod, periodScope.getPeriodCounter() ,
                incurredClaimsByKey().values(), coverBase);
        incurredClaimsByUWPeriod.put(uwPeriod, incurredClaimsInUWPeriod);
        return incurredClaimsInUWPeriod;
    }
    /* ------------------------------------------------------------------------------------------------------*/
    /* END incurred UW CACHE ! ---------------------------------------------------------------------------*/
    /* ------------------------------------------------------------------------------------------------------*/

    /* convienience method which should use the exisiting cache */
    public Set<IClaimRoot> allIncurredClaimsCurrentModelPeriodForAllocation(PeriodScope periodScope, ContractCoverBase coverBase) {
        return allIncurredClaimsInModelPeriod( periodScope.getCurrentPeriod(), periodScope, coverBase );
//        return RIUtilities.incurredClaimsByPeriod(
//                periodScope.getCurrentPeriod(), periodScope.getPeriodCounter(), allIncurredClaims(), coverBase);
    }


    public Collection<IClaimRoot> allIncurredClaimsUpToSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        Collection<IClaimRoot> claimsBeforeSimPeriod = new ArrayList<IClaimRoot>();
        for (int i = 0; i <= period; i++) {
            claimsBeforeSimPeriod.addAll( allIncurredClaimsInSimulationPeriod(i, periodScope, coverBase) );
        }
        return claimsBeforeSimPeriod;    }

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

    /* Incurred claims by simulation period */
    private Map<Integer /* simulation period */ , Collection<IClaimRoot>> incurredClaimsBySimPeriod = Maps.newHashMap();
    public Collection<IClaimRoot> allIncurredClaimsInSimulationPeriod(Integer period, PeriodScope periodScope, ContractCoverBase coverBase) {
        if(incurredClaimsBySimPeriod.get(period) != null) {
            return incurredClaimsBySimPeriod.get(period);
        }
        Collection<ClaimCashflowPacket> cashflowPackets = claimsBySimulationPeriod.get(period);
        Collection<IClaimRoot> incurredClaimsInSimPeriod = RIUtilities.incurredClaims(cashflowPackets, IncurredClaimBase.BASE);
        incurredClaimsBySimPeriod.put(period, incurredClaimsInSimPeriod);
        return incurredClaimsInSimPeriod;
    }
    /* ------------------------------------------------------------------------------------------------------*/
    /* END Sim Period incurred CACHE ! ---------------------------------------------------------------------------*/
    /* ------------------------------------------------------------------------------------------------------*/

    /* Convienience method which should use existing cache */
    public Collection<IClaimRoot> allIncurredClaimsCurrentSimulationPeriod(PeriodScope periodScope, ContractCoverBase coverBase) {
        return allIncurredClaimsInSimulationPeriod(periodScope.getCurrentPeriod(), periodScope, coverBase);
    }

    public void cacheClaims(Collection<ClaimCashflowPacket> newClaims, Integer simulationPeriod) {
//        Basic caches
        final Collection<ClaimCashflowPacket> cashflowPackets = new ArrayList<ClaimCashflowPacket>();
        cashflowPackets.addAll(newClaims);
        allCashflows.addAll(newClaims);
        if (claimsBySimulationPeriod.get(simulationPeriod) != null) {
            throw new SimulationException("Attempted to overwrite claimsBySimulationPeriod cache in claim store. Contact development");
        }
        claimsBySimulationPeriod.put(simulationPeriod, cashflowPackets );
//        ---------------------------------

//        Optimisation layer! - note that the basic Caches MUST be built first.
        cacheIncurredClaims(newClaims);
        cacheIncurredClaimsByKey();
        cleanCashflowsByUWPeriod();
    }


    private Map<Integer /* simulation period */ , Map<Integer /* underwriting period */ , Collection<ClaimCashflowPacket>>> simPeriodUwPeriodClaims = Maps.newHashMap();
    public Collection<ClaimCashflowPacket> cashflowsByUnderwritingPeriodUpToSimulationPeriod(Integer simPeriod, Integer uWPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        if(uWPeriod > simPeriod) {
            throw new SimulationException("Asked for an underwriting period greater than simulation period. This is not possible");
        }
        if(simPeriodUwPeriodClaims.get(simPeriod) == null) {
            simPeriodUwPeriodClaims.put(simPeriod, new HashMap<Integer, Collection<ClaimCashflowPacket>>());
        }
        if(simPeriodUwPeriodClaims.get(simPeriod).get(uWPeriod) != null ) {
            return simPeriodUwPeriodClaims.get(simPeriod).get(uWPeriod);
        }
        Collection<ClaimCashflowPacket> claimsToSimPeriod = allCashflowClaimsUpToSimulationPeriod(simPeriod, periodScope, coverBase);
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();
        Collection<ClaimCashflowPacket> tempCollection;
        Map<Integer, Collection<ClaimCashflowPacket>> uwPeriodMap = simPeriodUwPeriodClaims.get(simPeriod);
//        Ensure every underwriting period has a collection - no null pointers!
        for (int tempuwPeriod = 0; tempuwPeriod <= simPeriod; tempuwPeriod++) {
            uwPeriodMap.put(tempuwPeriod, new ArrayList<ClaimCashflowPacket>());
        }
//        Fill each underwriting period with the claims up to this simulation period.
        for (ClaimCashflowPacket claimCashflowPacket : claimsToSimPeriod) {
            DateTime coverDate = coverBase.claimCoverDate(claimCashflowPacket);
            int uwPeriod = periodCounter.belongsToPeriod(coverDate);
            tempCollection = simPeriodUwPeriodClaims.get(simPeriod).get(uwPeriod);
            tempCollection.add(claimCashflowPacket);
        }

//        Collection<ClaimCashflowPacket> cashflowsPaidAgainsThisModelPeriod = GRIUtilities.cashflowsCoveredInModelPeriod(claimsToSimPeriod, periodScope, coverBase, uWPeriod);
        return simPeriodUwPeriodClaims.get(simPeriod).get(uWPeriod);
    }

    private List<ClaimRIOutcome> allCededCashflows = Lists.newArrayList();
    private List<IncurredClaimRIOutcome> allIncurredOutcomes = Lists.newArrayList();
    @Override
    public void cacheCededClaims(final List<ClaimRIOutcome> cededCashflows, final List<IncurredClaimRIOutcome> cededIncurred) {
        allCededCashflows.addAll(cededCashflows);
        allIncurredOutcomes.addAll(cededIncurred);
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
        return allIncurredOutcomes;
    }

    public ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByBaseClaim() {
        ArrayListMultimap<IClaimRoot, ClaimCashflowPacket> cashflowsByBaseClaim = ArrayListMultimap.create();
        for (ClaimCashflowPacket aCashflow : allCashflows) {
            cashflowsByBaseClaim.put(aCashflow.getBaseClaim(), aCashflow);
        }
        return cashflowsByBaseClaim;
    }
}
