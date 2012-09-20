package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities

import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter

import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities

/**
*   author simon.parten @ art-allianz . com
 */
class ProportionalToGrossPaidAllocationTest extends GroovyTestCase {

    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0)
    DateTime start2011 = new DateTime(2011, 1, 1, 1, 0, 0, 0)

    Map<Integer, Double> incrementalPaidByPeriod = new HashMap<Integer, Double>()
    List<ClaimCashflowPacket> grossCashflowsThisPeriod = new ArrayList<ClaimCashflowPacket>()
    List<ClaimCashflowPacket> cededCashflowsToDate = new ArrayList<ClaimCashflowPacket>()
    List<ICededRoot> cededClaims = new ArrayList<ICededRoot>()

    PeriodScope periodScope

    void testAllocatePaidOneClaim() {
        periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IPeriodCounter counter = periodScope.getPeriodCounter()
        /* Gross claim setup */
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i], [1d], -100d, start2010, start2010, start2010) /* 50 + 50 */

        List<GrossClaimRoot> rooClaims = new ArrayList<GrossClaimRoot>()
        rooClaims << grossClaimRoot1

        List<ClaimCashflowPacket> cashflowPacketList = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList.addAll(grossClaimRoot1.getClaimCashflowPackets(counter))

        // Assume that there are no prior ceded claims.
        List<ClaimCashflowPacket> cededClaimsToDate = new ArrayList<ClaimCashflowPacket>()
        List<ICededRoot> cededClaims = new ArrayList<ICededRoot>()
        CededClaimRoot cededClaimRoot = new CededClaimRoot(-50d, grossClaimRoot1)
        cededClaims.add(cededClaimRoot)

//        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidThisPeriod = new HashMap<Integer, Double>()
        contractPaidThisPeriod.put(0, 50d)

        ProportionalToGrossPaidAllocation allocation = new ProportionalToGrossPaidAllocation()
        List<ClaimCashflowPacket> cededPackets = allocation.allocatePaid(contractPaidThisPeriod, cashflowPacketList, cededClaimsToDate, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)

        assert cededPackets.size() == cashflowPacketList.size()
        assertEquals "Check 50 ceded", -50d, cededPackets*.getPaidIncrementalIndexed().sum()
        for (int i = 0; i < cededPackets.size(); i++) {
           assertEquals("check dates : ", cededPackets.get(i).getDate(), cashflowPacketList.get(i).getDate())
           assertEquals("check severity : ",cededPackets.get(i).getPaidIncrementalIndexed(), cashflowPacketList.get(i).getPaidIncrementalIndexed() * 0.5, 0.000001)
        }
    }

    public static List<ClaimCashflowPacket> createCashflows(List<IClaimRoot> grossClaims, IPeriodCounter counter) {
        List<ClaimCashflowPacket> allClaims = new ArrayList<ClaimCashflowPacket>()
            for (GrossClaimRoot aRoot in grossClaims) {
                List<ClaimCashflowPacket> cashflows = aRoot.getClaimCashflowPackets(counter)
                if(cashflows.size() == 0){
                    cashflows.add(aRoot.zeroPaidIncrement(counter))
                }
                allClaims.addAll(cashflows)
            }
        return allClaims
    }


    void testAllocatePaidWithPriorCeded() {
        periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IPeriodCounter counter = periodScope.getPeriodCounter()
        /* Gross claim setup */
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i, 15i, 27i], [0.5d, 0.75d, 1d], -100d, start2010, start2010, start2010)

        List<ClaimCashflowPacket> cashflowPacketList1 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList1.addAll(grossClaimRoot1.getClaimCashflowPackets(counter))

        // Assume that there are no prior ceded claims.
        List<ClaimCashflowPacket> cededClaimsToDate = new ArrayList<ClaimCashflowPacket>()
        List<ICededRoot> cededClaims = new ArrayList<ICededRoot>()
        CededClaimRoot cededClaimRoot = new CededClaimRoot(-50d, grossClaimRoot1)
        cededClaims.add(cededClaimRoot)

