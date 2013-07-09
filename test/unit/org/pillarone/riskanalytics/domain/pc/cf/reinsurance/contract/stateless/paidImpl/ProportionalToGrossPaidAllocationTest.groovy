package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl

import com.google.common.collect.Lists
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IterationStore
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.*
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.global.AnnualPeriodStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AllCashflowClaimsRIOutcome
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AllClaimsRIOutcome
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimRIOutcome
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.AllContractPaidTestImpl
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumSelectionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.StatelessRIContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.TemplateContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ContractBasedOn
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
    List<ICededRoot> cededClaims = Lists.newArrayList()

    PeriodScope periodScope

    void testAllocatePaidOneClaim() {
        periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IPeriodCounter counter = periodScope.getPeriodCounter()
        /* Gross claim setup */
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i], [1d], 100d, start2010, start2010, start2010) /* 50 + 50 */
        AllClaimsRIOutcome allClaimsRIOutcome = new AllClaimsRIOutcome()
        List<GrossClaimRoot> rooClaims = new ArrayList<GrossClaimRoot>()
        rooClaims << grossClaimRoot1

        List<ClaimCashflowPacket> cashflowPacketList = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList.addAll(grossClaimRoot1.getClaimCashflowPackets(counter))

        // Assume that there are no prior ceded claims.
        CededClaimRoot cededClaimRoot = new CededClaimRoot(50d, grossClaimRoot1)
        IncurredClaimRIOutcome claimRIOutcome = new IncurredClaimRIOutcome(cededClaimRoot, cededClaimRoot, grossClaimRoot1)
        allClaimsRIOutcome.addClaim(claimRIOutcome)

//        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidThisPeriod = new HashMap<Integer, Double>()
        contractPaidThisPeriod.put(0, 50d)
        IAllContractClaimCache claimStore = new AllContractPaidTestImpl()
        ProportionalToGrossPaidAllocation allocation = new ProportionalToGrossPaidAllocation()
        AllCashflowClaimsRIOutcome cededPackets = allocation.allocatePaid(contractPaidThisPeriod, cashflowPacketList, claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, allClaimsRIOutcome, true)

        assert cededPackets.getAllCashflowOutcomes().size() == cashflowPacketList.size() + 1
        assertEquals "Check 50 ceded", 50d, cededPackets.getAllCededClaims()*.getPaidIncrementalIndexed().sum()
    }

    public static List<ClaimCashflowPacket> createCashflows(List<IClaimRoot> grossClaims, IPeriodCounter counter) {
        List<ClaimCashflowPacket> allClaims = new ArrayList<ClaimCashflowPacket>()
            for (GrossClaimRoot aRoot in grossClaims) {
                List<ClaimCashflowPacket> cashflows = aRoot.getClaimCashflowPackets(counter, null, false)
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
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i, 15i, 27i], [0.5d, 0.75d, 1d], 100d, start2010, start2010, start2010)

        List<ClaimCashflowPacket> cashflowPacketList1 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList1.addAll(grossClaimRoot1.getClaimCashflowPackets(counter, null, false))

        // Assume that there are no prior ceded claims.
        IAllContractClaimCache claimStore = new AllContractPaidTestImpl()
        AllClaimsRIOutcome incurredClaimOutcome = new AllClaimsRIOutcome();
        CededClaimRoot cededClaimRoot = new CededClaimRoot(50d, grossClaimRoot1)
        IncurredClaimRIOutcome claimRIOutcome = new IncurredClaimRIOutcome(new CededClaimRoot(0d, grossClaimRoot1), cededClaimRoot, grossClaimRoot1)
        incurredClaimOutcome.addClaim(claimRIOutcome)

