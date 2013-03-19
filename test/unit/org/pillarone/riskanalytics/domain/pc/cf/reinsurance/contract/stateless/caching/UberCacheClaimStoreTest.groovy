package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase

/**
*   author simon.parten @ art-allianz . com
 */
class UberCacheClaimStoreTest extends GroovyTestCase {

    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    DateTime start2011 = new DateTime(2011, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    DateTime start2012 = new DateTime(2012, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    List<ClaimCashflowPacket> grossClaims = new ArrayList<ClaimCashflowPacket>()
    Set<IClaimRoot> rooClaims = new ArrayList<GrossClaimRoot>()
    UberCacheClaimStore aTestStore

    protected void setUp() {
        aTestStore = new UberCacheClaimStore()
        IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(start2010, 4)
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 100, start2010, start2010, start2010) /* 50 + 50 */
        GrossClaimRoot grossClaimRoot2 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 200, start2010, start2010, start2010) /* 100 + 100 */
        GrossClaimRoot grossClaimRoot3 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 100, start2010, start2011, start2011) /* 50 + 50 */
        GrossClaimRoot grossClaimRoot4 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 300, start2010, start2011, start2011) /* 150 + 150 */
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

    void testInitialState(){
        assertEquals "", 0, aTestStore.incurredClaimsByUWPeriod.size()
        assertEquals "", 0, aTestStore.cashflowsByUWPeriod.size()
        assertEquals "", 0, aTestStore.incurredClaimsBySimPeriod.size()
        assertEquals "", 0, aTestStore.simPeriodUwPeriodClaims.size()
    }

    void testAllClaimCashflowPacketsInModelPeriod() {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 5)
        Collection<ClaimCashflowPacket> cashflowPacketCollection = aTestStore.allClaimCashflowPacketsInModelPeriod(0, periodScope, ContractCoverBase.LOSSES_OCCURING )
        assertEquals "", 1, aTestStore.cashflowsByUWPeriod.size()
        assertNotNull aTestStore.cashflowsByUWPeriod.get(0)
        assertEquals "", cashflowPacketCollection, aTestStore.allClaimCashflowPacketsInModelPeriod(0, periodScope, ContractCoverBase.LOSSES_OCCURING )
//      It's important the cache is cleaned out when new claims are potentially added !
        aTestStore.cacheClaims(new ArrayList<ClaimCashflowPacket>(), 6)
        assertEquals "", 0, aTestStore.cashflowsByUWPeriod.size()
    }

    void testAllIncurredClaimsInModelPeriod() {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 5)
        Collection<IClaimRoot> rootCollection = aTestStore.allIncurredClaimsInModelPeriod(0, periodScope, ContractCoverBase.LOSSES_OCCURING )
        assertNotNull aTestStore.incurredClaimsByUWPeriod.get(0)
        assertEquals "", 1, aTestStore.incurredClaimsByUWPeriod.size()
        assertEquals "", rootCollection, aTestStore.allIncurredClaimsInModelPeriod(0, periodScope, ContractCoverBase.LOSSES_OCCURING )
//      It's important the cache is cleaned out when new claims are potentially added !
        aTestStore.cacheClaims(new ArrayList<ClaimCashflowPacket>(), 6)
        assertEquals "", 0, aTestStore.incurredClaimsByUWPeriod.size()
    }

    /* Test of basic functionality. If this breaks we've had it!!! */
    void testCacheClaims() {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 5)
        UberCacheClaimStore testStore =  new UberCacheClaimStore()
        Collection<ClaimCashflowPacket> somClaims = grossClaims.findAll {it -> it.getDate().isBefore(start2011)}
        testStore.cacheClaims(somClaims, 0)
        assertEquals "", 2 , testStore.allIncurredClaims().size()
        assertEquals("", 4, testStore.allClaimCashflowPackets().size())
        assertEquals("" , 4, testStore.allCashflowClaimsUpToSimulationPeriod(0,periodScope, ContractCoverBase.LOSSES_OCCURING ).size())

        Collection<ClaimCashflowPacket> someMoreClaims = grossClaims.findAll {it -> (it.getDate().isBefore(start2012)) && (it.getDate().isAfter(start2011) || it.getDate().isEqual(start2011) )}
        periodScope.prepareNextPeriod()
        testStore.cacheClaims(someMoreClaims, 1)
        assertEquals "", 4 , testStore.allIncurredClaims().size()
        assertEquals("" , 6, testStore.allClaimCashflowPacketsInSimulationPeriod(1, periodScope, ContractCoverBase.LOSSES_OCCURING).size())
        assertEquals("", 10, testStore.allClaimCashflowPackets().size() )
        assertEquals("" , 10, testStore.allCashflowClaimsUpToSimulationPeriod(1, periodScope, ContractCoverBase.LOSSES_OCCURING).size())

        assertEquals("", 4, testStore.incurredClaimsByKey().size())
    }

    void testCashflowsByUnderwritingPeriodUpToSimulationPeriod() {
        PeriodScope  periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 5)
        Collection<ClaimCashflowPacket> cashflowPackets = aTestStore.cashflowsByUnderwritingPeriodUpToSimulationPeriod(0, 0, periodScope, ContractCoverBase.LOSSES_OCCURING)
        assertEquals("", 4 , cashflowPackets.size() )
        assertEquals("", 4, aTestStore.simPeriodUwPeriodClaims.get(0).get(0).size() )
        Collection<ClaimCashflowPacket> morePackets = aTestStore.cashflowsByUnderwritingPeriodUpToSimulationPeriod(1, 0, periodScope, ContractCoverBase.LOSSES_OCCURING)
        assertEquals("", 6, morePackets.size())
        assertEquals("", 4, aTestStore.simPeriodUwPeriodClaims.get(1).get(1).size() )
    }
}
