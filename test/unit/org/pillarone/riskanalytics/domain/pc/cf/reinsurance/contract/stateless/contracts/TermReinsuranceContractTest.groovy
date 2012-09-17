package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ContractBasedOn
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.SelectedCoverStrategy

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ContractBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.CoverStrategyType

/**
*   author simon.parten @ art-allianz . com
 */
class TermReinsuranceContractTest extends GroovyTestCase {
    public static final Double EPSILON = 1E-10

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)

    void setUp() {
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
        ConstraintsFactory.registerConstraint(new AdditionalPremiumConstraints())
        ConstraintsFactory.registerConstraint(new LayerConstraints())
        ConstraintsFactory.registerConstraint(new ContractBasedOn())
    }

    void testAllGrossClaims() {
        AttritionalClaimsGenerator marine = new AttritionalClaimsGenerator(name: 'marine')
        AttritionalClaimsGenerator motor = new AttritionalClaimsGenerator(name: 'motor')

        TermReinsuranceContract xlMarine = TermContractTestUtils.getOneLayerContract(500, 500, date20110101)
        xlMarine.parmCover = CoverStrategyType.getStrategy(CoverStrategyType.ALLGROSSCLAIMS, [:])
        xlMarine.name = 'marine'
        IPeriodCounter periodCounter = xlMarine.iterationScope.periodScope.periodCounter

        TermReinsuranceContract xlMotor = TermContractTestUtils.getOneLayerContract(300, 200, date20110101)
        xlMotor.parmCover = CoverStrategyType.getStrategy(CoverStrategyType.ALLGROSSCLAIMS, [:])
        xlMotor.name = 'motor'


        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(xlMarine)
        contracts.addSubComponent(xlMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims)

        List xlMarineCededClaims = new TestProbe(xlMarine, 'outClaimsCeded').result
        List xlMotorCededClaims = new TestProbe(xlMotor, 'outClaimsCeded').result

        contracts.start()

        assertEquals "marine: number of claims", 2, xlMarineCededClaims.size()
        assertEquals "marine: ceded claim value", [500, 0], xlMarineCededClaims*.ultimate()

        assertEquals "motor: number of claims", 2, xlMotorCededClaims.size()
        assertEquals "motor: ceded claim value", [200, 100], xlMotorCededClaims*.ultimate()
    }

    void testSelectedPerils() {
        AttritionalClaimsGenerator marine = new AttritionalClaimsGenerator(name: 'marine')
        AttritionalClaimsGenerator motor = new AttritionalClaimsGenerator(name: 'motor')

        TermReinsuranceContract xlMarine = TermContractTestUtils.getOneLayerContract(500, 500, date20110101)
        xlMarine.parmCover = CoverStrategyType.getStrategy(CoverStrategyType.SELECTED,
                ['grossClaims': new ComboBoxTableMultiDimensionalParameter(['marine'], ['Covered gross claims'], IPerilMarker.class),
//                        'claimFilters': new ComboBoxTableMultiDimensionalParameter([], ['Covered gross claims'], IClaimFilterMarker.class),
                        'structures': new ConstrainedMultiDimensionalParameter([[], []],
                                [ContractBasedOn.CONTRACT, ContractBasedOn.BASED_ON],
                                ConstraintsFactory.getConstraints(ContractBasedOn.IDENTIFIER))])
        ((SelectedCoverStrategy) xlMarine.parmCover).grossClaims.comboBoxValues['marine'] = marine
        xlMarine.name = 'marine'
        IPeriodCounter periodCounter = xlMarine.iterationScope.periodScope.periodCounter

        TermReinsuranceContract xlMotor = TermContractTestUtils.getOneLayerContract(300, 200, date20110101)
        xlMotor.parmCover = CoverStrategyType.getStrategy(CoverStrategyType.SELECTED,
                ['grossClaims': new ComboBoxTableMultiDimensionalParameter(['motor'], ['Covered gross claims'], IPerilMarker.class),
//                        'claimFilters': new ComboBoxTableMultiDimensionalParameter([], ['Covered gross claims'], IClaimFilterMarker.class),
                        'structures': new ConstrainedMultiDimensionalParameter([[], []],
                                [ContractBasedOn.CONTRACT, ContractBasedOn.BASED_ON],
                                ConstraintsFactory.getConstraints(ContractBasedOn.IDENTIFIER))])
        ((SelectedCoverStrategy) xlMotor.parmCover).grossClaims.comboBoxValues['motor'] = motor
        xlMotor.name = 'motor'


        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(xlMarine)
        contracts.addSubComponent(xlMotor)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims)

        List quotaShareMarineCededClaims = new TestProbe(xlMarine, 'outClaimsCeded').result
        List quotaShareMotorCededClaims = new TestProbe(xlMotor, 'outClaimsCeded').result

        contracts.start()

        assertEquals "marine: number of claims", 1, quotaShareMarineCededClaims.size()
        assertEquals "marine: ceded claim value", [500], quotaShareMarineCededClaims*.ultimate()

        assertEquals "motor: number of claims", 1, quotaShareMotorCededClaims.size()
        assertEquals "motor: ceded claim value", [100], quotaShareMotorCededClaims*.ultimate()
    }

    void testSelectedPerilsStructures() {
        AttritionalClaimsGenerator marine = new AttritionalClaimsGenerator(name: 'marine')
        AttritionalClaimsGenerator motor = new AttritionalClaimsGenerator(name: 'motor')

        TermReinsuranceContract xlAllPerils = TermContractTestUtils.getOneLayerContract(500, 500, date20110101)
        xlAllPerils.parmCover = CoverStrategyType.getStrategy(CoverStrategyType.ALLGROSSCLAIMS, [:])
        xlAllPerils.name = 'xl all perils'
        IPeriodCounter periodCounter = xlAllPerils.iterationScope.periodScope.periodCounter

        TermReinsuranceContract xlAfterXLAllPerilsMarineOnly = TermContractTestUtils.getOneLayerContract(300, 200, date20110101)
        xlAfterXLAllPerilsMarineOnly.parmCover = CoverStrategyType.getStrategy(CoverStrategyType.SELECTED,
                ['grossClaims': new ComboBoxTableMultiDimensionalParameter(['marine'], ['Covered gross claims'], IPerilMarker.class),
//                        'claimFilters': new ComboBoxTableMultiDimensionalParameter([], ['Covered gross claims'], IClaimFilterMarker.class),
                        'structures': new ConstrainedMultiDimensionalParameter([['xl all perils'], [ContractBase.CEDED.toString()]],
                                [ContractBasedOn.CONTRACT, ContractBasedOn.BASED_ON],
                                ConstraintsFactory.getConstraints(ContractBasedOn.IDENTIFIER))])
        ((SelectedCoverStrategy) xlAfterXLAllPerilsMarineOnly.parmCover).grossClaims.comboBoxValues['marine'] = marine
        ((SelectedCoverStrategy) xlAfterXLAllPerilsMarineOnly.parmCover).structures.comboBoxValues['xl all perils'] = xlAllPerils
        xlAfterXLAllPerilsMarineOnly.name = 'xl after xl all perils, marine only'


        List<ClaimCashflowPacket> marineClaims = grossClaims(periodCounter, [marine], 1000)
        List<ClaimCashflowPacket> motorClaims = grossClaims(periodCounter, [motor], 400)

        ReinsuranceContracts contracts = new ReinsuranceContracts()
        contracts.addSubComponent(xlAllPerils)
        contracts.addSubComponent(xlAfterXLAllPerilsMarineOnly)
        contracts.internalWiring()
        contracts.inClaims.addAll(marineClaims + motorClaims)

        List xlAllPerilsCededClaims = new TestProbe(xlAllPerils, 'outClaimsCeded').result
        List xlAfterXLAllPerilCededClaims = new TestProbe(xlAfterXLAllPerilsMarineOnly, 'outClaimsCeded').result

        contracts.start()

        assertEquals "xlAllPerils: number of claims", 2, xlAllPerilsCededClaims.size()
        assertEquals "xlAllPerils: ceded claim value", [500, 0], xlAllPerilsCededClaims*.ultimate()

        assertEquals "xlAfterXLAllPerilsMarineOnly: number of claims", 1, xlAfterXLAllPerilCededClaims.size()
        assertEquals "xlAfterXLAllPerilsMarineOnly: ceded claim value", [200], xlAfterXLAllPerilCededClaims*.ultimate()
    }

    private List<ClaimCashflowPacket> grossClaims(IPeriodCounter periodCounter, List<IComponentMarker> perils, double ultimate, ClaimType claimType = ClaimType.AGGREGATED) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, claimType, date20110418, date20110701,
                TermContractTestUtils.trivialPayoutPattern, TermContractTestUtils.trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, new ArrayList<Factors>(), true)
        perils.each { peril -> claims*.setMarker(peril) }
        return claims
    }
}