//        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidPeriod1 = new HashMap<Integer, Double>()
        contractPaidPeriod1.put(0, 25d)

        ProportionalToGrossPaidAllocation allocation = new ProportionalToGrossPaidAllocation()
        AllCashflowClaimsRIOutcome cededPackets = allocation.allocatePaid(contractPaidPeriod1, cashflowPacketList1, claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, incurredClaimOutcome, true)
        claimStore.addCededIncurred([claimRIOutcome])
        assert cededPackets.getAllCashflowOutcomes().size() == cashflowPacketList1.size() + 1
        claimStore.addCededPackets(cededPackets.getAllCededClaims())

        periodScope.prepareNextPeriod()
        AllClaimsRIOutcome outComeP2 = new AllClaimsRIOutcome()
        List<ClaimCashflowPacket> cashflowPacketList2 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList2.addAll(grossClaimRoot1.getClaimCashflowPackets(counter, null, false))

        // Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidPeriod2 = new HashMap<Integer, Double>()
        contractPaidPeriod2.put(0, 12.5d)
        contractPaidPeriod2.put(1, 0d)

        List<ClaimCashflowPacket> cededPacketsPeriod2 = allocation.allocatePaid(contractPaidPeriod2, cashflowPacketList2, claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, outComeP2, true).getAllCededClaims()
        assert cededPacketsPeriod2.size() == cashflowPacketList2.size()
        assertEquals("Check cumulated amount : ", 37.5d, cededPacketsPeriod2.get(1).getPaidCumulatedIndexed() )
        claimStore.addCededPackets(cededPacketsPeriod2)

        periodScope.prepareNextPeriod()
        List<ClaimCashflowPacket> cashflowPacketList3 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList3.addAll(grossClaimRoot1.getClaimCashflowPackets(counter, null, false))

        //        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidPeriod3 = new HashMap<Integer, Double>()
        contractPaidPeriod3.put(0, 100d)
        contractPaidPeriod3.put(1, 0d)
        contractPaidPeriod3.put(2, 0d)

        contractPaidPeriod3.put(0, 12.5d)
        AllCashflowClaimsRIOutcome cededPacketsPeriod3 = allocation.allocatePaid(contractPaidPeriod3, cashflowPacketList3, claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, outComeP2, true)
        assertEquals("Check cumulated amount : ", 50, cededPacketsPeriod3.getAllCededClaims().get(1).getPaidCumulatedIndexed())
    }


    void testAllocatePaidThreeClaims() {
        periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IPeriodCounter counter = periodScope.getPeriodCounter()
        /* Gross claim setup */
        GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i], [1d], 100d, start2010, start2010, start2010) /* 100 */
        GrossClaimRoot grossClaimRoot2 = TestClaimUtils.getGrossClaim([6i, 7i], [0.5d, 1d], 120d, start2010, start2010, start2010) /* 60+60 */ /* All paid P1 */
        GrossClaimRoot grossClaimRoot3 = TestClaimUtils.getGrossClaim([7i, 15i], [0.5d, 1d], 100d, start2010, start2010, start2010) /* 50 + 50 */
        GrossClaimRoot grossClaimRoot4 = TestClaimUtils.getGrossClaim([5i], [1d], 100d, start2010, start2011, start2011) /* 100 */

        /* Gross Incurred amount P1 : 300 */ /* Ceded incurred loss by claim: 10d, 30d, 10d */
        /* Contract p1 individual XOL 40 XS 90 */
        /* Contract should pay 40  ceded in p1 */

        /* Gross Incurred amount P2 : 100. Paid immediately. */
        IAllContractClaimCache claimStore = new AllContractPaidTestImpl()
        List<IClaimRoot> rootClaims = new ArrayList<IClaimRoot>()
        rootClaims << grossClaimRoot1 << grossClaimRoot2 << grossClaimRoot3

        List<ClaimCashflowPacket> cashflowPacketList = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList.addAll(createCashflows(rootClaims, periodScope.getPeriodCounter()))

        CededClaimRoot cededClaimRoot1 = new CededClaimRoot(50d * 100 / 320, grossClaimRoot1, ClaimType.CEDED)
        CededClaimRoot cededClaimRoot2 = new CededClaimRoot(50d * 120 / 320, grossClaimRoot2, ClaimType.CEDED)
        CededClaimRoot cededClaimRoot3 = new CededClaimRoot(50d * 100 / 320, grossClaimRoot3, ClaimType.CEDED)

        CededClaimRoot netClaimRoot1 = new CededClaimRoot(50d * 200 / 320, grossClaimRoot1, ClaimType.NET)
        CededClaimRoot netClaimRoot2 = new CededClaimRoot(50d * 200 / 320, grossClaimRoot2, ClaimType.NET)
        CededClaimRoot netClaimRoot3 = new CededClaimRoot(50d * 200 / 320, grossClaimRoot3, ClaimType.NET)

        AllClaimsRIOutcome allClaimsRIOutcome = new AllClaimsRIOutcome()
        IncurredClaimRIOutcome claimRIOutcome1 = new IncurredClaimRIOutcome(netClaimRoot1, cededClaimRoot1, grossClaimRoot1)
        IncurredClaimRIOutcome claimRIOutcome2 = new IncurredClaimRIOutcome(netClaimRoot2, cededClaimRoot2, grossClaimRoot2)
        IncurredClaimRIOutcome claimRIOutcome3 = new IncurredClaimRIOutcome(netClaimRoot3, cededClaimRoot3, grossClaimRoot3)
        allClaimsRIOutcome.addClaim(claimRIOutcome1)
        allClaimsRIOutcome.addClaim(claimRIOutcome2)
        allClaimsRIOutcome.addClaim(claimRIOutcome3)


