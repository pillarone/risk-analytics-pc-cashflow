package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ICoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment
import org.pillarone.riskanalytics.domain.pc.cf.segment.TestSegment
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

class MatrixReinsuranceContractsTests extends GroovyTestCase {
    def params
    ClaimsGenerator claimGeneratorCat
    ClaimsGenerator claimGeneratorFireLarge
    ClaimsGenerator claimGeneratorFireCat
    ClaimsGenerator claimGeneratorMotorCat
    ClaimsGenerator claimGeneratorFireAttritional
    ReinsuranceContract treaty
    MatrixReinsuranceContracts contracts

    ReinsuranceContract benefitContract
    ReinsuranceContract firstTreaty
    ReinsuranceContract secondTreaty
    Segment segmentHome
    Segment segmentCottage
    LegalEntity venusInsurance = new LegalEntity(name: 'Venus Insurance')
    LegalEntity marsInsurance = new LegalEntity(name: 'Mars Insurance')
    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class)
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class)

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    DateTime date20110418 = new DateTime(2011, 4, 18, 0, 0, 0, 0)
    DateTime date20110701 = new DateTime(2011, 7, 1, 0, 0, 0, 0)

    @Override
    protected void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
        ConstraintsFactory.registerConstraint(new CoverMap())
        contracts = new MatrixReinsuranceContracts()
        contracts.name = 'matrixContracts'
        firstTreaty = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        firstTreaty.name = 'First Treaty'
        secondTreaty = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        secondTreaty.name = 'Second Treaty'

        treaty = createTestContract('treaty', [['', ''], ['', ''], ['', ''], ['segmentHome', 'segmentCottage'], ['', ''], ['ANY', 'ANY']], 0)

        claimGeneratorCat = TestClaimsGenerator.getAttritionalClaimsGenerator('cat', treaty.iterationScope, 1000d)
        segmentHome = TestSegment.get('Home', treaty.iterationScope, [(claimGeneratorCat): 0.7d], null, null, null, venusInsurance)
        segmentCottage = TestSegment.get('Cottage', treaty.iterationScope, [(claimGeneratorCat): 0.8d], null, null, null)
        benefitContract = createTestContract('benefit1', [[''], [''], [''], ['segmentHome'], [''], ['ANY']], 0.4d)
    }

    private Map setupParameters(List parameters = [[''], [''], [''], [''], [''], ['ANY']], Map benefitContracts = [:]) {
        def result = [:]
        result.flexibleCover = new ConstrainedMultiDimensionalParameter(parameters,
                [CoverMap.CONTRACT_NET_OF, CoverMap.CONTRACT_CEDED_OF, CoverMap.LEGAL_ENTITY,
                        CoverMap.SEGMENTS, CoverMap.GENERATORS, CoverMap.LOSS_KIND_OF],
                ConstraintsFactory.getConstraints(CoverMap.IDENTIFIER))
        result.flexibleCover.comboBoxValues[0] = ['': null, 'contract1': firstTreaty, 'contract2': secondTreaty, 'benefit1': benefitContract]
        result.flexibleCover.comboBoxValues[1] = ['': null, 'contract1': firstTreaty, 'contract2': secondTreaty, 'benefit1': benefitContract]
        result.flexibleCover.comboBoxValues[2] = ['': null, 'venusInsurance': venusInsurance, 'marsInsurance': marsInsurance]
        result.flexibleCover.comboBoxValues[3] = ['': null, 'segmentHome': segmentHome, 'segmentCottage': segmentCottage]
        result.flexibleCover.comboBoxValues[4] = ['': null, 'fireLarge': claimGeneratorFireLarge, 'fireAttritional': claimGeneratorFireAttritional, 'fireCat': claimGeneratorFireCat]
        def claimTypeColumnValues = [:]
        ClaimTypeSelector.values().each {
            claimTypeColumnValues.put(it.name(), it)
        }
        result.flexibleCover.comboBoxValues[5] = claimTypeColumnValues
        result.benefitContracts = new ConstrainedMultiDimensionalParameter(benefitContracts.keySet() as List, ['Benefit Contracts'], ConstraintsFactory.getConstraints(ReinsuranceContractBasedOn.IDENTIFIER))
        def validBenefitContracts = ['': null]
        benefitContracts.each { k, v ->
            validBenefitContracts.put(k, v)
        }
        result.benefitContracts.comboBoxValues[0] = validBenefitContracts
        return result
    }

