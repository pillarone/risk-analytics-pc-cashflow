package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidCalc

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IPaidCalculation
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl.TermPaidRespectIncurredByClaim
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.APBasis

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.AllContractPaidTestImpl
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.ContractClaimStoreTestIncurredClaimImpl

import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities

/**
 *   author simon.parten @ art-allianz . com
 */
class PaidCalcTest extends GroovyTestCase {

    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    DateTime start2011 = new DateTime(2011, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    DateTime start2012 = new DateTime(2012, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    List<ClaimCashflowPacket> grossClaims = new ArrayList<ClaimCashflowPacket>()
    Set<IClaimRoot> rooClaims = new ArrayList<GrossClaimRoot>()
    IAllContractClaimCache cache

    protected void setUp() {

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
            for (GrossClaimRoot aRoot in rooClaims) {
                allClaims.addAll(aRoot.getClaimCashflowPackets(counter))
            }
            counter.next()
        }

        grossClaims.clear()
        grossClaims.addAll(allClaims)

    }

    void testLayerCededPaid() {

        IPaidCalculation calculation = new TermPaidRespectIncurredByClaim()
        List<ClaimCashflowPacket> period1Claims = RIUtilities.cashflowsByIncurredDate(start2010, start2011.minusMillis(1), grossClaims, ContractCoverBase.LOSSES_OCCURING)

        List<ClaimCashflowPacket> latestCashflows = RIUtilities.latestCashflowByIncurredClaim(period1Claims, IncurredClaimBase.BASE)
        LayerParameters periodLimitAndDeductible = new LayerParameters(1, 60, 90)
        periodLimitAndDeductible.addAdditionalPremium(10, 0, 0, APBasis.LOSS)
        double cededPaid = calculation.layerCededPaid(latestCashflows, periodLimitAndDeductible)
        assert cededPaid == 40 + 90 - 10

    }

    void testCumulativePaidRespectTerm() {
        IPaidCalculation calculation = new TermPaidRespectIncurredByClaim()
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        List<ClaimCashflowPacket> period1Claims = RIUtilities.cashflowsByIncurredDate(start2010, start2011.minusMillis(1), grossClaims, ContractCoverBase.LOSSES_OCCURING)
        Collection<ClaimCashflowPacket> period1Claims2010 = period1Claims.findAll {it -> it.getDate().isBefore(start2011) }
        AllContractPaidTestImpl claimCache = new AllContractPaidTestImpl(period1Claims2010)
        claimCache.updateSimPeriodUWPeriodCashflowMap(0, 0, period1Claims2010)

        ScaledPeriodLayerParameters allLayers = new ScaledPeriodLayerParameters()
        allLayers.setExposureBase(ExposureBase.ABSOLUTE)
        allLayers.setCounter(periodScope.getPeriodCounter())
        allLayers.setUwInfo(new AllPeriodUnderwritingInfoPacket())
        allLayers.add(0, 1, 1, 60, 90, 0, 0, 0, APBasis.LOSS)
        Map<Integer, Double> period1Calc = calculation.cededCumulativePaidRespectTerm(periodScope.getCurrentPeriod(),
                allLayers, periodScope, ContractCoverBase.LOSSES_OCCURING, 240, 10, claimCache, ContractCoverBase.LOSSES_OCCURING)
//        40 ceded out of claim limit, minus 10 term ded
        assertEquals("p1", 40, period1Calc.get(0))

        periodScope.prepareNextPeriod()
        List<ClaimCashflowPacket> period1and2Claims = RIUtilities.cashflowsByIncurredDate(start2010, start2012.minusMillis(1), grossClaims, ContractCoverBase.LOSSES_OCCURING)
        List<ClaimCashflowPacket> period1and2ClaimsBefore2012 = period1and2Claims.findAll {it -> it.getDate().isBefore(start2012) }
        Collection<ClaimCashflowPacket> cashflows1 = GRIUtilities.cashflowsCoveredInModelPeriod(period1and2Claims, periodScope, ContractCoverBase.LOSSES_OCCURING, 0).findAll { it -> it.getDate().isBefore(start2012)};
        Collection<ClaimCashflowPacket> cashflows2 = GRIUtilities.cashflowsCoveredInModelPeriod(period1and2Claims, periodScope, ContractCoverBase.LOSSES_OCCURING, 1).findAll { it -> it.getDate().isBefore(start2012)};

        AllContractPaidTestImpl claimCache1 = new AllContractPaidTestImpl(period1and2ClaimsBefore2012)
        claimCache1.updateSimPeriodUWPeriodCashflowMap(1, 0, cashflows1)
        claimCache1.updateSimPeriodUWPeriodCashflowMap(1, 1, cashflows2)
        Map<Integer, Double> period2Calc = calculation.cededCumulativePaidRespectTerm(periodScope.getCurrentPeriod(), allLayers, periodScope, ContractCoverBase.LOSSES_OCCURING, 240, 10, claimCache1, ContractCoverBase.LOSSES_OCCURING)
        assertEquals("p1", 120 , period2Calc.get(0) )
        assertEquals("p2", 90 , period2Calc.get(1) )

        periodScope.prepareNextPeriod()
        ContractClaimStoreTestIncurredClaimImpl claimCache2 = new ContractClaimStoreTestIncurredClaimImpl( rooClaims, grossClaims)
        Map<Integer, Double> p3Calc = calculation.cededCumulativePaidRespectTerm(periodScope.getCurrentPeriod(), allLayers, periodScope, ContractCoverBase.LOSSES_OCCURING, 240, 10, claimCache2, ContractCoverBase.LOSSES_OCCURING)
        assertEquals("p1", 120 , p3Calc.get(0) )
        assertEquals("p2", 120 , p3Calc.get(1) )

        GrossClaimRoot shouldCede0 = TestClaimUtils.getGrossClaim([5i, 13i], [0.5d, 1d], 100, start2011, start2012, start2012) /* 50 + 50 */
        periodScope.prepareNextPeriod()
        grossClaims.addAll( shouldCede0.getClaimCashflowPackets(periodScope.getPeriodCounter()) )
        ContractClaimStoreTestIncurredClaimImpl claimCache3 = new ContractClaimStoreTestIncurredClaimImpl( rooClaims, grossClaims)
        Map<Integer, Double> p4Calc = calculation.cededCumulativePaidRespectTerm(periodScope.getCurrentPeriod(), allLayers, periodScope, ContractCoverBase.LOSSES_OCCURING, 240, 10, claimCache3, ContractCoverBase.LOSSES_OCCURING)
        assertEquals("p1", 120 , p4Calc.get(0) )
        assertEquals("p2", 120 , p4Calc.get(1) )
        assertEquals("p3", 0 , p4Calc.get(2) )
    }

    void testPaidLossAllLayers() {
        IPaidCalculation calculation = new TermPaidRespectIncurredByClaim()
        List<ClaimCashflowPacket> period1Claims = RIUtilities.cashflowsByIncurredDate(start2010, start2011.minusMillis(1), grossClaims, ContractCoverBase.LOSSES_OCCURING)
        List<ClaimCashflowPacket> latestCashflows = RIUtilities.latestCashflowByIncurredClaim(period1Claims, IncurredClaimBase.BASE)


        LayerParameters claimLimits = new LayerParameters(1, 60, 90)
        claimLimits.addAdditionalPremium(0, 0, 0, APBasis.LOSS)

        LayerParameters moreClaimLimits = new LayerParameters(0.5, 150, 40)
        moreClaimLimits.addAdditionalPremium(0, 0, 0, APBasis.LOSS)

        List<LayerParameters> allLayers = [claimLimits, moreClaimLimits]


        double cededPaid = calculation.paidLossAllLayers(latestCashflows, allLayers)
        assert cededPaid == 40 + 90 + 40 * 0.5

    }

    void testAdditionalPremiumByLayer() {

    }
}
