package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredCalc

import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IPremiumPerPeriod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IRiLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerIdentifier
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LossAfterClaimAndAnnualStructures
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.NoPremiumPerPeriod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.PeriodLayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.AdditionalPremium
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLoss
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossAndLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossWithTerm
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.PremiumStructreAPBasis
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ContractOrderingMethod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier
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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossAndApsAfterTermStructure
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.AdditionalPremiumLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractStructure
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ReinstatementLayer

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

        LayerParameters noLimits = new LayerParameters(1, 0, 0, 1, 1)
        noLimits.addAdditionalPremium(0, 0, 0, APBasis.LOSS)
        double ceded0 = calc.layerCededIncurred(grossClaims, noLimits).getLossAfterAnnualStructureWithShareApplied()
        assert ceded0 == 450

        LayerParameters testClaimExcess = new LayerParameters(1, 200, 50,  1, 1)
        double ceded1 = calc.layerCededIncurred(grossClaims, testClaimExcess).getLossAfterAnnualStructureWithShareApplied()
        assert ceded1 == 0

        LayerParameters testClaimDeductible = new LayerParameters(1, 90, 0,  1, 1)
        testClaimDeductible.addAdditionalPremium(0, 0, 0, APBasis.LOSS)
        double ceded2 = calc.layerCededIncurred(grossClaims, testClaimDeductible).getLossAfterAnnualStructureWithShareApplied()
        assert ceded2 == 10 + 60 + 110

        LayerParameters claimLimit = new LayerParameters(1, 70, 50,  1, 1)
        claimLimit.addAdditionalPremium(0, 0, 0, APBasis.LOSS)
        double ceded3 = calc.layerCededIncurred(grossClaims, claimLimit).getLossAfterAnnualStructureWithShareApplied()
        assert ceded3 == 30 + 50 + 50

        LayerParameters periodDeductible = new LayerParameters(1, 0, 0,  1, 1)
        periodDeductible.addAdditionalPremium(400, 0, 0, APBasis.LOSS)
        double ceded4 = calc.layerCededIncurred(grossClaims, periodDeductible).getLossAfterAnnualStructureWithShareApplied()
        assert ceded4 == 50

        LayerParameters periodLimit = new LayerParameters(1, 0, 0,  1, 1)
        periodLimit.addAdditionalPremium(0, 400, 0, APBasis.LOSS)
        double ceded5 = calc.layerCededIncurred(grossClaims, periodLimit).getLossAfterAnnualStructureWithShareApplied()
        assert ceded5 == 400

        LayerParameters periodLimitAndDeductible = new LayerParameters(1, 0, 0,  1, 1)
        periodLimitAndDeductible.addAdditionalPremium(250, 100, 0, APBasis.LOSS)
        double ceded6 = calc.layerCededIncurred(grossClaims, periodLimitAndDeductible).getLossAfterAnnualStructureWithShareApplied()
        assert ceded6 == 100

    }

    void testAdditionalPremiumLoss() {
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc()
        ScaledPeriodLayerParameters layerParameters = new ScaledPeriodLayerParameters()
        layerParameters.add(1, 0, 1, 0, 0, 0, 400d, 0.5, APBasis.LOSS)
        final IncurredLoss incurredLoss = incurredAmountForAps(400d, layerParameters.getContractLayers(1).get(0))
        final IncurredLossWithTerm layerLoss = new IncurredLossWithTerm(incurredLoss, 400, 400, layerParameters, 1 )

        double lossAP = annualIncurredCalc.additionalPremiumByLayer(20, layerLoss, layerParameters.getContractLayers(1).get(0) ).getAt(0).additionalPremium
        assert lossAP == 400 * 0.5
    }

    void testAdditionalPremiumPremium(){
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc()
        ScaledPeriodLayerParameters layerParameters = new ScaledPeriodLayerParameters()
        layerParameters.add(1, 0, 0.8, 0, 460, 0, 600, 0.5, APBasis.PREMIUM)
        final IncurredLoss incurredLoss = incurredAmountForAps(450d, layerParameters.getContractLayers(1).get(0))
        final IncurredLossWithTerm layerLoss = new IncurredLossWithTerm(incurredLoss, 450, 450, layerParameters, 1 )

        double premiumAP = annualIncurredCalc.additionalPremiumByLayer(20, layerLoss, layerParameters.getContractLayers(1).get(0)).getAt(0).additionalPremium
        assertEquals( premiumAP , (450 * 0.5 * 0.8 * 20) / 460, 0.01d)
    }

    void testAdditionalPremiumNCB(){
        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc()
        ScaledPeriodLayerParameters layerParameters = new ScaledPeriodLayerParameters()
        layerParameters.add(1, 0, 1, 0, 460, 0, 600, 0.5, APBasis.NCB)
        final IncurredLoss incurredLoss = incurredAmountForAps(0d, layerParameters.getContractLayers(1).get(0))
        final IncurredLossWithTerm layerLoss = new IncurredLossWithTerm(incurredLoss, 0d, 0d, layerParameters, 1 )

        double ncbPrem = annualIncurredCalc.additionalPremiumByLayer(20, layerLoss, layerParameters.getContractLayers(1).get(0)).getAt(0).additionalPremium
        assert ncbPrem == 20 * 0.5
    }

    static IncurredLoss incurredAmountForAps(double lossAfterShareAndStructure, IRiLayer layer) {

//        double loss = lossAndLayer.getIncurredLoss().getLayerAndIncurredLoss(layerParams.getLayerIdentifier()).getLossAfterClaimAndAnnualStructures().getLossAfterAnnualStructureWithShareApplied();
        final LossAfterClaimAndAnnualStructures structures = new LossAfterClaimAndAnnualStructures(lossAfterShareAndStructure, lossAfterShareAndStructure, layer) {
            @Override
            double getLossAfterAnnualStructureWithShareApplied() {
                return lossAfterShareAndStructure
            }
        }

        final IncurredLossAndLayer incurredLossAndLayer = new IncurredLossAndLayer(structures, layer)

        final IncurredLoss incurredLoss = new IncurredLoss(null) {
            @Override
            IncurredLossAndLayer getLayerAndIncurredLoss(final LayerIdentifier layerIdentifier) {
                return incurredLossAndLayer
            }
        }
        return incurredLoss
    }

    void testReinstatements() {
        final YearLayerIdentifier identifier = new YearLayerIdentifier(1, 1)
        final ReinstatementLayer layer1 = new ReinstatementLayer(identifier, 0, 0.5d)
        final ReinstatementLayer layer2 = new ReinstatementLayer(identifier, 1, 0.5d)
        final ReinstatementLayer layer3 = new ReinstatementLayer(identifier, 2, 1d)
        final ArrayList<ReinstatementLayer> reinStLayer = [layer1, layer2, layer3]
        final ContractLayer layer = new ContractLayer(identifier, 0.9, 30d, 50d, 0d, 0d, 500d, reinStLayer, [], [], 0d)
        IncurredLossWithTerm incurredLossWithTerm = new IncurredLossWithTerm(new IncurredLoss(null), 81d, 81d, null, 1)

        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc()
        final Collection<AdditionalPremium> aps = annualIncurredCalc.additionalPremiumByLayer(0d, incurredLossWithTerm, layer)
        assertEquals("test AP", (Double) aps*.getAdditionalPremium().sum(), 765, 0.01)

//        Test for when there should be no reinstatements
        final ContractLayer noClaimLimit = new ContractLayer(identifier, 0.9, 0d, 50d, 0d, 0d, 500d, reinStLayer, [], [], 0d)
        final Collection<AdditionalPremium> noAps = annualIncurredCalc.additionalPremiumByLayer(0d, incurredLossWithTerm, noClaimLimit)
        assert noAps.size() == 0
    }

    void testNewAdditionalPremiums() {
        final YearLayerIdentifier identifier = new YearLayerIdentifier(1, 1)
        final AdditionalPremium layer1 = new AdditionalPremium(identifier, 2, 1d)
        final ArrayList<AdditionalPremiumLayer> apLayers = [layer1]
        final ContractLayer layer = new ContractLayer(identifier, 0.9, 30d, 50d, 0d, 0d, 500d, [], apLayers, [], 0d)
        IncurredLossWithTerm incurredLossWithTerm = new IncurredLossWithTerm(new IncurredLoss(null), 81d, 81d, null, 1)

        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc()
        final Collection<AdditionalPremium> aps = annualIncurredCalc.additionalPremiumByLayer(0d, incurredLossWithTerm, layer)
        assertEquals("test AP", (Double) aps*.getAdditionalPremium().sum(), 765, 0.01)

//        Test for when there should be no reinstatements
        final ContractLayer noClaimLimit = new ContractLayer(identifier, 0.9, 0d, 50d, 0d, 0d, 500d, reinStLayer, [], [], 0d)
        final Collection<AdditionalPremium> noAps = annualIncurredCalc.additionalPremiumByLayer(0d, incurredLossWithTerm, noClaimLimit)
        assert noAps.size() == 0
    }

    void testNCB() {
        final YearLayerIdentifier identifier = new YearLayerIdentifier(1, 1)
        final ContractLayer layer = new ContractLayer(identifier, 0.9, 30d, 50d, 0d, 0d, 500d, [], [], [], 0.5)
        final IncurredLossWithTerm term = new IncurredLossWithTerm(new IncurredLoss(null), 0d, 0d, null, 1)
        final Collection<AdditionalPremium> aps = new AnnualIncurredCalc().additionalPremiumByLayer(0d, term,layer)
        assertEquals("test AP", (Double) aps*.getAdditionalPremium().sum(), 500 * 0.9 * 0.5, 0.01)

        final LossAfterClaimAndAnnualStructures withLoss = new LossAfterClaimAndAnnualStructures(100d, 100d, layer)
        final IncurredLossWithTerm withALoss = new IncurredLossWithTerm(new IncurredLoss(null), 100d, 100d, null, 1)
        IncurredLossAndLayer andLossLayer = new IncurredLossAndLayer(withLoss, layer)
        final Collection<AdditionalPremium> noAps = new AnnualIncurredCalc().additionalPremiumByLayer(0d, withALoss, layer)
        assert noAps.size() == 0
    }

    void testNewAdditionalPremium() {
        final YearLayerIdentifier identifier = new YearLayerIdentifier(1, 1)
        final YearLayerIdentifier identifier2 = new YearLayerIdentifier(1, 2)
        final AdditionalPremiumLayer apLayer11 = new AdditionalPremiumLayer(identifier, 0d,0.2d, 0.1, PremiumStructreAPBasis.PREMIUM )
        final AdditionalPremiumLayer apLayer12 = new AdditionalPremiumLayer(identifier, 0.2d,1d, 0.2, PremiumStructreAPBasis.PREMIUM )
        final AdditionalPremiumLayer apLayer13 = new AdditionalPremiumLayer(identifier, 1d, 2d, 0.3, PremiumStructreAPBasis.PREMIUM )
        final AdditionalPremiumLayer apLayer21 = new AdditionalPremiumLayer(identifier2, 0d,0.2d, 0.1, PremiumStructreAPBasis.LOSS )
        final AdditionalPremiumLayer apLayer22 = new AdditionalPremiumLayer(identifier2, 0.2d,1d, 0.2, PremiumStructreAPBasis.LOSS )
        final AdditionalPremiumLayer apLayer23 = new AdditionalPremiumLayer(identifier2, 1d,2d, 0.3, PremiumStructreAPBasis.LOSS )
        final ArrayList<AdditionalPremiumLayer> ap1 = [apLayer11, apLayer12, apLayer13]
        final ArrayList<AdditionalPremiumLayer> ap2 = [apLayer21, apLayer22, apLayer23]
        final ContractLayer layer1 = new ContractLayer(identifier, 0.9, 30d, 50d, 0d, 0d, 500d, [], ap1 , [], 0d)
        final ContractLayer layer2 = new ContractLayer(identifier2, 0.7, 150d, 80d, 0d, 0d, 200d, [], ap2 , [], 0d)
        IncurredLoss andLayer1 = incurredAmountForAps(81d, layer1)
        IncurredLoss andLayer2 = incurredAmountForAps(56d, layer2)
        final IncurredLossWithTerm term1 = new IncurredLossWithTerm(andLayer1, 81d, 81d, null, 1)
        final IncurredLossWithTerm term2 = new IncurredLossWithTerm(andLayer2, 56d, 56d, null, 1)


        AnnualIncurredCalc annualIncurredCalc = new AnnualIncurredCalc()
        final Collection<AdditionalPremium> aps1 = annualIncurredCalc.additionalPremiumByLayer(0d, term1, layer1)
        final Collection<AdditionalPremium> aps2 = annualIncurredCalc.additionalPremiumByLayer(0d, term2, layer2)
        assert aps1*.getAdditionalPremium().sum() == 216
        assert aps2*.getAdditionalPremium().sum() == 4.85333333333333333
    }

    void testCededIncurredRespectTerm() {

        TermIncurredCalculation calculation = new TermIncurredCalculation()
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IPremiumPerPeriod premiumPerPeriod =  new NoPremiumPerPeriod()
        IAllContractClaimCache claimCache = new ContractClaimStoreTestIncurredClaimImpl(grossClaims)

        ScaledPeriodLayerParameters layerParameters = new ScaledPeriodLayerParameters()
        layerParameters.setExposureBase(ExposureBase.ABSOLUTE)
        layerParameters.setCounter(periodScope.getPeriodCounter())
        layerParameters.setUwInfo(new AllPeriodUnderwritingInfoPacket())
        layerParameters.add(0, 1, 1, 0, 0, 0, 0, 0, APBasis.NCB)
        layerParameters.setTermExcess(100)
        layerParameters.setTermLimit(400)
        IncurredLossAndApsAfterTermStructure incurredFirstCalc = calculation.cededIncurredAndApsRespectTerm(claimCache, layerParameters, periodScope, ContractCoverBase.LOSSES_OCCURING, premiumPerPeriod)
        assertEquals("First period inc term deductible", 350, incurredFirstCalc.getIncurredLossAfterTermStructure(0).incurredLossAfterTermStructurte)
        Map<Integer, Double> incurredFirstCalcMap = calculation.cededIncurredsByPeriods(claimCache, periodScope, layerParameters, ContractCoverBase.LOSSES_OCCURING, 0, premiumPerPeriod)
        assertEquals(" ", 350, incurredFirstCalcMap.get(0))

        periodScope.prepareNextPeriod()
        IncurredLossAndApsAfterTermStructure incurredSecondCalc = calculation.cededIncurredAndApsRespectTerm(claimCache, layerParameters, periodScope, ContractCoverBase.LOSSES_OCCURING, premiumPerPeriod)
        assertEquals("second period inc term deductible", 0, incurredSecondCalc.getIncurredLossAfterTermStructure(1).incurredLossAfterTermStructurte)
        Map<Integer, Double> incurredSecondCalcMap = calculation.cededIncurredsByPeriods(claimCache, periodScope, layerParameters, ContractCoverBase.LOSSES_OCCURING, 1, premiumPerPeriod)
        assertEquals(" ", 350, incurredSecondCalcMap.get(0))
        assertEquals(" ", 0, incurredSecondCalcMap.get(1))

        grossClaims.add(new ClaimRoot(100, ClaimType.SINGLE, new DateTime(2012, 1, 1, 1, 0, 0, 0), new DateTime(2012, 1, 1, 1, 0, 0, 0)))
        IContractClaimStore claimCache1 = new ContractClaimStoreTestIncurredClaimImpl(grossClaims)

        periodScope.prepareNextPeriod()
        IncurredLossAndApsAfterTermStructure inThirdCalc = calculation.cededIncurredAndApsRespectTerm(claimCache1, layerParameters, periodScope, ContractCoverBase.LOSSES_OCCURING, premiumPerPeriod)
        Map<Integer, Double> incurredThirdCalcMap = calculation.cededIncurredsByPeriods(claimCache1, periodScope, layerParameters, ContractCoverBase.LOSSES_OCCURING, 2, premiumPerPeriod)
        assertEquals("third period inc term deductible", 50, inThirdCalc.getIncurredLossAfterTermStructure(2).incurredLossAfterTermStructurte)
        assertEquals(" ", 0, incurredThirdCalcMap.get(1))
        assertEquals("", 50, incurredThirdCalcMap.get(2))
    }
}
