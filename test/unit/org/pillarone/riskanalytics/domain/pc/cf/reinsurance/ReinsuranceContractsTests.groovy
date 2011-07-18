package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractTests
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.GrossPerilsCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.GrossSegmentsCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.GrossLegalEntitiesCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.GrossPerilsSegmentsCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ContractsPerilsCoverAttributeStrategy
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ContractBasedOn

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

    void testCoverGrossPerils() {
        ClaimsGenerator marine = new ClaimsGenerator(name: 'marine')
        ClaimsGenerator motor = new ClaimsGenerator(name: 'motor')

        ReinsuranceContract quotaShareMarine = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSPERILS,
                ['perils':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Perils'], IPerilMarker),])
        quotaShareMarine.name = 'marine'
        ((GrossPerilsCoverAttributeStrategy) quotaShareMarine.parmCover).perils.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSPERILS,
                ['perils':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Perils'], IPerilMarker),])
        quotaShareMotor.name = 'motor'
        ((GrossPerilsCoverAttributeStrategy) quotaShareMotor.parmCover).perils.comboBoxValues['motor'] = motor


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
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSSEGMENTS,
                ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Segments'], ISegmentMarker),])
        quotaShareMarine.name = 'marine'
        ((GrossSegmentsCoverAttributeStrategy) quotaShareMarine.parmCover).segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSSEGMENTS,
                ['segments':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Segments'], ISegmentMarker),])
        quotaShareMotor.name = 'motor'
        ((GrossSegmentsCoverAttributeStrategy) quotaShareMotor.parmCover).segments.comboBoxValues['motor'] = motor


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
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSPERILSSEGMENTS,
                ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Segments'], ISegmentMarker),
                 'perils':new ComboBoxTableMultiDimensionalParameter([['attritional marine']],['Covered Perils'], IPerilMarker),
                 'connection': LogicArguments.AND])
        quotaShareMarine.name = 'marine'
        ((GrossPerilsSegmentsCoverAttributeStrategy) quotaShareMarine.parmCover).segments.comboBoxValues['marine'] = marine
        ((GrossPerilsSegmentsCoverAttributeStrategy) quotaShareMarine.parmCover).perils.comboBoxValues['attritional marine'] = attritionalMarine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSPERILSSEGMENTS,
                ['segments':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Segments'], ISegmentMarker),
                 'perils':new ComboBoxTableMultiDimensionalParameter([['attritional motor']],['Covered Perils'], IPerilMarker),
                 'connection': LogicArguments.OR])
        quotaShareMotor.name = 'motor'
        ((GrossPerilsSegmentsCoverAttributeStrategy) quotaShareMotor.parmCover).segments.comboBoxValues['motor'] = motor
        ((GrossPerilsSegmentsCoverAttributeStrategy) quotaShareMotor.parmCover).perils.comboBoxValues['attritional motor'] = attritionalMotor


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
        quotaShareMarine.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSLEGALENTITIES,
                ['legalEntities':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Legal Entities'], ILegalEntityMarker),])
        ((GrossLegalEntitiesCoverAttributeStrategy) quotaShareMarine.parmCover).legalEntities.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarine.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMotor = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMotor.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSLEGALENTITIES,
                ['legalEntities':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Legal Entities'], ILegalEntityMarker),])
        quotaShareMotor.name = 'motor'
        ((GrossLegalEntitiesCoverAttributeStrategy) quotaShareMotor.parmCover).legalEntities.comboBoxValues['motor'] = motor


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
        ConstraintsFactory.registerConstraint(new ContractBasedOn())

        Segment marine = new Segment(name: 'marine')
        Segment motor = new Segment(name: 'motor')
        ClaimsGenerator attritionalMarine = new ClaimsGenerator(name: 'attritional marine')
        ClaimsGenerator singleMarine = new ClaimsGenerator(name: 'single marine')
        ClaimsGenerator attritionalMotor = new ClaimsGenerator(name: 'attritional motor')

        ReinsuranceContract quotaShareMarineOnGross = ReinsuranceContractTests.getQuotaShareContract(0.2, date20110101)
        quotaShareMarineOnGross.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSSEGMENTS,
                ['segments':new ComboBoxTableMultiDimensionalParameter([['marine']],['Covered Segments'], ISegmentMarker),])
        quotaShareMarineOnGross.name = 'qs marine, gross'
        ((GrossSegmentsCoverAttributeStrategy) quotaShareMarineOnGross.parmCover).segments.comboBoxValues['marine'] = marine
        IPeriodCounter periodCounter = quotaShareMarineOnGross.iterationScope.periodScope.periodCounter

        ReinsuranceContract quotaShareMarineAttritionalOnNet = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnNet.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTSPERILS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['NET']],['Covered Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'perils':new ComboBoxTableMultiDimensionalParameter([['attritional marine']],['Covered Perils'], IPerilMarker),])
        quotaShareMarineAttritionalOnNet.name = 'qs marine, attritional net'
        ((ContractsPerilsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsPerilsCoverAttributeStrategy) quotaShareMarineAttritionalOnNet.parmCover).perils.comboBoxValues['attritional marine'] = attritionalMarine

        ReinsuranceContract quotaShareMarineAttritionalOnCeded = ReinsuranceContractTests.getQuotaShareContract(0.3, date20110101)
        quotaShareMarineAttritionalOnCeded.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTSPERILS,
                ['contracts':new ConstrainedMultiDimensionalParameter([['qs marine, gross'], ['CEDED']],['Covered Contracts','Based On'],
                        ConstraintsFactory.getConstraints('CONTRACT_BASEDON')),
                 'perils':new ComboBoxTableMultiDimensionalParameter([['attritional marine']],['Covered Perils'], IPerilMarker),])
        quotaShareMarineAttritionalOnCeded.name = 'qs marine, attritional ceded'
        ((ContractsPerilsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).contracts.comboBoxValues['qs marine, gross'] = quotaShareMarineOnGross
        ((ContractsPerilsCoverAttributeStrategy) quotaShareMarineAttritionalOnCeded.parmCover).perils.comboBoxValues['attritional marine'] = attritionalMarine

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

    private List<ClaimCashflowPacket> grossClaims(IPeriodCounter periodCounter, List<IComponentMarker> perils, double ultimate) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)
        perils.each { peril -> claims*.setMarker(peril) }
        return claims
    }
}
