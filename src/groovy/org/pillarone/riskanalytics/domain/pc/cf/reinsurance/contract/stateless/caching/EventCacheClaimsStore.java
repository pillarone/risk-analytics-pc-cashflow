package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching;

import com.google.common.collect.*;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.AggregatedEventClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * author simon.parten @ art-allianz . com
 */
public class EventCacheClaimsStore extends UberCacheClaimStore {


    Map<IEvent, IClaimRoot> aggregatedClaimByEvent = Maps.newHashMap();

    @Override
    public Set<IClaimRoot> allIncurredClaimsInModelPeriod(Integer uwPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        Set<IClaimRoot> iClaimRoots = super.allIncurredClaimsInModelPeriod(uwPeriod, periodScope, coverBase);
        SetMultimap<IEvent, IClaimRoot> rootClaimsByEvent = HashMultimap.create();
        for (IClaimRoot iClaimRoot : iClaimRoots) {
            rootClaimsByEvent.put(iClaimRoot.getEvent(), iClaimRoot);
        }

        Set<IClaimRoot> iClaimRoots1 = Sets.newHashSet();
        for (Map.Entry<IEvent, Collection<IClaimRoot>> iEventIClaimRootEntry : rootClaimsByEvent.asMap().entrySet()) {
            IClaimRoot eventRoot = new AggregatedEventClaimRoot(iEventIClaimRootEntry.getValue(), iEventIClaimRootEntry.getKey());
            aggregatedClaimByEvent.put(iEventIClaimRootEntry.getKey(), eventRoot);
            iClaimRoots1.add(eventRoot);
        }
        return iClaimRoots1;
    }

    @Override
    public Collection<ClaimCashflowPacket> cashflowsByUnderwritingPeriodUpToSimulationPeriod(Integer simPeriod, Integer uWPeriod, PeriodScope periodScope, ContractCoverBase coverBase) {
        Collection<ClaimCashflowPacket> cashflowsByUWPeriod = super.cashflowsByUnderwritingPeriodUpToSimulationPeriod(simPeriod, uWPeriod, periodScope, coverBase);
        Collection<ClaimCashflowPacket> latestCashflows = RIUtilities.latestCashflowByIncurredClaim(cashflowsByUWPeriod, IncurredClaimBase.BASE);
        SetMultimap<IEvent, ClaimCashflowPacket> cashflowClaimsByEvent = HashMultimap.create();
        for (ClaimCashflowPacket iClaimRoot : latestCashflows) {
            cashflowClaimsByEvent.put(iClaimRoot.getEvent(), iClaimRoot);
        }

        Collection<ClaimCashflowPacket> cumulativeEventCashflows = Lists.newArrayList();
        for (Map.Entry<IEvent, Collection<ClaimCashflowPacket>> cashflowPacketsForEvent : cashflowClaimsByEvent.asMap().entrySet()) {
            double cumualtiveEventCashflow = 0d;
            for (ClaimCashflowPacket claimCashflowPacket : cashflowPacketsForEvent.getValue()) {
                cumualtiveEventCashflow += claimCashflowPacket.getPaidCumulatedIndexed();
            }
            ClaimCashflowPacket claimCashflowPacket = new ClaimCashflowPacket(aggregatedClaimByEvent.get(cashflowPacketsForEvent.getKey()),
                    0d, 0d, cumualtiveEventCashflow, 0d, 0d, 0d, 0d, 0d, null, periodScope.getNextPeriodStartDate().minusDays(1), -1);
            cumulativeEventCashflows.add(claimCashflowPacket);
        }
        return cumulativeEventCashflows;
    }

    @Override
    public Set<IClaimRoot> allIncurredClaimsCurrentModelPeriodForAllocation(PeriodScope periodScope, ContractCoverBase coverBase) {
        return super.allIncurredClaimsInModelPeriod(periodScope.getCurrentPeriod(), periodScope, coverBase);
    }

    @Override
    public void cacheClaims(Collection<ClaimCashflowPacket> newClaims, Integer simulationPeriod) {
        for (ClaimCashflowPacket newClaim : newClaims) {
            if (newClaim.getEvent() == null) {
                throw new SimulationException("Covering claims in event mode. Recieved claim ; " + newClaim.toString() + " with null event. This is not allowed.");
            }
        }
        super.cacheClaims(newClaims, simulationPeriod);
    }
}
