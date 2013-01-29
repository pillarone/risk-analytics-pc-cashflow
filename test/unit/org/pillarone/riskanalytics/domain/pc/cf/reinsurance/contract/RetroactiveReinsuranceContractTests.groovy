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
    private static final RESULT_OFFSET = 4
    private static final double EPSILON = 10e-8d

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

        def midnight = new LocalTime(0, 0, 0)
        DateTime coveredFrom = contractValues.coveredFrom.toDateTime(midnight)
        DateTime coveredTo = contractValues.coveredTo.toDateTime(midnight)
        DateTime coveredDevelopmentPeriod = contractValues.coveredDevelopmentPeriod.toDateTime(midnight)
        RetroactiveReinsuranceContract contract = new RetroactiveReinsuranceContract(
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
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(new DateTime(startYear, 1, 1, 0, 0, 0, 0), 10)

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
            contract.doCalculation()

            def column = getColumn(developmentPeriod + RESULT_OFFSET)
            if (minPeriod >= coveredDevelopmentPeriod.year) {
                // development period 2017 does not return values as the cover starts in 2018
                assertExpectedValue(contract.outClaimsCeded*.getPaidCumulatedIndexed(), spreadsheet, column, 84, 'Paid Cumulated')
                assertExpectedValue(contract.outClaimsCeded*.getReportedCumulatedIndexed(), spreadsheet, column, 85, 'Reported Cumulated')
                assertExpectedValue(contract.outClaimsCeded*.ultimate(), spreadsheet, column, 86, 'Total Culumated')
            }
            contract.reset()
            iterationScope.periodScope.prepareNextPeriod()
        }
    }

    String getColumn(int columnAsNumber) {
        ('A'..'Z')[columnAsNumber]
    }

    private assertExpectedValue(def calculatedValues, SpreadsheetImporter importer, String column, int row, String description) {
        def spreadsheetValue = importer.cells([sheet: 'Retroactive', cellMap: ["$column$row": 'cumulatedValue']])
        if (!spreadsheetValue) {
            assert (calculatedValues.sum() == null ? 0d : calculatedValues.sum()) == 0d
        } else {
            assertEquals("$description: ($column$row) does not contain calculated value. ${spreadsheetValue.cumulatedValue} != ${calculatedValues.sum()} ",
                    spreadsheetValue.cumulatedValue, calculatedValues.sum(), EPSILON)
        }
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
            List<Period> cumulativePeriods = reportedTriangle.cummulativePeriods(startOfUnderwritingPeriod)
            List<Double> cumulativeReportedValues = reportedTriangle.valuesBy(startOfUnderwritingPeriod)
            List<Double> cumulativePaidValues = paidTriangle.valuesBy(startOfUnderwritingPeriod)
            List<Double> cumulativeReportedRatios = []
            List<Double> cumulativePaidRatios = []
            for (Double value : cumulativeReportedValues) {
                cumulativeReportedRatios << value / ultimateTriangle.latestValue(startOfUnderwritingPeriod)
            }
            for (Double value : cumulativePaidValues) {
                cumulativePaidRatios << value / ultimateTriangle.latestValue(startOfUnderwritingPeriod)
            }
            PatternPacket reportingPattern = new PatternPacket(IPayoutPatternMarker, cumulativeReportedRatios, cumulativePeriods)
            PatternPacket payoutPattern = new PatternPacket(IPayoutPatternMarker, cumulativePaidRatios, cumulativePeriods)
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

