package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.grails.plugins.excelimport.ExpectedPropertyType
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.UpdatingPattern
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): reading results from formulas and not pasted as values
// todo(sku): try to include additional validations (ranges) to correspond with the spreadsheet
class AggregateUpdatingMethodSpreadsheetTests extends SpreadsheetUnitTest {

    static final private double EPSILON = 1E-7

    static Log LOG = LogFactory.getLog(AggregateUpdatingMethodSpreadsheetTests.class);

    @Override
    List<String> getSpreadsheetNames() {
        [
                "AggregateBFUsage.xlsx",
                "AggregateBFUpdateAtStart.xlsx",
                "AggregateBFRunOff.xlsx",
                "AggregateBFUpdateEnd2ndPeriod.xlsx",
        ]
    }

    void testUsage() {
        for (SpreadsheetImporter importer: importers) {

            LOG.info(importer.fileName);

            List<ClaimRoot> baseClaims = baseClaims(importer)
            List<Integer> periods = numberYears(importer)
            IAggregateActualClaimsStrategy actualClaims = actualClaims(importer, 'Claims')
            IPeriodCounter periodCounter = periodCounter(importer)
            DateTime updateDate = generalParameters(importer).updateDate.toDateTimeAtStartOfDay()
            PatternPacket pattern = pattern(importer, 'Pattern')

            ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker, pattern.origin.name)
            updatingPattern.selectedComponent = pattern.origin

            IAggregateUpdatingMethodologyStrategy updatingMethodology = new AggregateUpdatingBFReportingMethodology(updatingPattern: updatingPattern)
            List<ClaimRoot> allUpdatedClaims = new ArrayList<ClaimRoot>();
            int j = 0
            for (ClaimRoot baseClaim : baseClaims) {
                List<ClaimRoot> updatedClaims = updatingMethodology.updatingUltimate([baseClaim], actualClaims,
                        periodCounter, updateDate, [pattern], periods.get(j), DateTimeUtilities.Days360.US, PayoutPatternBase.PERIOD_START_DATE  )
                allUpdatedClaims.addAll(updatedClaims)
            }

            List<Double> referenceAdjustedUltimates = referenceAdjustedUltimate(importer)
            for (int i = 0; i < referenceAdjustedUltimates.size(); i++) {
                assertEquals "[${importer.fileName}] correct adjusted ultimates $i", referenceAdjustedUltimates[i], allUpdatedClaims[i].getUltimate(), EPSILON
            }

            manageValidationErrors(importer)
        }
    }

    Map generalParameters(SpreadsheetImporter importer) {
        importer.cells([sheet: 'Usage',
                       cellMap: ['E9': 'startCoverDate', 'E10': 'numberOfYears', 'E12': 'updateDate',
                         'I10': 'ultimate']])

    }

    List<Double> referenceAdjustedUltimate(SpreadsheetImporter importer) {
        Map map = [sheet: 'Results', startRow: 1,
         columnMap: ['A' : 'adjustedUltimate']]
        importer.columns(map, ADJUSTED_ULTIMATE_VALIDATION)*.adjustedUltimate
    }

    private static Map ADJUSTED_ULTIMATE_VALIDATION = [adjustedUltimate : ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d])]

    IPeriodCounter periodCounter(SpreadsheetImporter importer) {
        Map params = generalParameters(importer)
        int periods = params.numberOfYears
        TestPeriodScopeUtilities.getPeriodScope(params.startCoverDate.toDateTimeAtStartOfDay(), periods).periodCounter
    }

    private Map claimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 1, // startRow counting starts at 0
                columnMap: ['A' : 'contractPeriod', 'B' : 'reported', 'C' : 'paid', 'D' : 'reportedDate']
        ]
    }

    private static Map CLAIMS_VALIDATION = [
            contractPeriod : ([expectedType: ExpectedPropertyType.IntType, defaultValue: 1]),
            reported:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            paid:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            reportedDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            adjustedUltimate:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d])
    ]

    List<ClaimRoot> baseClaims(SpreadsheetImporter importer) {
        Map params = generalParameters(importer)
        List<ClaimRoot> claims = []
        int periods = params.numberOfYears
        for (int period = 0; period < periods; period++) {
            DateTime occurrenceDate = ((LocalDate) params.startCoverDate).plusYears(period).toDateTimeAtStartOfDay()
            claims << new ClaimRoot(params.ultimate, ClaimType.AGGREGATED, params.startCoverDate.toDateTimeAtStartOfDay(), occurrenceDate)
        }
        claims
    }

    List<Integer> numberYears(SpreadsheetImporter importer) {
        Map params = generalParameters(importer)
        int periods = params.numberOfYears
        1..periods
    }

    IAggregateActualClaimsStrategy actualClaims(SpreadsheetImporter importer, String sheet) {
        List claimsMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)
        List<Integer> contractPeriod = []
        List<Double> reportedValues = []
        List<Double> paidValues = []
        List<LocalDate> reportedDates = []
        for (Map claim : claimsMap) {
            contractPeriod << claim.contractPeriod
            reportedValues << claim.reported
            paidValues << claim.paid
            reportedDates << claim.reportedDate.toDateTimeAtStartOfDay()
        }
        return AggregateActualClaimsStrategyType.getStrategy(
                AggregateActualClaimsStrategyType.AGGREGATE,
                [history: new ConstrainedMultiDimensionalParameter([
                        contractPeriod, reportedValues, paidValues, reportedDates],
                        AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                        ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER))]
        )
    }

    PatternPacket pattern(SpreadsheetImporter importer, String sheet) {
        List patternMap = importer.columns(patternSheetStructure(sheet), PATTERN_VALIDATION)
        List<Period> periods = []
        for (Double period : patternMap.month) {
            periods << Period.months((Integer) period)
        }
        PatternPacket pattern = new PatternPacket(IUpdatingPatternMarker.class, patternMap.cumulatedValue, periods)
        pattern.origin = new UpdatingPattern(name: '48m')
        pattern
    }

    private Map patternSheetStructure(String sheet) {
        [
                sheet: sheet, startRow:  1,
                columnMap:  ['A' : 'month', 'B': 'cumulatedValue']
        ]
    }

    private static Map PATTERN_VALIDATION = [
        month : ([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
        cumulatedValue : ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0])
    ]

}
