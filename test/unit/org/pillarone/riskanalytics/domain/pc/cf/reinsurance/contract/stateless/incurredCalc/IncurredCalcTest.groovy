package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredCalc

import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.APBasis

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.TermIncurredCalculation
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.AnnualIncurredCalc
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IContractClaimStore

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.ContractClaimStoreTestIncurredClaimImpl
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache

/**
 * Created with IntelliJ IDEA.
 * User: sparten
 * Date: 31.08.12
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
/**
 *   author simon.parten @ art-allianz . com
 */
class IncurredCalcTest extends GroovyTestCase {

    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0)
    List<IClaimRoot> grossClaims = new ArrayList<IClaimRoot>()

    protected void setUp() {

        grossClaims.clear()

        grossClaims << new ClaimRoot(100, ClaimType.SINGLE, start2010, start2010)
        grossClaims << new ClaimRoot(150, ClaimType.SINGLE, start2010, start2010)
        grossClaims << new ClaimRoot(200, ClaimType.SINGLE, start2010, start2010)
    }

    void testCededIncurred() {
        AnnualIncurredCalc calc = new AnnualIncurredCalc()

        LayerParameters noLimits = new LayerParameters(1, 0, 0)
        noLimits.addAdditionalPremium(0, 0, 0, APBasis.LOSS)
        double ceded0 = calc.layerCededIncurred(grossClaims, noLimits)
        assert ceded0 == 450

        LayerParameters testClaimExcess = new LayerParameters(1, 200, 50)
        double ceded1 = calc.layerCededIncurred(grossClaims, testClaimExcess)
        assert ceded1 == 0

        LayerParameters testClaimDeductible = new LayerParameters(1, 90, 0)
        testClaimDeductible.addAdditionalPremium(0, 0, 0, APBasis.LOSS)
        double ceded2 = calc.layerCededIncurred(grossClaims, testClaimDeductible)
        assert ceded2 == 10 + 60 + 110

        LayerParameters claimLimit = new LayerParameters(1, 70, 50)
        claimLimit.addAdditionalPremium(0, 0, 0, APBasis.LOSS)
        double ceded3 = calc.layerCededIncurred(grossClaims, claimLimit)
        assert ceded3 == 30 + 50 + 50

        LayerParameters periodDeductible = new LayerParameters(1, 0, 0)
        periodDeductible.addAdditionalPremium(400, 0, 0, APBasis.LOSS)
        double ceded4 = calc.layerCededIncurred(grossClaims, periodDeductible)
        assert ceded4 == 50

        LayerParameters periodLimit = new LayerParameters(1, 0, 0)
        periodLimit.addAdditionalPremium(0, 400, 0, APBasis.LOSS)
        double ceded5 = calc.layerCededIncurred(grossClaims, periodLimit)
        assert ceded5 == 400

        LayerParameters periodLimitAndDeductible = new LayerParameters(1, 0, 0)
        periodLimitAndDeductible.addAdditionalPremium(250, 100, 0, APBasis.LOSS)
        double ceded6 = calc.layerCededIncurred(grossClaims, periodLimitAndDeductible)
        assert ceded6 == 100

    }

    void testAdditionalPremium() {

        AnnualIncurredCalc calc = new AnnualIncurredCalc()

        LayerParameters testPremiumAP = new LayerParameters(1, 0, 0)
        testPremiumAP.addAdditionalPremium(0, 400, 0.5, APBasis.LOSS)
        double lossAP = calc.additionalPremiumByLayer(grossClaims, testPremiumAP, 20)
        assert lossAP == 400 * 0.5

        LayerParameters testLossAP = new LayerParameters(0.8, 0, 0)
        testLossAP.addAdditionalPremium(0, 600, 0.5, APBasis.PREMIUM)
        double premiumAP = calc.additionalPremiumByLayer(grossClaims, testLossAP, 20)
        assert premiumAP == (450 * 0.5 * 0.8 * 20) / 600

        LayerParameters ncbPremium = new LayerParameters(1, 0, 0)
        ncbPremium.addAdditionalPremium(0, 0, 0.5, APBasis.NCB)
        double ncbPrem = calc.additionalPremiumByLayer(new ArrayList<IClaimRoot>(), ncbPremium, 20)
        assert ncbPrem == 20 * 0.5

//        Mix AP's.

        LayerParameters lotsOfAP = new LayerParameters(1, 0, 0)
        lotsOfAP.addAdditionalPremium(50, 100, 0.5, APBasis.LOSS)
        lotsOfAP.addAdditionalPremium(150, 200, 0.5, APBasis.PREMIUM)
        lotsOfAP.addAdditionalPremium(350, 50, 0.1, APBasis.LOSS)

        double allAPs = calc.additionalPremiumByLayer(grossClaims, lotsOfAP, 20)
        assert allAPs == 0.5 * 100 + (200 * 1 * 20 * 0.5) / 200 + 50 * 0.1
    }

    void testCededIncurredRespectTerm() {

        TermIncurredCalculation calculation = new TermIncurredCalculation()
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)

        IAllContractClaimCache claimCache = new ContractClaimStoreTestIncurredClaimImpl(grossClaims)

        ScaledPeriodLayerParameters layerParameters = new ScaledPeriodLayerParameters()
        layerParameters.setExposureBase(ExposureBase.ABSOLUTE)
        layerParameters.setCounter(periodScope.getPeriodCounter())
        layerParameters.setUwInfo(new AllPeriodUnderwritingInfoPacket())
        layerParameters.add(0, 1, 1, 0, 0, 0, 0, 0, APBasis.NCB)
        double incurredFirstCalc = calculation.cededIncurredRespectTerm(claimCache, layerParameters, periodScope, 100, 400, periodScope.getPeriodCounter(), ContractCoverBase.LOSSES_OCCURING)
        assertEquals("First period inc term deductible", 350, incurredFirstCalc)
        Map<Integer, Double> incurredFirstCalcMap = calculation.cededIncurredsByPeriods(claimCache, periodScope, 100, 400, layerParameters, ContractCoverBase.LOSSES_OCCURING, 0)
        assertEquals(" ", 350, incurredFirstCalcMap.get(0))

        periodScope.prepareNextPeriod()
        double incurredSecondCalc = calculation.cededIncurredRespectTerm(claimCache, layerParameters, periodScope, 100, 400, periodScope.getPeriodCounter(), ContractCoverBase.LOSSES_OCCURING)
        assertEquals("second period inc term deductible", 0, incurredSecondCalc)
        Map<Integer, Double> incurredSecondCalcMap = calculation.cededIncurredsByPeriods(claimCache, periodScope, 100, 400, layerParameters, ContractCoverBase.LOSSES_OCCURING, 1)
        assertEquals(" ", 350, incurredSecondCalcMap.get(0))
        assertEquals(" ", 0, incurredSecondCalcMap.get(1))

        grossClaims.add(new ClaimRoot(100, ClaimType.SINGLE, new DateTime(2012, 1, 1, 1, 0, 0, 0), new DateTime(2012, 1, 1, 1, 0, 0, 0)))
        IContractClaimStore claimCache1 = new ContractClaimStoreTestIncurredClaimImpl(grossClaims)

        periodScope.prepareNextPeriod()
        double inThirdCalc = calculation.cededIncurredRespectTerm(claimCache1, layerParameters, periodScope, 100, 400, periodScope.getPeriodCounter(), ContractCoverBase.LOSSES_OCCURING)
        Map<Integer, Double> incurredThirdCalcMap = calculation.cededIncurredsByPeriods(claimCache1, periodScope, 100, 400, layerParameters, ContractCoverBase.LOSSES_OCCURING, 2)
        assertEquals("third period inc term deductible", 50, inThirdCalc)
        assertEquals(" ", 0, incurredThirdCalcMap.get(1))
        assertEquals("", 50, incurredThirdCalcMap.get(2))
    }
}
