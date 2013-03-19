package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractTests
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.*
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractsTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    DateTime date20110418 = new DateTime(2011, 4, 18, 0, 0, 0, 0)
    DateTime date20110701 = new DateTime(2011, 7, 1, 0, 0, 0, 0)

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
    }

    void testCoverGrossPerils() {
        ClaimsGenerator marine = new ClaimsGenerator(name: 'marine')
        ClaimsGenerator motor = new ClaimsGenerator(name: 'motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.PERILS,
                        ['perils': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Covered Perils'], IPerilMarker),]),])
        quotaShareMarine.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.perils.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.PERILS,
                        ['perils': new ComboBoxTableMultiDimensionalParameter([['motor']], ['Covered Perils'], IPerilMarker),]),])
        quotaShareMotor.name = 'motor'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.perils.comboBoxValues['motor'] = motor


        List<ClaimCashflowPacket> marineClaims = getClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarine)
        contracts.addSubComponent(quotaShareMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims)

        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarine, 'outClaimsCeded').result
        List quotaShareMotorCededClaims = new TestProbe(quotaShareMotor, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered marine claims", 1, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()

        assertEquals "number of covered motor claims", 1, quotaShareMotorCededClaims.size()
        assertEquals "ceded motor claim value", 120, quotaShareMotorCededClaims[0].ultimate()
    }

    void testCoverGrossSegments() {
        Segment marine = new Segment(name: 'marine')
        Segment motor = new Segment(name: 'motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Covered Perils'], ISegmentMarker),]),])
        quotaShareMarine.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['motor']], ['Covered Perils'], ISegmentMarker),]),])
        quotaShareMotor.name = 'motor'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.segments.comboBoxValues['motor'] = motor

        List<ClaimCashflowPacket> marineClaims = getClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarine)
        contracts.addSubComponent(quotaShareMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims)

        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarine, 'outClaimsCeded').result
        List quotaShareMotorCededClaims = new TestProbe(quotaShareMotor, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered marine claims", 1, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()

        assertEquals "number of covered motor claims", 1, quotaShareMotorCededClaims.size()
        assertEquals "ceded motor claim value", 120, quotaShareMotorCededClaims[0].ultimate()
    }

    void testCoverGrossPerilsSegments() {
        Segment marine = new Segment(name: 'marine')
        Segment motor = new Segment(name: 'motor')
        ClaimsGenerator attritionalMarine = new ClaimsGenerator(name: 'attritional marine')
        ClaimsGenerator attritionalMotor = new ClaimsGenerator(name: 'attritional motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.PERILSSEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Segments'], ISegmentMarker),
                                'perils': new ComboBoxTableMultiDimensionalParameter([['attritional marine']], ['Perils'], IPerilMarker),
                                'connection': LogicArguments.AND]),])
        quotaShareMarine.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.segments.comboBoxValues['marine'] = marine
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.perils.comboBoxValues['attritional marine'] = attritionalMarine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.PERILSSEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['motor']], ['Segments'], ISegmentMarker),
                                'perils': new ComboBoxTableMultiDimensionalParameter([['attritional motor']], ['Perils'], IPerilMarker),
                                'connection': LogicArguments.OR])])
        quotaShareMotor.name = 'motor'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.segments.comboBoxValues['motor'] = motor
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.perils.comboBoxValues['attritional motor'] = attritionalMotor

        List<ClaimCashflowPacket> marineClaims = getClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor], 400)
        List<ClaimCashflowPacket> motorAttritionalClaims = getClaims(periodCounter, [attritionalMotor], 500)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarine)
        contracts.addSubComponent(quotaShareMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims + motorAttritionalClaims)

        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarine, 'outClaimsCeded').result
        List quotaShareMotorCededClaims = new TestProbe(quotaShareMotor, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered marine claims", 1, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()

        assertEquals "number of covered motor claims", 2, quotaShareMotorCededClaims.size()
        assertEquals "ceded motor claim value", 120, quotaShareMotorCededClaims[0].ultimate()
        assertEquals "ceded motor claim value", 150, quotaShareMotorCededClaims[1].ultimate()
    }

    void testCoverGrossLegalEntities() {
        LegalEntity marine = new LegalEntity(name: 'marine')
        LegalEntity motor = new LegalEntity(name: 'motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.name = 'marine'
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LEGALENTITIES,
                ['legalEntityCoverMode': LegalEntityCoverMode.ORIGINALCLAIMS,
                        'legalEntities': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Covered Legal Entities'], ILegalEntityMarker),])
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMarine.parmCover).legalEntities.comboBoxValues['marine'] = marine

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LEGALENTITIES,
                ['legalEntityCoverMode': LegalEntityCoverMode.ORIGINALCLAIMS,
                        'legalEntities': new ComboBoxTableMultiDimensionalParameter([['motor']], ['Covered Legal Entities'], ILegalEntityMarker),])
        quotaShareMotor.name = 'motor'
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMotor.parmCover).legalEntities.comboBoxValues['motor'] = motor


        List<ClaimCashflowPacket> marineClaims = getClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarine)
        contracts.addSubComponent(quotaShareMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims)

        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarine, 'outClaimsCeded').result
        List quotaShareMotorCededClaims = new TestProbe(quotaShareMotor, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered marine claims", 1, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()

        assertEquals "number of covered motor claims", 1, quotaShareMotorCededClaims.size()
        assertEquals "ceded motor claim value", 120, quotaShareMotorCededClaims[0].ultimate()
    }

    void testCoverContracts() {
        Segment marine = new Segment(name: 'marine')
        Segment motor = new Segment(name: 'motor')
        ClaimsGenerator attritionalMarine = new ClaimsGenerator(name: 'attritional marine')
        ClaimsGenerator singleMarine = new ClaimsGenerator(name: 'single marine')
        ClaimsGenerator attritionalMotor = new ClaimsGenerator(name: 'attritional motor')

        ReinsuranceContract quotaShareMarineOnGross = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarineOnGross.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Segments'], ISegmentMarker),]),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts': new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']], ['Covered Contracts', 'Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                        'filter': FilterStrategyType.getDefault()])
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts': new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']], ['Covered Contracts', 'Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                        'filter': FilterStrategyType.getDefault()])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross

        List<ClaimCashflowPacket> marineClaimsAttritional = getClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> marineClaimsSingle = getClaims(periodCounter, [marine, singleMarine], 500)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor, attritionalMotor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarineOnGross)
        contracts.addSubComponent(quotaShareMarineAttritionalOnNet)
        contracts.addSubComponent(quotaShareMarineAttritionalOnCeded)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaimsAttritional + marineClaimsSingle + motorClaims)

        List contractsCededClaims = new TestProbe(contracts, 'outClaimsCeded').result
        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarineOnGross, 'outClaimsCeded').result
        List quotaShareMarineAttritionalOnNetCededClaims = new TestProbe(quotaShareMarineAttritionalOnNet, 'outClaimsCeded').result
        List quotaShareMarineAttritionalOnCededCededClaims = new TestProbe(quotaShareMarineAttritionalOnCeded, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered contracts", 6, contractsCededClaims.size()

        assertEquals "number of covered marine claims", 2, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()
        assertEquals "ceded marine claim value", 100, quotaShareMarineCededClaims[1].ultimate()

        assertEquals "number of covered motor claims", 2, quotaShareMarineAttritionalOnNetCededClaims.size()
        assertEquals "ceded motor claim value", 240, quotaShareMarineAttritionalOnNetCededClaims[0].ultimate()
        assertEquals "ceded motor claim value", 120, quotaShareMarineAttritionalOnNetCededClaims[1].ultimate()

        assertEquals "number of covered motor claims", 2, quotaShareMarineAttritionalOnCededCededClaims.size()
        assertEquals "ceded motor claim value", 60, quotaShareMarineAttritionalOnCededCededClaims[0].ultimate()
        assertEquals "ceded motor claim value", 30, quotaShareMarineAttritionalOnCededCededClaims[1].ultimate()
    }

    void testCoverContractsPerils() {
        Segment marine = new Segment(name: 'marine')
        Segment motor = new Segment(name: 'motor')
        ClaimsGenerator attritionalMarine = new ClaimsGenerator(name: 'attritional marine')
        ClaimsGenerator singleMarine = new ClaimsGenerator(name: 'single marine')
        ClaimsGenerator attritionalMotor = new ClaimsGenerator(name: 'attritional motor')

        ReinsuranceContract quotaShareMarineOnGross = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarineOnGross.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Segments'], ISegmentMarker),]),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts': new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']], ['Contracts', 'Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                        'filter': FilterStrategyType.getStrategy(FilterStrategyType.PERILS, [
                                'perils': new ComboBoxTableMultiDimensionalParameter([['attritional marine']], ['Perils'], IPerilMarker),])])
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).filter.perils.comboBoxValues['attritional marine'] = attritionalMarine
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts': new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']], ['Contracts', 'Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                        'filter': FilterStrategyType.getStrategy(FilterStrategyType.PERILS, [
                                'perils': new ComboBoxTableMultiDimensionalParameter([['attritional marine']], ['Perils'], IPerilMarker),])])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).filter.perils.comboBoxValues['attritional marine'] = attritionalMarine

        List<ClaimCashflowPacket> marineClaimsAttritional = getClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> marineClaimsSingle = getClaims(periodCounter, [marine, singleMarine], 500)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor, attritionalMotor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarineOnGross)
        contracts.addSubComponent(quotaShareMarineAttritionalOnNet)
        contracts.addSubComponent(quotaShareMarineAttritionalOnCeded)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaimsAttritional + marineClaimsSingle + motorClaims)

        List contractsCededClaims = new TestProbe(contracts, 'outClaimsCeded').result
        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarineOnGross, 'outClaimsCeded').result
        List quotaShareMarineAttritionalOnNetCededClaims = new TestProbe(quotaShareMarineAttritionalOnNet, 'outClaimsCeded').result
        List quotaShareMarineAttritionalOnCededCededClaims = new TestProbe(quotaShareMarineAttritionalOnCeded, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered contracts", 4, contractsCededClaims.size()

        assertEquals "number of covered marine claims", 2, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()
        assertEquals "ceded marine claim value", 100, quotaShareMarineCededClaims[1].ultimate()

        assertEquals "number of covered motor claims", 1, quotaShareMarineAttritionalOnNetCededClaims.size()
        assertEquals "ceded motor claim value", 240, quotaShareMarineAttritionalOnNetCededClaims[0].ultimate()

        assertEquals "number of covered motor claims", 1, quotaShareMarineAttritionalOnCededCededClaims.size()
        assertEquals "ceded motor claim value", 60, quotaShareMarineAttritionalOnCededCededClaims[0].ultimate()
    }

    void testCoverContractsSegments() {
        Segment marine = new Segment(name: 'marine')
        Segment motor = new Segment(name: 'motor')
        ClaimsGenerator attritionalMarine = new ClaimsGenerator(name: 'attritional marine')
        ClaimsGenerator singleMarine = new ClaimsGenerator(name: 'single marine')
        ClaimsGenerator attritionalMotor = new ClaimsGenerator(name: 'attritional motor')

        ReinsuranceContract quotaShareMarineOnGross = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarineOnGross.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter": FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                        ['segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Segments'], ISegmentMarker),]),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts': new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']], ['Contracts', 'Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                        'filter': FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS, [
                                'segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Segments'], ISegmentMarker),])])
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).filter.segments.comboBoxValues['marine'] = marine
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts': new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']], ['Contracts', 'Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                        'filter': FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS, [
                                'segments': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Segments'], ISegmentMarker),])])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).filter.segments.comboBoxValues['marine'] = marine


        List<ClaimCashflowPacket> marineClaimsAttritional = getClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> marineClaimsSingle = getClaims(periodCounter, [marine, singleMarine], 500)
        List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor, attritionalMotor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarineOnGross)
        contracts.addSubComponent(quotaShareMarineAttritionalOnNet)
        contracts.addSubComponent(quotaShareMarineAttritionalOnCeded)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaimsAttritional + marineClaimsSingle + motorClaims)

        List contractsCededClaims = new TestProbe(contracts, 'outClaimsCeded').result
        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarineOnGross, 'outClaimsCeded').result
        List quotaShareMarineAttritionalOnNetCededClaims = new TestProbe(quotaShareMarineAttritionalOnNet, 'outClaimsCeded').result
        List quotaShareMarineAttritionalOnCededCededClaims = new TestProbe(quotaShareMarineAttritionalOnCeded, 'outClaimsCeded').result

        contracts.start()

        assertEquals "number of covered contracts", 6, contractsCededClaims.size()

        assertEquals "number of covered marine claims", 2, quotaShareMarineCededClaims.size()
        assertEquals "ceded marine claim value", 200, quotaShareMarineCededClaims[0].ultimate()
        assertEquals "ceded marine claim value", 100, quotaShareMarineCededClaims[1].ultimate()

        assertEquals "number of covered motor claims", 2, quotaShareMarineAttritionalOnNetCededClaims.size()
        assertEquals "ceded motor claim value", 240, quotaShareMarineAttritionalOnNetCededClaims[0].ultimate()
        assertEquals "ceded motor claim value", 120, quotaShareMarineAttritionalOnNetCededClaims[1].ultimate()

        assertEquals "number of covered motor claims", 2, quotaShareMarineAttritionalOnCededCededClaims.size()
        assertEquals "ceded motor claim value", 60, quotaShareMarineAttritionalOnCededCededClaims[0].ultimate()
        assertEquals "ceded motor claim value", 30, quotaShareMarineAttritionalOnCededCededClaims[1].ultimate()
    }

    void testCoverInwardLegalEntities() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())

        LegalEntity marine = new LegalEntity(name: 'marine')
        LegalEntity motor = new LegalEntity(name: 'motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.name = 'marine'
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LEGALENTITIES,
                ['legalEntities': new ComboBoxTableMultiDimensionalParameter([['marine']], ['Legal Entities'], ILegalEntityMarker),
                        'legalEntityCoverMode': LegalEntityCoverMode.ORIGINALCLAIMS])
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMarine.parmCover).legalEntities.comboBoxValues['marine'] = marine
        quotaShareMarine.parmReinsurers = new ConstrainedMultiDimensionalParameter(
                [['motor'], [0.8d]],
                LegalEntityPortionConstraints.COLUMN_TITLES,
                ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER));
        quotaShareMarine.parmReinsurers.comboBoxValues[0] = ['motor': motor]
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LEGALENTITIES,
                ['legalEntities': new ComboBoxTableMultiDimensionalParameter([['motor']], ['Legal Entities'], ILegalEntityMarker),
                        'legalEntityCoverMode': LegalEntityCoverMode.INWARD])
        quotaShareMotor.name = 'motor'
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMotor.parmCover).legalEntities.comboBoxValues['motor'] = motor

        List<ClaimCashflowPacket> marineClaimsAttritional = getClaims(periodCounter, [marine], 1000)
