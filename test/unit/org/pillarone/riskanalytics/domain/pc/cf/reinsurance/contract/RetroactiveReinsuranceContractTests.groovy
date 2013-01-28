package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract

import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.TestTriangle
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective.UnifiedADCLPTBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest

class RetroactiveReinsuranceContractTests extends SpreadsheetUnitTest {
    private static final String RETROACTIVE = 'Retroactive'

    @Override
    List<String> getSpreadsheetNames() {
        ['PMO-2235_Spec.xlsx']
    }

    @Override
    void doSetUp() {
        super.doSetUp()
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    void testReadData() {
        // enable the following line while writing/debugging the test case but comment it out before committing!
//        setCheckedForValidationErrors(true)
        for (SpreadsheetImporter spreadsheet : importers) {
            doTest(spreadsheet)
            manageValidationErrors(spreadsheet)
        }
    }

    void doTest(SpreadsheetImporter spreadsheet) {
        Map contractValues = spreadsheet.cells([sheet: 'Retroactive', cellMap: [F3: 'cededShare', F6: 'absolute',
                F8: 'attachmentPoint', F9: 'limit', F12: 'coveredFrom', F13: 'coveredTo', F14: 'coveredDevelopmentPeriod']])
        contractValues.contractBase = contractValues.absolute.asBoolean() ? UnifiedADCLPTBase.ABSOLUTE : UnifiedADCLPTBase.OUTSTANDING_PERCENTAGE

        def midnight = new LocalTime(0,0,0)
        DateTime coveredFrom = contractValues.coveredFrom.toDateTime(midnight)
        DateTime coveredTo = contractValues.coveredTo.toDateTime(midnight)
        DateTime coveredDevelopmentPeriod = contractValues.coveredDevelopmentPeriod.toDateTime(midnight)
        ReinsuranceContract contract = new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.UNIFIEDADCLPT, contractValues),
                parmCover: CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                parmCoveredPeriod: PeriodStrategyType.getStrategy(PeriodStrategyType.RETROACTIVE, [
                        coveredOccurencePeriodFrom: coveredFrom,
                        coveredOccurencePeriodTo: coveredTo,
                        coveredDevelopmentPeriodStartDate: coveredDevelopmentPeriod
                ])
        )
        Map dates = spreadsheet.cells([sheet: RETROACTIVE, cellMap: [E17: 'firstDevelopment', R17: 'lastDevelopment', D18: 'firstOccurrence', D27: 'lastOccurrence']])
        int startYear = dates.firstOccurrence.year
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(new DateTime(startYear,1,1,0,0,0,0), 10)

        contract.iterationScope = iterationScope
        contract.periodStore = iterationScope.periodStores[0]
        IPeriodCounter periodCounter = iterationScope.periodScope.periodCounter
        int numberOfUnderwritingYears = new Period(dates.firstOccurrence, dates.lastOccurrence).years + 1
        int numberOfDevelopmentYears = new Period(dates.firstDevelopment, dates.lastDevelopment).years + 1

        TestTriangle grossClaimsUltimates = new TestTriangle(spreadsheet, RETROACTIVE, 'gross ultimates', startYear, numberOfUnderwritingYears, numberOfDevelopmentYears, 'E46', -1)
        TestTriangle grossClaimsReported = new TestTriangle(spreadsheet, RETROACTIVE, 'gross reported', startYear, numberOfUnderwritingYears, numberOfDevelopmentYears, 'E31', -1)
        TestTriangle grossClaimsPaid = new TestTriangle(spreadsheet, RETROACTIVE, 'gross paid', startYear, numberOfUnderwritingYears, numberOfDevelopmentYears, 'E18', -1)
        Map<Integer, GrossClaimRoot> claimsByPeriod = getClaimsByUnderwritingPeriod(grossClaimsUltimates, grossClaimsReported, grossClaimsPaid)