// PMO-2233 / Test1
//    void testCoverNetAndCededOfPrecedingContract() {
//        firstTreaty.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, setupParameters([[''], [''], [''], [''], [''], ['ANY']]))
//        secondTreaty.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX,
//                setupParameters([['', 'contract1'], ['contract1', ''], ['', ''], ['', ''], ['', ''], ['ANY', 'ANY']]))
//
//        MatrixReinsuranceContracts contracts = new MatrixReinsuranceContracts()
//        contracts.addSubComponent(firstTreaty)
//        contracts.addSubComponent(secondTreaty)
//        contracts.internalWiring()
//        IPeriodCounter periodCounter = firstTreaty.iterationScope.periodScope.periodCounter
//        contracts.inClaims.addAll(getClaims(periodCounter, [], 700d, ClaimType.SINGLE))
//        contracts.inClaims.addAll(getClaims(periodCounter, [], 500d, ClaimType.SINGLE))
//
//        List outClaimsCededFirstTreaty = new TestProbe(firstTreaty, 'outClaimsCeded').result
//        List outClaimsNetFirstTreaty = new TestProbe(firstTreaty, 'outClaimsNet').result
//        List outClaimsCededSecondTreaty = new TestProbe(secondTreaty, 'outClaimsCeded').result
//        List outClaimsNetSecondTreaty = new TestProbe(secondTreaty, 'outClaimsNet').result
//
//        contracts.start()
//        assert 2 == outClaimsCededSecondTreaty.size()
//        def expectedCededUltimatesFirstTreaty = [700 * 0.2d, 500 * 0.2d]
//        def expectedCededUltimatesSecondTreaty = [700 * 0.3d, 500 * 0.3d]
//        def expectedNetUltimatesFirstTreaty = [-700 + (700 * 0.2d), -500 + (500 * 0.2d)]
//        def expectedNetUltimatesSecondTreaty = [-700 + (700 * 0.3d), -500 + (500 * 0.3d)]
//
//        outClaimsNetFirstTreaty.each { ClaimCashflowPacket claim ->
//            assert claim.ultimate() in expectedNetUltimatesFirstTreaty
//        }
//
//        outClaimsCededFirstTreaty.each { ClaimCashflowPacket claim ->
//            assert claim.ultimate() in expectedCededUltimatesFirstTreaty
//        }
//        outClaimsNetSecondTreaty.each { ClaimCashflowPacket claim ->
//            assert claim.ultimate() in expectedNetUltimatesSecondTreaty
//        }
//
//        outClaimsCededSecondTreaty.each { ClaimCashflowPacket claim ->
//            assert claim.ultimate() in expectedCededUltimatesSecondTreaty
//        }
//    }

    // PMO-2233 Test 3b
    void testGrossClaims() {
        ReinsuranceContract markerTreaty = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)

        firstTreaty.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, setupParameters())

        MatrixReinsuranceContracts contracts = new MatrixReinsuranceContracts()
        contracts.addSubComponent(firstTreaty)
        contracts.internalWiring()
        IPeriodCounter periodCounter = firstTreaty.iterationScope.periodScope.periodCounter
        contracts.inClaims.addAll(getClaims(periodCounter, [], 700d, ClaimType.SINGLE))
        contracts.inClaims.addAll(getClaims(periodCounter, [], 500d, ClaimType.SINGLE))
        contracts.inClaims.addAll(getClaims(periodCounter, [markerTreaty], 400d, ClaimType.SINGLE))
        contracts.inClaims.addAll(getClaims(periodCounter, [markerTreaty], 150d, ClaimType.SINGLE))

        List outClaimsCededFirstTreaty = new TestProbe(firstTreaty, 'outClaimsCeded').result
        List outClaimsNetFirstTreaty = new TestProbe(firstTreaty, 'outClaimsNet').result

        contracts.start()
        assert 2 == outClaimsCededFirstTreaty.size()
        def expectedCededUltimatesFirstTreaty = [700 * 0.2d, 500 * 0.2d]
        def expectedNetUltimatesFirstTreaty = [-700 + (700 * 0.2d), -500 + (500 * 0.2d)]

        outClaimsNetFirstTreaty.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedNetUltimatesFirstTreaty
        }

        outClaimsCededFirstTreaty.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedCededUltimatesFirstTreaty
        }
    }

    //PMO-2233 Test 3c
    void testDoubleCounting() {
        ClaimsGenerator claimGeneratorCat = new ClaimsGenerator(name: 'cat')

        ReinsuranceContract treaty = ReinsuranceContractTests.getQuotaShareContract(0, date20110101)
        treaty.name = 'Second Treaty'
        treaty.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX,
                setupParameters([['', ''], ['', ''], ['', ''], ['segmentHome', 'segmentCottage'], ['', ''], ['ANY', 'ANY']]))

        MatrixReinsuranceContracts contracts = new MatrixReinsuranceContracts()
        contracts.addSubComponent(treaty)
        contracts.internalWiring()
        IPeriodCounter periodCounter = treaty.iterationScope.periodScope.periodCounter
        contracts.inClaims.addAll(getClaims(periodCounter, [claimGeneratorCat, segmentHome], 700d, ClaimType.SINGLE))
        contracts.inClaims.addAll(getClaims(periodCounter, [claimGeneratorCat, segmentCottage], 800d, ClaimType.SINGLE))

        List outClaimsCeded = new TestProbe(treaty, 'outClaimsCeded').result
        List outClaimsNet = new TestProbe(treaty, 'outClaimsNet').result
        contracts.start()
        assert 2 == outClaimsCeded.size()
        def expectedCededUltimates = [0d]
        def expectedNetUltimates = [-700d, -800d]

        outClaimsNet.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedNetUltimates
        }

        outClaimsCeded.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedCededUltimates
        }
    }

    //PMO-2233 Test F4,F5
    void testFilterByContract() {
        firstTreaty.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, setupParameters([[''], [''], [''], [''], [''], ['ANY']]))
        ReinsuranceContract treatyWithLegalEntity = createTestContract('treatyWithLegalEntity', [['contract1'], [''], ['venusInsurance'], [''], [''], ['ANY']])
        ReinsuranceContract treatyWithoutLegalEntity = createTestContract('treatyWithoutLegalEntity', [['contract1'], [''], [''], [''], [''], ['ANY']])
        ReinsuranceContract treatyNotPartOfLegalEntity = createTestContract('treatyNotPartOfLegalEntity', [['contract1'], [''], ['marsInsurance'], [''], [''], ['ANY']])

        contracts.addSubComponent(firstTreaty)
        contracts.addSubComponent(treatyWithLegalEntity)
        contracts.addSubComponent(treatyWithoutLegalEntity)
        contracts.addSubComponent(treatyNotPartOfLegalEntity)

        WiringUtils.use(WireCategory) {
            segmentHome.inClaims = claimGeneratorCat.outClaims
            contracts.inClaims = segmentHome.outClaimsGross
        }
        contracts.internalWiring()

        def contracts = [treatyWithoutLegalEntity, treatyWithLegalEntity]
        List cededProbes = contracts.collect {
            new TestProbe(it, 'outClaimsCeded').result
        }
        List netProbes = contracts.collect {
            new TestProbe(it, 'outClaimsNet').result
        }


        List netProbe = new TestProbe(treatyNotPartOfLegalEntity, 'outClaimsNet').result
        List cededProbe = new TestProbe(treatyNotPartOfLegalEntity, 'outClaimsCeded').result

        claimGeneratorCat.start()

        assert 0 == netProbe.size()
        assert 0 == cededProbe.size()

        def expectedCededUltimate = 112d
        def expectedNetUltimate = -448d

        cededProbes.each {
            assert 1 == it.size()
            it.each { ClaimCashflowPacket claim ->
                assert expectedCededUltimate == claim.ultimate()
            }
        }

        netProbes.each {
            assert 1 == it.size()
            it.each { ClaimCashflowPacket claim ->
                assert expectedNetUltimate == claim.ultimate()
            }
        }


    }

    //PMO-2233 Test 3c
    void testDoubleCountingWithWire() {

        WiringUtils.use(WireCategory) {
            segmentHome.inClaims = claimGeneratorCat.outClaims
            segmentCottage.inClaims = claimGeneratorCat.outClaims
            treaty.inClaims = segmentHome.outClaimsGross
            treaty.inClaims = segmentCottage.outClaimsGross
        }



        List outClaimsCeded = new TestProbe(treaty, 'outClaimsCeded').result
        List outClaimsNet = new TestProbe(treaty, 'outClaimsNet').result

        claimGeneratorCat.start()
        assert 2 == outClaimsCeded.size()
        def expectedCededUltimates = [0d]
        def expectedNetUltimates = [-700d, -800d]

        outClaimsNet.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedNetUltimates
        }

        outClaimsCeded.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedCededUltimates
        }
    }

    // PMO-2233 Test F1(a), F1(b), F2, F7, F8
    void testIntersectAndUnionCombinations() {

        ReinsuranceContract intersectionLegalEntityAndLobTreaty = createTestContract('intersectionLegalEntityAndLobTreaty', [[''], [''], ['venusInsurance'], ['segmentHome'], [''], ['ANY']])
        ReinsuranceContract segmentTreaty = createTestContract('segmentTreaty', [[''], [''], [''], ['segmentHome'], [''], ['ANY']])
        ReinsuranceContract unionLegalEntityAndSegmentsTreaty = createTestContract('unionLegalEntityAndSegmentsTreaty', [['', ''], ['', ''], ['', 'venusInsurance'], ['segmentHome', ''], ['', ''], ['ANY', 'ANY']])
        ReinsuranceContract legalEntityTreaty = createTestContract('legalEntityTreaty', [[''], [''], ['venusInsurance'], [''], [''], ['ANY']])
        ReinsuranceContract segmentNotPartOfLegalEntityTreaty = createTestContract('segmentNotPartOfLegalEntityTreaty', [[''], [''], ['venusInsurance'], ['segmentCottage'], [''], ['ANY']])
        ReinsuranceContract twoEqualFilterRowsTreaty = createTestContract('twoEqualFilterRowsTreaty', [['', ''], ['', ''], ['', ''], ['segmentHome', 'segmentHome'], ['', ''], ['ANY', 'ANY']])
        ReinsuranceContract emptyMatrixTreaty = createTestContract('emptyMatrixTreaty', [[], [], [], [], [], []])

        WiringUtils.use(WireCategory) {
            segmentHome.inClaims = claimGeneratorCat.outClaims
            intersectionLegalEntityAndLobTreaty.inClaims = segmentHome.outClaimsGross
            segmentTreaty.inClaims = segmentHome.outClaimsGross
            unionLegalEntityAndSegmentsTreaty.inClaims = segmentHome.outClaimsGross
            legalEntityTreaty.inClaims = segmentHome.outClaimsGross
            segmentNotPartOfLegalEntityTreaty.inClaims = segmentHome.outClaimsGross
            twoEqualFilterRowsTreaty.inClaims = segmentHome.outClaimsGross
            emptyMatrixTreaty.inClaims = segmentHome.outClaimsGross
        }

        def contracts = [intersectionLegalEntityAndLobTreaty, segmentTreaty, unionLegalEntityAndSegmentsTreaty, legalEntityTreaty, twoEqualFilterRowsTreaty]
        List cededProbes = contracts.collect {
            new TestProbe(it, 'outClaimsCeded')
        }
        List netProbes = contracts.collect {
            new TestProbe(it, 'outClaimsNet')
        }

        List netProbe1 = new TestProbe(segmentNotPartOfLegalEntityTreaty, 'outClaimsNet').result
        List netProbe2 = new TestProbe(emptyMatrixTreaty, 'outClaimsNet').result
        List cededProbe1 = new TestProbe(segmentNotPartOfLegalEntityTreaty, 'outClaimsCeded').result
        List cededProbe2 = new TestProbe(emptyMatrixTreaty, 'outClaimsCeded').result


        claimGeneratorCat.start()

        assert 0 == cededProbe1.size()
        assert 0 == cededProbe2.size()
        assert 0 == netProbe1.size()
        assert 0 == netProbe2.size()

        def expectedCededUltimate = 140d
        def expectedNetUltimate = -560d

        cededProbes.each { TestProbe probe ->
            assert 1 == probe.result.size(), probe
            probe.result.each { ClaimCashflowPacket claim ->
                assert expectedCededUltimate == claim.ultimate()
            }
        }

        netProbes.each { TestProbe probe ->
            assert 1 == probe.result.size(), probe
            probe.result.each { ClaimCashflowPacket claim ->
                assert expectedNetUltimate == claim.ultimate()
            }
        }
    }

    //PMO-2233 Test 11
    void testNetEqualsNet() {
        ReinsuranceContract treatyWithoutBenefit = createTestContract('treatyWithoutBenefit', [['benefit1'], [''], [''], [''], [''], ['ANY']])
        ReinsuranceContract treatyWithBenefit = createTestContract('treatyWithBenefit', [[''], [''], [''], ['segmentHome'], [''], ['ANY']], 0.2d, ['benefit1': benefitContract])

        WiringUtils.use(WireCategory) {
            segmentHome.inClaims = claimGeneratorCat.outClaims
            contracts.inClaims = segmentHome.outClaimsGross
        }

        contracts.addSubComponent(treatyWithBenefit)
        contracts.addSubComponent(treatyWithoutBenefit)
        contracts.addSubComponent(benefitContract)
        contracts.internalWiring()

        List netProbe1 = new TestProbe(treatyWithoutBenefit, 'outClaimsNet').result
        List cededProbe1 = new TestProbe(treatyWithoutBenefit, 'outClaimsCeded').result
        List netProbe2 = new TestProbe(treatyWithBenefit, 'outClaimsNet').result
        List cededProbe2 = new TestProbe(treatyWithBenefit, 'outClaimsCeded').result

        claimGeneratorCat.start()
        println contracts
        assert 1 == netProbe1.size()
        assert 1 == cededProbe1.size()
        def expectedCededUltimate = 84d
        def expectedNetUltimate = -336d
        assert expectedNetUltimate == netProbe1[0].ultimate()
        assert expectedNetUltimate == netProbe2[0].ultimate()
        assert expectedCededUltimate == cededProbe1[0].ultimate()
        assert expectedCededUltimate == cededProbe2[0].ultimate()
    }

    // PMO-2233 Test 12
    void testCededMinusBenefitEqualsNull() {
        claimGeneratorCat = TestClaimsGenerator.getFrequencySeverityClaimsGenerator('generator1', treaty.iterationScope, 10, 500)
        segmentHome = TestSegment.get('Home', treaty.iterationScope, [(claimGeneratorCat): 0.8d], null, null, null, venusInsurance)
        benefitContract = ReinsuranceContractTests.getWXLContract(100, 300, 1000, 100, treaty.iterationScope.periodScope.currentPeriodStartDate)
        benefitContract.parmCover = getMatrixCover([[''], [''], [''], ['segmentHome'], [''], ['ANY']])
        benefitContract.name = 'benefitContract'
        ReinsuranceContract quotaShareTreaty = createTestContract('quotaShareTreaty', [[''], ['benefit1'], [''], [''], [''], ['ANY']], 0.2d, ['benefit1': benefitContract])


        contracts.addSubComponent(quotaShareTreaty)
        contracts.addSubComponent(benefitContract)
        contracts.internalWiring()

        WiringUtils.use(WireCategory) {
            segmentHome.inClaims = claimGeneratorCat.outClaims
            contracts.inClaims = segmentHome.outClaimsGross
        }


        List netProbe1 = new TestProbe(quotaShareTreaty, 'outClaimsNet').result
        List cededProbe1 = new TestProbe(quotaShareTreaty, 'outClaimsCeded').result
        // handy for debugging.
        List netProbe2 = new TestProbe(benefitContract, 'outClaimsNet').result
        List cededProbe2 = new TestProbe(benefitContract, 'outClaimsCeded').result


        claimGeneratorCat.start()

        netProbe1.each {
            assert 0d == it.ultimate()
        }
        cededProbe1.each {
            assert 0d == it.ultimate()
        }
    }

    //PMO-2233 Test 13
    void testCatClaims() {
        claimGeneratorFireLarge = TestClaimsGenerator.getOccurenceAndSeverityClaimsGenerator('Fire Large', treaty.iterationScope, 3, 500)
        claimGeneratorFireCat = TestClaimsGenerator.getOccurenceAndSeverityClaimsGenerator('Fire Cat', treaty.iterationScope, 3, 1000, null, null, null, FrequencySeverityClaimType.AGGREGATED_EVENT)
        claimGeneratorMotorCat = TestClaimsGenerator.getOccurenceAndSeverityClaimsGenerator('Motor Cat', treaty.iterationScope, 3, 1000, null, null, null, FrequencySeverityClaimType.AGGREGATED_EVENT)
        claimGeneratorFireAttritional = TestClaimsGenerator.getAttritionalClaimsGenerator('Fire Attritional', treaty.iterationScope, 10000)

        segmentHome = TestSegment.get('Motor', treaty.iterationScope, [(claimGeneratorMotorCat): 1d], null, null, null, venusInsurance)
        segmentCottage = TestSegment.get('Fire', treaty.iterationScope, [(claimGeneratorFireAttritional): 1d, (claimGeneratorFireCat): 1d, (claimGeneratorFireLarge): 1d], null, null, null, venusInsurance)

        benefitContract = ReinsuranceContractTests.getCXLContract(100, 300, 1000, 100, treaty.iterationScope.periodScope.currentPeriodStartDate)
        benefitContract.parmCover = getMatrixCover([[''], [''], [''], [''], [''], [ClaimTypeSelector.AGGREGATED_EVENT.name()]])
        benefitContract.name = 'Cat XL'
        ReinsuranceContract treatyWithoutBenefit = createTestContract('treatyWithoutBenefit', [
                ['', '', 'benefit1'], ['', '', ''], ['', '', ''], ['', '', ''], ['fireLarge', 'fireAttritional', 'fireCat'], [ClaimTypeSelector.ANY.name()] * 3
        ], 0.2d)

        ReinsuranceContract treatyWithBenefit = createTestContract('treatyWithBenefit', [
                [''], [''], [''], ['segmentCottage'], [''], [ClaimTypeSelector.ANY.name()]
        ], 0.2d, ['benefit1': benefitContract])

        contracts.addSubComponent(treatyWithoutBenefit)
        contracts.addSubComponent(treatyWithBenefit)
        contracts.addSubComponent(benefitContract)
        contracts.internalWiring()

        WiringUtils.use(WireCategory) {
            segmentHome.inClaims = claimGeneratorMotorCat.outClaims
            segmentCottage.inClaims = claimGeneratorFireLarge.outClaims
            segmentCottage.inClaims = claimGeneratorFireAttritional.outClaims
            segmentCottage.inClaims = claimGeneratorFireCat.outClaims
            contracts.inClaims = segmentHome.outClaimsGross
            contracts.inClaims = segmentCottage.outClaimsGross
        }

        List netProbe1 = new TestProbe(treatyWithBenefit, 'outClaimsNet').result
        List cededProbe1 = new TestProbe(treatyWithBenefit, 'outClaimsCeded').result
        List netProbe2 = new TestProbe(treatyWithoutBenefit, 'outClaimsNet').result
        List cededProbe2 = new TestProbe(treatyWithoutBenefit, 'outClaimsCeded').result

        // handy for debugging.
        List netProbe3 = new TestProbe(benefitContract, 'outClaimsNet').result
        List cededProbe3 = new TestProbe(benefitContract, 'outClaimsCeded').result

        claimGeneratorFireCat.start()
        claimGeneratorFireAttritional.start()
        claimGeneratorFireLarge.start()
        claimGeneratorMotorCat.start()

        assert 6 == netProbe3.size() // as attr and single has been filtered out, using any would provide 10
        assert 6 == cededProbe3.size() // as attr and single has been filtered out, using any would provide 10

        assert 7 == netProbe1.size()
        assert 7 == cededProbe1.size()
        assert 7 == netProbe2.size()
        assert 7 == cededProbe2.size()

        def expectedCededUltimates = [140d, 2000d, 200d, 100d, 200d]
        def expectedNetUltimates = [-560d, -8000d, -800d, -400d]

        netProbe1 + netProbe2.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedNetUltimates
        }

        cededProbe1 + cededProbe2.each { ClaimCashflowPacket claim ->
            assert claim.ultimate() in expectedCededUltimates
        }

    }

    private ReinsuranceContract createTestContract(String contractName, List matrixParams, Double qoutaShare = 0.2d, Map benefitContracts = [:]) {
        def contract = ReinsuranceContractTests.getQuotaShareContract(qoutaShare, date20110101)
        contract.name = contractName
        contract.parmCover = getMatrixCover(matrixParams, benefitContracts)
        return contract
    }

    private ICoverAttributeStrategy getMatrixCover(List matrixParams, Map benefitContracts = [:]) {
        CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, setupParameters(matrixParams, benefitContracts))
    }


    private List<ClaimCashflowPacket> getClaims(IPeriodCounter periodCounter, List<IComponentMarker> markers, double ultimate, ClaimType claimType = ClaimType.AGGREGATED) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, claimType,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)
        markers.each { claims*.setMarker(it) }
        return claims
    }

}
