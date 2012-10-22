package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts

import com.google.common.collect.ListMultimap
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ContractBasedOn
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.pillarone.riskanalytics.domain.pc.cf.global.AnnualPeriodStrategy
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.components.IterationStore

/**
 * This spreadsheet test does not check any reinstatements and AP calculations as they are not implemented so far
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class StatelessSpreadsheetContractTests extends SpreadsheetUnitTest {

    static final double EPSILON = 1E-8

    static DateTime beginOfCover = new DateTime(2012,1,1,0,0,0,0)

    static StatelessRIContract getArtLayerContract(Double termExcess, Double termLimit, List<Integer> periods,
                                                      List<Integer> layers, List<Double> shares,
                                                      List<Double> periodExcess, List<Double> periodLimit,
                                                      List<Double> claimExcess, List<Double> claimLimit, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        int numberOfLayers = layers.size()
        return new StatelessRIContract(
                parmContractStructure: TemplateContractType.getStrategy(
                        TemplateContractType.NONPROPORTIONAL,
                        ['termLimit': termLimit,
                                'termExcess': termExcess,
                                'termAP': new ConstrainedMultiDimensionalParameter(
                                        [[0d],[0d],[0d]], AdditionalPremiumConstraints.columnHeaders,
                                        ConstraintsFactory.getConstraints(AdditionalPremiumConstraints.IDENTIFIER)),
                                'structure': new ConstrainedMultiDimensionalParameter(
                                        [periods, layers, shares, periodLimit, periodExcess, claimLimit, claimExcess,
                                                [0d] * numberOfLayers, ['PREMIUM'] * numberOfLayers],
                                        LayerConstraints.columnHeaders, ConstraintsFactory.getConstraints(LayerConstraints.IDENTIFIER))]),
                iterationScope: iterationScope,
                iterationStore: new IterationStore(iterationScope),
                periodScope: iterationScope.getPeriodScope(),
                globalCover: new AnnualPeriodStrategy(startCover: beginOfCover),
                periodStore: iterationScope.periodStores[0])

    }

    static StatelessRIContract getLayerContract(Double termExcess, Double termLimit, List<Integer> periods,
                                                List<Integer> layers, List<Double> shares,
                                                List<Double> periodExcess, List<Double> periodLimit,
                                                List<Double> claimExcess, List<Double> claimLimit, DateTime beginOfCover) {

        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        int numberOfLayers = layers.size()
        return new StatelessRIContract(
                parmContractStructure: TemplateContractType.getStrategy(
                        TemplateContractType.NONPROPORTIONAL,
                        ['termLimit': termLimit,
                                'termExcess': termExcess,
                                'termAP': new ConstrainedMultiDimensionalParameter(
                                        [[0d],[0d],[0d]], AdditionalPremiumConstraints.columnHeaders,
                                        ConstraintsFactory.getConstraints(AdditionalPremiumConstraints.IDENTIFIER)),
                                'structure': new ConstrainedMultiDimensionalParameter(
                                        [periods, layers, shares, periodLimit, periodExcess, claimLimit, claimExcess,
                                                [0d] * numberOfLayers, ['PREMIUM'] * numberOfLayers],
                                        LayerConstraints.columnHeaders, ConstraintsFactory.getConstraints(LayerConstraints.IDENTIFIER))]),
                iterationScope: iterationScope,
                iterationStore: new IterationStore(iterationScope),
                periodScope: iterationScope.getPeriodScope(),
                globalCover: new AnnualPeriodStrategy(startCover: beginOfCover),
                periodStore: iterationScope.periodStores[0])

    }

    static StatelessRIContract getARTLayerContract(Double termExcess, Double termLimit, TermContractTestUtils.TestLayers layers, DateTime beginOfCover) {
        return getArtLayerContract(termExcess, termLimit, layers.periods, layers.layers, layers.shares, layers.periodExcess,
                layers.periodLimit, layers.claimExcess, layers.claimLimits, beginOfCover)
    }

    static StatelessRIContract getLayerContract(Double termExcess, Double termLimit, TermContractTestUtils.TestLayers layers, DateTime beginOfCover) {
        return getLayerContract(termExcess, termLimit, layers.periods, layers.layers, layers.shares, layers.periodExcess,
                layers.periodLimit, layers.claimExcess, layers.claimLimits, beginOfCover)
    }

    void doSetUp() {
        ConstraintsFactory.registerConstraint(new AdditionalPremiumConstraints())
        ConstraintsFactory.registerConstraint(new LayerConstraints())
        ConstraintsFactory.registerConstraint(new ContractBasedOn())
    }

    @Override
    List<String> getSpreadsheetNames() {
        [
                'ART-686-NonPropStructure.xlsx',
        ]
    }

    void testUsage() {
        // enable the following line while writing/debugging the test case but comment it out before committing!
        setCheckedForValidationErrors(true)
        List<String> sheets = [
                'Module',
                'Test1',
                'Test2b',
                'Test3',
                'Test4',
                'Test5',
                'Test6'
        ]

        SpreadsheetImporter importer = importers[0]
        for (String sheet : sheets) {
            ListMultimap<Integer, Double> ultimatesPerPeriod = TermContractTestUtils.getUltimatesByPeriod(importer, sheet)
            PatternPacket payoutPattern = getPayoutPattern(importer, sheet)
            TermContractTestUtils.TestLayers layerPeriodParams = getLayers(importer, sheet)

            StatelessRIContract contract = getARTLayerContract(
                    termExcess(importer, sheet),
                    termLimit(importer, sheet),
                    layerPeriodParams, beginOfCover
            )
            IPeriodCounter periodCounter = contract.iterationScope.periodScope.periodCounter
            Map<Integer, TestCededClaimValues> cededClaims = cededClaims(importer, sheet)

            List<GrossClaimRoot> claimRoots = []

            int lastPeriodWithNewClaims = ultimatesPerPeriod.keys().max()
            int projectionPeriods = Math.min(lastPeriodWithNewClaims, layerPeriodParams.maxPeriod) + payoutPattern.lastCumulativePeriod.months / 12d

            for (int period = 0; period <= projectionPeriods; period++) {
                DateTime occurrenceDate = beginOfCover.plusMonths(period * 12)
                claimRoots.addAll TermContractTestUtils.getClaimRoots(ultimatesPerPeriod.get(period), occurrenceDate, payoutPattern)
                List<ClaimCashflowPacket> claims = []
                for (GrossClaimRoot claimRoot : claimRoots) {
                    claims.addAll claimRoot.getClaimCashflowPackets(periodCounter)
                }

                contract.inClaims.addAll claims
                for (ClaimCashflowPacket grossClaim : claims) {
                    println "gross $period $grossClaim"
                }
                contract.doCalculation()
                for (ClaimCashflowPacket cededClaim : contract.outClaimsCeded) {
                    println "ceded $period $cededClaim"
                }

                if (contract.outClaimsCeded.size() > 0) {
                    assertEquals "[${importer.fileName}, $sheet] period: $period correct ceded ultimates", cededClaims.get(period).ultimate, (Double) contract.outClaimsCeded*.ultimate().sum(), EPSILON
                    assertEquals "[${importer.fileName}, $sheet] period: $period correct ceded paid", cededClaims.get(period).paid, (Double) contract.outClaimsCeded.paidIncrementalIndexed.sum(), EPSILON
                    int calendarYear = beginOfCover.year
                    assertEquals "[${importer.fileName}, $sheet] period: $period correct ceded paid CP1", cededClaims.get(period).paidCY1, TermContractTestUtils.paidSumOfCalendarYear(contract.outClaimsCeded, calendarYear), EPSILON
                    assertEquals "[${importer.fileName}, $sheet] period: $period correct ceded paid CP2", cededClaims.get(period).paidCY2, TermContractTestUtils.paidSumOfCalendarYear(contract.outClaimsCeded, ++calendarYear), EPSILON
                    assertEquals "[${importer.fileName}, $sheet] period: $period correct ceded paid CP3", cededClaims.get(period).paidCY3, TermContractTestUtils.paidSumOfCalendarYear(contract.outClaimsCeded, ++calendarYear), EPSILON
                }
                contract.reset()
                contract.iterationScope.periodScope.prepareNextPeriod()
            }
        }
        manageValidationErrors(importer)
    }

    private Map<Integer, TestCededClaimValues> cededClaims(SpreadsheetImporter importer, String sheet) {
        List<String> columns = ['C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']
        Map<Integer, TestCededClaimValues> cededClaims = [:]
        int column = 0
        for (int period = 0; period < columns.size(); period++) {
            cededClaims.put(period, new TestCededClaimValues(importer.cells([
                    sheet: sheet, cellMap: [
                    "${columns[column]}81": 'ultimate',
                    "${columns[column]}82": 'paid',
                    "${columns[column]}83": 'paidCY1',
                    "${columns[column]}84": 'paidCY2',
                    "${columns[column]}85": 'paidCY3',
            ]
            ])))
            column++
        }
        cededClaims
    }

    private PatternPacket getPayoutPattern(SpreadsheetImporter importer, String sheet) {
        List<Double> cumulativePayouts = []
        Map payouts = importer.cells([sheet: sheet, cellMap:  ['C17': 'dev0', 'D17': 'dev1', 'E17': 'dev2', 'F17': 'dev3', 'G17': 'dev4', 'H17': 'dev5']])
        double cumulativePayout = 0
        for (int i = 0; i < 6; i++) {
            Double payout = (Double) payouts["dev$i"]
            if (payout == null) {
                payout = 0d
            }
            cumulativePayout += payout
            cumulativePayouts << cumulativePayout
        }
        List<Integer> periods = []
        for (int period = 0; period < cumulativePayouts.size(); period++) {
            periods << period * 12
        }
        PatternPacketTests.getPattern(periods, cumulativePayouts)
    }

    Double termLimit(SpreadsheetImporter importer, String sheet) {
        Double termLimit = (Double) importer.cells([sheet: sheet, cellMap:  ['C58': 'termLimit']])['termLimit']
        if (termLimit == null) {
            termLimit = Double.MAX_VALUE
        }
        termLimit
    }

    Double termExcess(SpreadsheetImporter importer, String sheet) {
        Double termExcess = (Double) importer.cells([sheet: sheet, cellMap:  ['C59': 'termExcess']])['termExcess']
        if (termExcess == null) {
            termExcess = 0
        }
        termExcess
    }

    TermContractTestUtils.TestLayers getLayers(SpreadsheetImporter importer, String sheet) {
        TermContractTestUtils.TestLayers layers = new TermContractTestUtils.TestLayers()
        List<String> columns = ['C', 'D', 'E', 'I', 'J', 'K']
        int column = 0
        for (int layer = 0; layer < 2; layer++) {
            for (int period = 1; period < 4; period++) {
                layers.add(new TermContractTestUtils.TestLayerPeriodContractParams(layer, period, importer.cells([
                        sheet: sheet, cellMap: [
                        "${columns[column]}66": 'share',
                        "${columns[column]}67": 'claimLimit', "${columns[column]}68": 'claimExcess',
                        "${columns[column]}69": 'periodLimit', "${columns[column]}70": 'periodExcess'
                ]])))
                column++
            }
        }
        layers.fillListMembers()
        layers
    }

    private class TestCededClaimValues {
        double ultimate
        double paid
        double paidCY1
        double paidCY2
        double paidCY3

        TestCededClaimValues(Map<String, Double> params) {
            ultimate = params['ultimate'] ? params['ultimate'] : 0d
            paid = params['paid'] ? params['paid'] : 0d
            paidCY1 = params['paidCY1'] ? params['paidCY1'] : 0d
            paidCY2 = params['paidCY2'] ? params['paidCY2'] : 0d
            paidCY3 = params['paidCY3'] ? params['paidCY3'] : 0d
        }

        @Override
        String toString() {
            "$ultimate, $paid ($paidCY1, $paidCY2, $paidCY3)"
        }
    }
}