        for (int developmentPeriod = 0; developmentPeriod < numberOfDevelopmentYears; developmentPeriod++) {
            int minPeriod = Math.min(startYear + developmentPeriod, startYear + numberOfUnderwritingYears - 1);
            for (int underwritingPeriod = startYear; underwritingPeriod <= minPeriod; underwritingPeriod++) {
                contract.inClaims.addAll claimsByPeriod.get(underwritingPeriod).getClaimCashflowPackets(periodCounter)
            }
            println "year:${developmentPeriod + startYear} before calc ${contract.inClaims.size()}"
            contract.doCalculation()
            println "year:${developmentPeriod + startYear} after calc ${contract.inClaims.size()}"

            contract.reset()
            iterationScope.periodScope.prepareNextPeriod()
        }

        def expectedValues = [:]
        ('E'..'R').eachWithIndex { col, colIdx ->
            (18..27).eachWithIndex { row, rowIdx ->
                expectedValues.put("$col$row", "cell_row${rowIdx}_col${colIdx}")
            }
        }
        def resultValues = spreadsheet.cells([sheet: 'Retroactive', cellMap: expectedValues])

        def expectedPaidCumulative = getValues(spreadsheet, 'E'..'R', 60, 'paidCumulative')
        def expectedReported = getValues(spreadsheet, 'E'..'R', 61, 'reported')
        def expectedTotalCumulative = getValues(spreadsheet, 'E'..'R', 62, 'totalCumulative')
        def expectedOutstanding = getValues(spreadsheet, 'E'..'R', 63, 'outstanding')

        def valuesAtStartOfCover = spreadsheet.cells([sheet: 'Retroactive', cellMap: ['E66': 'paidCumulativeAtStartOfCover', 'E67': 'reportedAtStartOfCover', 'E68': 'totalCumulativeAtStartOfCover', 'E69': 'outstandingr']])
        println valuesAtStartOfCover

    }

    /**
     *
     * @param ultimateTriangle
     * @param reportedTriangle
     * @param paidTriangle
     * @return key: year
     */
    private static Map<Integer, GrossClaimRoot> getClaimsByUnderwritingPeriod(TestTriangle ultimateTriangle, TestTriangle reportedTriangle, TestTriangle paidTriangle) {
        Map<Integer, GrossClaimRoot> claimsByOccurrenceYear = [:]
        for (DateTime startOfUnderwritingPeriod : reportedTriangle.underwritingPeriodStartDates) {
            List<Period> cummulativePeriods = reportedTriangle.cummulativePeriods(startOfUnderwritingPeriod)
            List<Double> cummulativeReportedValues = reportedTriangle.valuesBy(startOfUnderwritingPeriod)
            List<Double> cummulativePaidValues = paidTriangle.valuesBy(startOfUnderwritingPeriod)
            List<Double> cummulativeReportedRatios = []
            List<Double> cummulativePaidRatios = []
            for (Double value : cummulativeReportedValues) {
                cummulativeReportedRatios << value / ultimateTriangle.latestValue(startOfUnderwritingPeriod)
            }
            for (Double value : cummulativePaidValues) {
                cummulativePaidRatios  << value / ultimateTriangle.latestValue(startOfUnderwritingPeriod)
            }
            PatternPacket reportingPattern = new PatternPacket(IPayoutPatternMarker, cummulativeReportedRatios, cummulativePeriods)
            PatternPacket payoutPattern = new PatternPacket(IPayoutPatternMarker, cummulativePaidRatios, cummulativePeriods)
            claimsByOccurrenceYear[startOfUnderwritingPeriod.year] = new GrossClaimRoot(
                    new ClaimRoot(ultimateTriangle.latestValue(startOfUnderwritingPeriod), ClaimType.ATTRITIONAL,
                            startOfUnderwritingPeriod, startOfUnderwritingPeriod), payoutPattern, reportingPattern)
        }
        return claimsByOccurrenceYear
    }

    private def getValues(SpreadsheetImporter importer, Range columRange, int rowIndex, String prefix) {
        def cellMap = [:]
        ('E'..'R').eachWithIndex { it, i ->
            cellMap.put("${it}$rowIndex", "${prefix}_$i")
        }
        return importer.cells([sheet: 'Retroactive', cellMap: cellMap])
    }
}