//        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidPeriod1 = new HashMap<Integer, Double>()
        contractPaidPeriod1.put(0, 25d)

        ProportionalToGrossPaidAllocation allocation = new ProportionalToGrossPaidAllocation()
        List<ClaimCashflowPacket> cededPackets = allocation.allocatePaid(contractPaidPeriod1, cashflowPacketList1, cededClaimsToDate, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)

        assert cededPackets.size() == cashflowPacketList1.size()
        ArrayList<ClaimCashflowPacket> cededCashflows = new ArrayList<ClaimCashflowPacket>()
        cededCashflows.addAll(cededPackets)

        periodScope.prepareNextPeriod()
        List<ClaimCashflowPacket> cashflowPacketList2 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList2.addAll(grossClaimRoot1.getClaimCashflowPackets(counter))

        //        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidPeriod2 = new HashMap<Integer, Double>()
        contractPaidPeriod2.put(0, 12.5d)
        contractPaidPeriod2.put(1, 0d)

        List<ClaimCashflowPacket> cededPacketsPeriod2 = allocation.allocatePaid(contractPaidPeriod2, cashflowPacketList2, cededCashflows, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)
        assert cededPacketsPeriod2.size() == cashflowPacketList2.size()
        assertEquals("Check cumulated amount : ", -37.5d, cededPacketsPeriod2.get(0).getPaidCumulatedIndexed() )
        cededCashflows.addAll(cededPacketsPeriod2)


        periodScope.prepareNextPeriod()
        List<ClaimCashflowPacket> cashflowPacketList3 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList3.addAll(grossClaimRoot1.getClaimCashflowPackets(counter))

        //        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidPeriod3 = new HashMap<Integer, Double>()
        contractPaidPeriod3.put(0, 100d)
        contractPaidPeriod3.put(1, 0d)
        contractPaidPeriod3.put(2, 0d)
        shouldFail {
            /* Expect an exception here because the claim will pay more than the Root ultimate amount.  */
            List<ClaimCashflowPacket> cededPacketsPeriod3 = allocation.allocatePaid(contractPaidPeriod3, cashflowPacketList3, cededCashflows, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)
        }

        contractPaidPeriod3.put(0, 12.5d)
        List<ClaimCashflowPacket> cededPacketsPeriod3 = allocation.allocatePaid(contractPaidPeriod3, cashflowPacketList3, cededCashflows, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)
        assertEquals("Check cumulated amount : ", -50, cededPacketsPeriod3.get(0).getPaidCumulatedIndexed())
    }


    void testAllocatePaidThreeClaims() {
        periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IPeriodCounter counter = periodScope.getPeriodCounter()
        /* Gross claim setup */
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i], [1d], -100d, start2010, start2010, start2010) /* 100 */
        GrossClaimRoot grossClaimRoot2 = TestClaimUtils.getGrossClaim([6i, 7i], [0.5d, 1d], -120d, start2010, start2010, start2010) /* 60+60 */
        GrossClaimRoot grossClaimRoot3 = TestClaimUtils.getGrossClaim([7i, 15i], [0.5d, 1d], -100d, start2010, start2010, start2010) /* 50 + 50 */
        GrossClaimRoot grossClaimRoot4 = TestClaimUtils.getGrossClaim([5i], [1d], -100d, start2010, start2011, start2011) /* 100 */

        /* Gross Incurred amount P1 : 300 */ /* Ceded incurred loss by claim: 10d, 30d, 10d */

        /* Gross Incurred amount P2 : 100 */
        List<ClaimCashflowPacket> cededClaimsToDate = new ArrayList<ClaimCashflowPacket>()
        List<IClaimRoot> rootClaims = new ArrayList<IClaimRoot>()
        rootClaims << grossClaimRoot1 << grossClaimRoot2 << grossClaimRoot3

        List<ClaimCashflowPacket> cashflowPacketList = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList.addAll(createCashflows(rootClaims, periodScope.getPeriodCounter()))

        List<ICededRoot> cededClaims = new ArrayList<ICededRoot>()
        CededClaimRoot cededClaimRoot1 = new CededClaimRoot(-50d * 100 / 320, grossClaimRoot1)
        CededClaimRoot cededClaimRoot2 = new CededClaimRoot(-50d * 120 / 320, grossClaimRoot2)
        CededClaimRoot cededClaimRoot3 = new CededClaimRoot(-50d * 100 / 320, grossClaimRoot3)
        cededClaims.add(cededClaimRoot1)
        cededClaims.add(cededClaimRoot2)
        cededClaims.add(cededClaimRoot3)

//        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidThisPeriod = new HashMap<Integer, Double>()
        contractPaidThisPeriod.put(0, 40d)

        ProportionalToGrossPaidAllocation allocation = new ProportionalToGrossPaidAllocation()
        List<ClaimCashflowPacket> cededPackets1 = allocation.allocatePaid(contractPaidThisPeriod, cashflowPacketList, cededClaimsToDate, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)


        assert cededPackets1.size() == cashflowPacketList.size()
        assertEquals "Check 40 ceded", -40d, cededPackets1*.getPaidIncrementalIndexed().sum()
        cededClaimsToDate.addAll(cededPackets1)

        Map<Integer, Double> contractPaidPeriod1 = new HashMap<Integer, Double>()
        contractPaidPeriod1.put(0, 10d)
        contractPaidPeriod1.put(1, 80d)

        CededClaimRoot cededClaimRoot4 = new CededClaimRoot(-80d, grossClaimRoot4)
        cededClaims.add(cededClaimRoot4)

        periodScope.prepareNextPeriod()
        rootClaims.add(grossClaimRoot4)
        List<ClaimCashflowPacket> cashflowPacketList2 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList2.addAll(createCashflows(rootClaims, periodScope.getPeriodCounter()))
        List<ClaimCashflowPacket> cededPackets2 = allocation.allocatePaid(contractPaidPeriod1, cashflowPacketList2,
                cededClaimsToDate, periodScope, ContractCoverBase.LOSSES_OCCURING, cededClaims, true)

        List<ClaimCashflowPacket> cededClaimsP0 = RIUtilities.cashflowsClaimsByPeriod(0, periodScope.getPeriodCounter(), cededPackets2, ContractCoverBase.LOSSES_OCCURING)
        List<ClaimCashflowPacket> cededClaimsP1 = RIUtilities.cashflowsClaimsByPeriod(1, periodScope.getPeriodCounter(), cededPackets2, ContractCoverBase.LOSSES_OCCURING)

        assertEquals "", -10d, cededClaimsP0*.getPaidIncrementalIndexed().sum()
        assertEquals "", -80d, cededClaimsP1*.getPaidIncrementalIndexed().sum()


    }


    void setUp() {


    }
}