//         List<ClaimCashflowPacket> marineClaimsSingle = getClaims(periodCounter, [marine, singleMarine], 500)
//         List<ClaimCashflowPacket> motorClaims = getClaims(periodCounter, [motor, attritionalMotor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(quotaShareMarine)
        contracts.addSubComponent(quotaShareMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaimsAttritional) // + marineClaimsSingle + motorClaims)

        List contractsCededClaims = new TestProbe(contracts, 'outClaimsCeded').result
        List quotaShareMarineCededClaims = new TestProbe(quotaShareMarine, 'outClaimsCeded').result
//         List quotaShareMarineAttritionalOnNetCededClaims = new TestProbe(quotaShareMarineAttritionalOnNet, 'outClaimsCeded').result
//         List quotaShareMarineAttritionalOnCededCededClaims = new TestProbe(quotaShareMarineAttritionalOnCeded, 'outClaimsCeded').result

        contracts.start()

    }

    void testCoverOriginalAndContractCover() {
        Segment marine = new Segment(name: 'marine')
        ClaimsGenerator attritionalMarine = new ClaimsGenerator(name: 'attritional marine')

        ReinsuranceContract xl500xs500 = ReinsuranceContractTests.getWXLContract(500, 500, 500, 200, date20110101)
        xl500xs500.name = 'xl 500 xs 500'
        xl500xs500.parmVirtual = true
        ReinsuranceContract quoteOnXLNet = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quoteOnXLNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ["contracts": new ConstrainedMultiDimensionalParameter([["xl 500 xs 500"], ["NET"]], ["Covered Contracts", "Based On"],
                        ConstraintsFactory.getConstraints('RI_CONTRACT_BASEDON')),
                        "filter": FilterStrategyType.getStrategy(FilterStrategyType.ALL, [:]),])
        ((ContractsCoverAttributeStrategy) quoteOnXLNet.parmCover).contracts.comboBoxValues['xl 500 xs 500'] = xl500xs500
        quoteOnXLNet.name = 'Quote on XL net'

        IPeriodCounter periodCounter = xl500xs500.iterationScope.periodScope.periodCounter

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(xl500xs500)
        contracts.addSubComponent(quoteOnXLNet)
        contracts.internalWiring()
        contracts.inClaims.addAll(getClaims(periodCounter, [marine, attritionalMarine], 700d, ClaimType.SINGLE))
        contracts.inUnderwritingInfo.addAll(grossUnderwritingInfo(1000d, marine, periodCounter))

        List contractsCededClaims = new TestProbe(contracts, 'outClaimsCeded').result
        List xl500xs500CededClaims = new TestProbe(xl500xs500, 'outClaimsCeded').result
        List quoteOnXLNetCededClaims = new TestProbe(quoteOnXLNet, 'outClaimsCeded').result

        List contractsCededUwInfo = new TestProbe(contracts, 'outUnderwritingInfoCeded').result
        List xl500xs500CededUwInfo = new TestProbe(xl500xs500, 'outUnderwritingInfoCeded').result
        List quoteOnXLNetCededUwInfo = new TestProbe(quoteOnXLNet, 'outUnderwritingInfoCeded').result

        contracts.start()

        assertEquals "number of covered claims", 1, contractsCededClaims.size()
        assertEquals "number of covered claims XL 500 xs 500", 1, xl500xs500CededClaims.size()
        assertEquals "number of covered claims quoteOnXLNet", 1, quoteOnXLNetCededClaims.size()
        assertEquals "ceded claim value", 100, contractsCededClaims[0].ultimate()
        assertEquals "ceded XL claim value", 200, xl500xs500CededClaims[0].ultimate()
        assertEquals "ceded quote claim value", 100, quoteOnXLNetCededClaims[0].ultimate()

        assertEquals "number of covered uw info", 1, contractsCededUwInfo.size()
        assertEquals "number of covered uw info XL 500 xs 500", 1, xl500xs500CededUwInfo.size()
        assertEquals "number of covered uw info quoteOnXLNet", 1, quoteOnXLNetCededUwInfo.size()
        assertEquals "ceded uw info value", -200, contractsCededUwInfo[0].premiumWritten
        assertEquals "ceded XL uw info value", -200, xl500xs500CededUwInfo[0].premiumWritten
        // -200 as it is based on GNPI
        assertEquals "ceded quote uw info value", -200, quoteOnXLNetCededUwInfo[0].premiumWritten

    }

    private List<ClaimCashflowPacket> getClaims(IPeriodCounter periodCounter, List<IComponentMarker> markers, double ultimate, ClaimType claimType = ClaimType.AGGREGATED) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, claimType,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)
        markers.each { claims*.setMarker(it) }
        return claims
    }

    private List<UnderwritingInfoPacket> grossUnderwritingInfo(double premium, ISegmentMarker segment, IPeriodCounter periodCounter) {
        [new UnderwritingInfoPacket(segment: segment, premiumWritten: premium, premiumPaid: premium, numberOfPolicies: 1,
                exposure: new ExposureInfo(date20110101, periodCounter))]
    }
}
