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

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractsTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
    }

    void testCoverGrossPerils() {
        ClaimsGenerator marine = new ClaimsGenerator(name: 'marine')
        ClaimsGenerator motor = new ClaimsGenerator(name: 'motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.PERILS,
                    ['perils':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Perils'], IPerilMarker),]),])
        quotaShareMarine.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.perils.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.PERILS,
                    ['perils':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Perils'], IPerilMarker),]),])
        quotaShareMotor.name = 'motor'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.perils.comboBoxValues['motor'] = motor


        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)

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
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Perils'], ISegmentMarker),]),])
        quotaShareMarine.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Perils'], ISegmentMarker),]),])
        quotaShareMotor.name = 'motor'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.segments.comboBoxValues['motor'] = motor

        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)

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
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.PERILSSEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Segments'], ISegmentMarker),
                     'perils':new ComboBoxTableMultiDimensionalParameter([['attritional marine']],['Perils'], IPerilMarker),
                     'connection': LogicArguments.AND]),])
        quotaShareMarine.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.segments.comboBoxValues['marine'] = marine
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarine.parmCover).filter.perils.comboBoxValues['attritional marine'] = attritionalMarine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.PERILSSEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['motor']],['Segments'], ISegmentMarker),
                     'perils':new ComboBoxTableMultiDimensionalParameter([['attritional motor']],['Perils'], IPerilMarker),
                     'connection': LogicArguments.OR])])
        quotaShareMotor.name = 'motor'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.segments.comboBoxValues['motor'] = motor
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMotor.parmCover).filter.perils.comboBoxValues['attritional motor'] = attritionalMotor

        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)
        List<ClaimCashflowPacket> motorAttritionalClaims = grossClaims(periodCounter, [attritionalMotor], 500)

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
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.INWARDLEGALENTITIES,
                ['activeReMode' : ActiveReMode.ORIGINALCLAIMS,
                 'legalEntities' : new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Legal Entities'], ILegalEntityMarker),])
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMarine.parmCover).legalEntities.comboBoxValues['marine'] = marine

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.INWARDLEGALENTITIES,
                ['activeReMode' : ActiveReMode.ORIGINALCLAIMS,
                 'legalEntities':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Legal Entities'], ILegalEntityMarker),])
        quotaShareMotor.name = 'motor'
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMotor.parmCover).legalEntities.comboBoxValues['motor'] = motor


        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)

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
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Segments'], ISegmentMarker),]),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']],['Covered Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'filter': FilterStrategyType.getDefault()])
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']],['Covered Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'filter': FilterStrategyType.getDefault()])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross

        List<ClaimCashflowPacket> marineClaimsAttritional = grossClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> marineClaimsSingle = grossClaims(periodCounter, [marine, singleMarine], 500)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor, attritionalMotor], 400)

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
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Segments'], ISegmentMarker),]),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']],['Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'filter': FilterStrategyType.getStrategy(FilterStrategyType.PERILS, [
                    'perils':new ComboBoxTableMultiDimensionalParameter([['attritional marine']],['Perils'], IPerilMarker),])])
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).filter.perils.comboBoxValues['attritional marine'] = attritionalMarine
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']],['Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'filter': FilterStrategyType.getStrategy(FilterStrategyType.PERILS, [
                    'perils':new ComboBoxTableMultiDimensionalParameter([['attritional marine']],['Perils'], IPerilMarker),])])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).filter.perils.comboBoxValues['attritional marine'] = attritionalMarine

        List<ClaimCashflowPacket> marineClaimsAttritional = grossClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> marineClaimsSingle = grossClaims(periodCounter, [marine, singleMarine], 500)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor, attritionalMotor], 400)

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
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Segments'], ISegmentMarker),]),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((OriginalClaimsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).filter.segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']],['Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'filter': FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS, [
                    'segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Segments'], ISegmentMarker),])])
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).filter.segments.comboBoxValues['marine'] = marine
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']],['Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'filter': FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS, [
                    'segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Segments'], ISegmentMarker),])])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).filter.segments.comboBoxValues['marine'] = marine


        List<ClaimCashflowPacket> marineClaimsAttritional = grossClaims(periodCounter, [marine, attritionalMarine], 1000)
        List<ClaimCashflowPacket> marineClaimsSingle = grossClaims(periodCounter, [marine, singleMarine], 500)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor, attritionalMotor], 400)

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
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.INWARDLEGALENTITIES,
                ['legalEntities':new ComboBoxTableMultiDimensionalParameter([['marine']],['Legal Entities'], ILegalEntityMarker),
                 'activeReMode': ActiveReMode.INWARD])
        ((InwardLegalEntitiesCoverAttributeStrategy) quotaShareMarine.parmCover).legalEntities.comboBoxValues['marine'] = marine
        quotaShareMarine.parmReinsurers = new ConstrainedMultiDimensionalParameter(
                [['motor'], [0.8d]],
                LegalEntityPortionConstraints.COLUMN_TITLES,
                ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER));
        quotaShareMarine.parmReinsurers.comboBoxValues[0] = ['motor': motor]
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

//        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
//        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSLEGALENTITIES,
//                ['legalEntities':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Legal Entities'], ILegalEntityMarker),])
//        quotaShareMotor.name = 'motor'
//        ((GrossLegalEntitiesCoverAttributeStrategy) quotaShareMotor.parmCover).legalEntities.comboBoxValues['motor'] = motor

         List<ClaimCashflowPacket> marineClaimsAttritional = grossClaims(periodCounter, [marine], 1000)
//         List<ClaimCashflowPacket> marineClaimsSingle = grossClaims(periodCounter, [marine, singleMarine], 500)
//         List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor, attritionalMotor], 400)

         ReinsuranceContracts contracts = new ReinsuranceContracts()
         contracts.addSubComponent(quotaShareMarine)
         contracts.internalWiring()
         contracts.inClaims.addAll(marineClaimsAttritional) // + marineClaimsSingle + motorClaims)

         List contractsCededClaims = new TestProbe(contracts, 'outClaimsCeded').result
         List quotaShareMarineCededClaims = new TestProbe(quotaShareMarine, 'outClaimsCeded').result
//         List quotaShareMarineAttritionalOnNetCededClaims = new TestProbe(quotaShareMarineAttritionalOnNet, 'outClaimsCeded').result
//         List quotaShareMarineAttritionalOnCededCededClaims = new TestProbe(quotaShareMarineAttritionalOnCeded, 'outClaimsCeded').result

         contracts.start()

    }

    private List<ClaimCashflowPacket> grossClaims(IPeriodCounter periodCounter, List<IComponentMarker> perils, double ultimate) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)
        perils.each { peril -> claims*.setMarker(peril) }
        return claims
    }
}