//        Allocation independant of incurred strategy, and paid amount, only contract paid is needed.
        Map<Integer, Double> contractPaidThisPeriod = new HashMap<Integer, Double>()
        double cededPaid = 40d
        double netPaid = 100d + 60d + 60d + 50d - 40d
        contractPaidThisPeriod.put(0, cededPaid)

        ProportionalToGrossPaidAllocation allocation = new ProportionalToGrossPaidAllocation()
        AllCashflowClaimsRIOutcome cededPackets1 = allocation.allocatePaid(contractPaidThisPeriod, cashflowPacketList, claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, allClaimsRIOutcome, true)
        claimStore.addCededIncurred([claimRIOutcome1, claimRIOutcome2, claimRIOutcome3])

        assert cededPackets1.allCashflowOutcomes.size() == cashflowPacketList.size() + rootClaims.size()


        assertEquals "Check 40 ceded", cededPaid, cededPackets1.getAllCededClaims()*.getPaidIncrementalIndexed().sum()
        assertEquals "Check remaining is net", netPaid, cededPackets1.getAllNetClaims()*.getPaidIncrementalIndexed().sum()
        claimStore.addCededPackets(cededPackets1.allCededClaims)

        Map<Integer, Double> contractPaidPeriod1 = new HashMap<Integer, Double>()
        double p0CededPaid = 10d
        double p0NetPaid = 50d - 10d
        contractPaidPeriod1.put(0, p0CededPaid)
        contractPaidPeriod1.put(1, 20d)

        final AllClaimsRIOutcome incurredOutcome = new AllClaimsRIOutcome()
        CededClaimRoot cededClaimRoot4 = new CededClaimRoot(80d, grossClaimRoot4)
        CededClaimRoot netClaimRoot4 = new CededClaimRoot(20d, grossClaimRoot4)
        IncurredClaimRIOutcome claimRIOutcome = new IncurredClaimRIOutcome(netClaimRoot4, cededClaimRoot4, grossClaimRoot4)
        incurredOutcome.addClaim(claimRIOutcome)

        periodScope.prepareNextPeriod()
        rootClaims.add(grossClaimRoot4)
        List<ClaimCashflowPacket> cashflowPacketList2 = new ArrayList<ClaimCashflowPacket>()
        cashflowPacketList2.addAll(createCashflows(rootClaims, periodScope.getPeriodCounter()))
        List<ClaimCashflowPacket> cededPackets2 = allocation.allocatePaid(contractPaidPeriod1, cashflowPacketList2,
                claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, incurredOutcome, true).getAllCededClaims()

        List<ClaimCashflowPacket> netPackets2 = allocation.allocatePaid(contractPaidPeriod1, cashflowPacketList2,
                claimStore, periodScope, ContractCoverBase.LOSSES_OCCURING, incurredOutcome, true).getAllNetClaims()

        List<ClaimCashflowPacket> cededClaimsP0 = RIUtilities.cashflowsClaimsByPeriod(0, periodScope.getPeriodCounter(), cededPackets2, ContractCoverBase.LOSSES_OCCURING)
        List<ClaimCashflowPacket> cededClaimsP1 = RIUtilities.cashflowsClaimsByPeriod(1, periodScope.getPeriodCounter(), cededPackets2, ContractCoverBase.LOSSES_OCCURING)

        List<ClaimCashflowPacket> netClaimsP0 = RIUtilities.cashflowsClaimsByPeriod(0, periodScope.getPeriodCounter(), netPackets2, ContractCoverBase.LOSSES_OCCURING)
        List<ClaimCashflowPacket> netClaimsP1 = RIUtilities.cashflowsClaimsByPeriod(1, periodScope.getPeriodCounter(), netPackets2, ContractCoverBase.LOSSES_OCCURING)

        assert p0CededPaid == cededClaimsP0*.getPaidIncrementalIndexed().sum()
        assert 20d == cededClaimsP1*.getPaidIncrementalIndexed().sum()

        assert p0NetPaid == netClaimsP0*.getPaidIncrementalIndexed().sum()
        assert 80d == netClaimsP1*.getPaidIncrementalIndexed().sum()
    }

    void testStacking(){
        ConstraintsFactory.registerConstraint(new AdditionalPremiumConstraints())
        ConstraintsFactory.registerConstraint(new LayerConstraints())
        ConstraintsFactory.registerConstraint(new ContractBasedOn())


        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(start2010, 3)
        periodScope = iterationScope.getPeriodScope()
        IPeriodCounter counter = iterationScope.getPeriodScope().getPeriodCounter()
        /* Setup contracts */
        StatelessRIContract nonUSContract = getArtLayerContract(0d, Double.MAX_VALUE, [1], [1], [1], [0d], [Double.MAX_VALUE],  [10], [230], start2010, [0d], ["NONE"], iterationScope   )
        StatelessRIContract usContract = getArtLayerContract(0d, Double.MAX_VALUE, [1], [1], [1], [0d], [Double.MAX_VALUE],  [60], [245], start2010, [0d], ["NONE"], iterationScope   )
        StatelessRIContract aggContract = getArtLayerContract(0d, Double.MAX_VALUE, [1], [1], [1], [400d], [350d],  [0], [0], start2010, [0d], ["NONE"], iterationScope   )
        usContract.periodStore = new PeriodStore(periodScope)
        nonUSContract.periodStore = new PeriodStore(periodScope)
        aggContract.periodStore = new PeriodStore(periodScope)

        GrossClaimRoot nonUSClaim1 = TestClaimUtils.getGrossClaim([24i - 1], [1d], 1000, start2010, start2010, start2010) /* 100 */
        GrossClaimRoot nonUSClaim2 = TestClaimUtils.getGrossClaim([48i - 1], [1d], 10d, start2010, start2010, start2010) /* 100 */
        GrossClaimRoot USClaim1 = TestClaimUtils.getGrossClaim([12i - 1], [1d], 400, start2010, start2010, start2010) /* 100 */
        GrossClaimRoot USClaim2 = TestClaimUtils.getGrossClaim([36i - 1], [1d], 60.5d, start2010, start2010, start2010) /* 100 */

        List<IClaimRoot> usClaims = [USClaim1, USClaim2]
        List<IClaimRoot> nonUSClaims = [nonUSClaim1, nonUSClaim2]
        List<ClaimCashflowPacket> usCashflow = createCashflows(usClaims, counter)
        List<ClaimCashflowPacket> nonUSCashflow = createCashflows(nonUSClaims, counter)

        /* PERIOD 1 */
        usContract.inClaims.addAll( usCashflow )
        usContract.doCalculation()
        List<ClaimCashflowPacket> usCededClaimsP1 = usContract.outClaimsCeded
        assertEquals "", usCededClaimsP1*.getPaidIncrementalIndexed().sum(), 245 , 0.01d

        nonUSContract.inClaims.addAll( nonUSCashflow )
        nonUSContract.doCalculation()
        List<ClaimCashflowPacket> nonUSCededClaimsP1 = nonUSContract.outClaimsCeded
        assert nonUSCededClaimsP1*.getPaidIncrementalIndexed().sum() == 0d

        aggContract.inClaims.addAll( usCededClaimsP1 )
        aggContract.inClaims.addAll( nonUSCededClaimsP1 )
        aggContract.doCalculation()
        List<ClaimCashflowPacket> aggContractCededP1 = aggContract.outClaimsCeded
        assert aggContractCededP1*.getPaidIncrementalIndexed().sum() == 0d
        assertEquals "", aggContractCededP1*.ultimate().sum(), 75.5 , 0.01d

        /* PERIOD 2 */
        iterationScope.getPeriodScope().prepareNextPeriod()
        List<ClaimCashflowPacket> usCashflow2 = createCashflows(usClaims, counter)
        List<ClaimCashflowPacket> nonUSCashflow2 = createCashflows(nonUSClaims, counter)
        usContract.reset()
        usContract.inClaims.addAll( usCashflow2 )
        usContract.doCalculation()
        List<ClaimCashflowPacket> usCededClaimsP2 = usContract.outClaimsCeded
        assert usCededClaimsP2*.getPaidIncrementalIndexed().sum() == 0d

        nonUSContract.reset()
        nonUSContract.inClaims.addAll( nonUSCashflow2 )
        nonUSContract.doCalculation()
        List<ClaimCashflowPacket> nonUSCededClaimsP2 = nonUSContract.outClaimsCeded
        assert nonUSCededClaimsP2*.getPaidIncrementalIndexed().sum() == 230d

        aggContract.reset()
        aggContract.inClaims.addAll( usCededClaimsP2 )
        aggContract.inClaims.addAll( nonUSCededClaimsP2 )
        aggContract.doCalculation()
        List<ClaimCashflowPacket> aggContractCededP2 = aggContract.outClaimsCeded
        assertEquals( "", aggContractCededP2*.getPaidIncrementalIndexed().sum(), 75d, 0.01d )
    }

    static StatelessRIContract getArtLayerContract(
            Double termExcess, Double termLimit, List<Integer> periods, List<Integer> layers, List<Double> shares, List<Double> periodExcess, List<Double> periodLimit,
            List<Double> claimExcess, List<Double> claimLimit, DateTime beginOfCover, List<Double> apPercentages, List<String> apTypes, IterationScope iterationScope ) {


        ConstraintsFactory.registerConstraint(new PremiumSelectionConstraints())
        int numberOfLayers = layers.size()
        return new StatelessRIContract(
                parmContractStructure: TemplateContractType.getStrategy(
                        TemplateContractType.NONPROPORTIONAL,
                        ['termLimit': termLimit,
                                'termExcess': termExcess,
                                'termAP': new ConstrainedMultiDimensionalParameter(
                                        [[0d], [0d], [0d]], AdditionalPremiumConstraints.columnHeaders,
                                        ConstraintsFactory.getConstraints(AdditionalPremiumConstraints.IDENTIFIER)),
                                'structure': new ConstrainedMultiDimensionalParameter(
                                        [
                                                periods, layers, shares, periodLimit, periodExcess, claimLimit, claimExcess, apPercentages, apTypes
                                        ],
                                        LayerConstraints.columnHeaders, ConstraintsFactory.getConstraints(LayerConstraints.IDENTIFIER))]),
                iterationScope: iterationScope,
                iterationStore: new IterationStore(iterationScope),
                periodScope: iterationScope.getPeriodScope(),
                globalCover: new AnnualPeriodStrategy(startCover: beginOfCover),
                periodStore: iterationScope.periodStores[0],
                parmPremiumCover: new ConstrainedMultiDimensionalParameter(GroovyUtils.toList("[]"),
                        Arrays.asList(PremiumSelectionConstraints.PREMIUM_TITLE), ConstraintsFactory.getConstraints(PremiumSelectionConstraints.IDENTIFIER))
        )
    }



    void setUp() {


    }
}
