package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase

/**
*   author simon.parten @ art-allianz . com
 */
class EventCacheClaimStoreTest extends UberCacheClaimStoreTest {

    protected void setUp() {

        IEvent event1 = new EventPacket(new DateTime(2012,1, 1, 0, 0, 0, 0))
        IEvent event2 = new EventPacket(new DateTime(2012,1, 1, 0, 0, 0, 0))

        aTestStore = new EventCacheClaimsStore()
        IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(start2010, 4)
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 100, start2010, start2010, start2010, event1) /* 50 + 50 */
        GrossClaimRoot grossClaimRoot2 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 200, start2010, start2010, start2010, event1) /* 100 + 100 */
        GrossClaimRoot grossClaimRoot3 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 100, start2010, start2010, start2010, event2) /* 50 + 50 */
        GrossClaimRoot grossClaimRoot4 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 300, start2010, start2010, start2010, event2) /* 150 + 150 */
        rooClaims.clear()

        rooClaims << grossClaimRoot1
        rooClaims << grossClaimRoot2
        rooClaims << grossClaimRoot3
        rooClaims << grossClaimRoot4

        List<ClaimCashflowPacket> allClaims = new ArrayList<ClaimCashflowPacket>()
        for (int i = 0; i < 5; i++) {
            Collection<ClaimCashflowPacket> claimsInPeriod = new ArrayList<ClaimCashflowPacket>()
            for (GrossClaimRoot aRoot in rooClaims) {
                claimsInPeriod.addAll(aRoot.getClaimCashflowPackets(counter))
            }
            allClaims.addAll(claimsInPeriod)
            aTestStore.cacheClaims(claimsInPeriod, i)
            counter.next()
        }

        grossClaims.clear()
        grossClaims.addAll(allClaims)
    }

    void testClaimRoots(){
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 5)
        Collection<IClaimRoot> aggregatedClaims = aTestStore.allIncurredClaimsInModelPeriod(0, periodScope, ContractCoverBase.LOSSES_OCCURING)
        assert aggregatedClaims.size() == 2
        assert aggregatedClaims.asList().get(0).getUltimate() == 400
        assert aggregatedClaims.asList().get(1).getUltimate() == 300
    }

    void testSafety(){
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 100, start2010, start2010, start2010)
        IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(start2010, 4)
        shouldFail {
            aTestStore.cacheClaims(grossClaimRoot1.getClaimCashflowPackets(counter), 0)
        }
    }

    void testCashflows(){
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 5)
        aTestStore.allIncurredClaimsInModelPeriod(0, periodScope, ContractCoverBase.LOSSES_OCCURING)
        Collection<ClaimCashflowPacket> cashflowPacketCollection1 = aTestStore.cashflowsByUnderwritingPeriodUpToSimulationPeriod(0, 0, periodScope, ContractCoverBase.LOSSES_OCCURING)
        Collection<ClaimCashflowPacket> cashflowPacketCollection2 = aTestStore.cashflowsByUnderwritingPeriodUpToSimulationPeriod(1, 0, periodScope, ContractCoverBase.LOSSES_OCCURING)
        assert cashflowPacketCollection1.size() == 2
        assert cashflowPacketCollection2.size() == 2
        assert cashflowPacketCollection1.asList().get(0).getPaidCumulatedIndexed() == 200
        assert cashflowPacketCollection1.asList().get(1).getPaidCumulatedIndexed() == 150
        assert cashflowPacketCollection2.asList().get(0).getPaidCumulatedIndexed() == 400
        assert cashflowPacketCollection2.asList().get(1).getPaidCumulatedIndexed() == 300

    }

}
