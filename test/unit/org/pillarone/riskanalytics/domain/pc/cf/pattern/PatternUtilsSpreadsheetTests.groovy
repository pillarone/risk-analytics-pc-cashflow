package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.IAggregateActualClaimsStrategy
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime

import org.grails.plugins.excelimport.ExpectedPropertyType
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.joda.time.LocalDate
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.AggregateActualClaimsStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.AggregateHistoricClaimsConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PatternUtilsSpreadsheetTests extends SpreadsheetUnitTest {

    static Log LOG = LogFactory.getLog(PatternUtilsSpreadsheetTests.class);
    static final private double EPSILON = 1E-7

    @Override
    List<String> getSpreadsheetNames() {
        [
         'ART-687-1-CODNoInPeriodStartPeriod.xlsx',
         'ART-687-2-CODYesInPeriodMiddleActualClaims.xlsx',
         'ART-687-3-CODYesLongTimeIntoFuture.xlsx',
         'ART-687-4-CODYesVeryLongTimeIntoFuture.xlsx',
         'ART-687-5-PSDYesMiddleOfActualPeriod.xlsx',
         'ART-687-6-PSDYesFirstQuarter.xlsx',
         'ART-687-7-PSDYesFirstQuarterUpdateEqualsReported.xlsx',
         'ART-687-8-PSDNoAfterUpdateDate2ndPeriod.xlsx',
         'ART-687-9-CODNoAfterUpdateDate2ndPeriod.xlsx',
         'ART-687-10-CODYesPeriod1_TwoUpdates.xlsx',
         'ART-687-11-CODNoPeriod1_Period3.xlsx',
         'ART-687-12-PSDNoPeriod1.xlsx',
        ]
    }


    void testUsage() {
        for (SpreadsheetImporter importer: importers) {
            // enable the following line while writing/debugging the test case but comment it out before committing!
//            SP - Why?
            setCheckedForValidationErrors( true );
            LOG.info(importer.fileName);
            Integer grossClaimPeriod  = generalParameters(importer).claimPeriod - 1
            PatternPacket originalPattern = pattern(importer, 'Pattern')
            DateTime coverStartDate = generalParameters(importer).startCoverDate.toDateTimeAtStartOfDay()
            DateTime periodStartDate = coverStartDate.plusYears(grossClaimPeriod)
            DateTime occurrenceDate = generalParameters(importer).occurrenceDate.toDateTimeAtStartOfDay()
            PayoutPatternBase payoutPatternBase = generalParameters(importer). payoutPatternBase == 'Claim Occurance Date' ? PayoutPatternBase.CLAIM_OCCURANCE_DATE : PayoutPatternBase.PERIOD_START_DATE
            int periods = (originalPattern.cumulativePeriods[-1].months + 12) / 12
            IPeriodCounter periodCounter = periodCounter(importer, periods)
            DateTime updateDate = generalParameters(importer).updateDate.toDateTimeAtStartOfDay()
            Double ultimate = generalParameters(importer).ultimate
            TreeMap<DateTime, Double> claimUpdates = claimUpdates(importer, 'Claims', 0, periodCounter, updateDate, payoutPatternBase, grossClaimPeriod)
            DateTime baseDate = payoutPatternBase.equals(PayoutPatternBase.CLAIM_OCCURANCE_DATE) ? occurrenceDate : periodStartDate
            DateTime lastReportedDate = claimUpdates.lastEntry() == null ? updateDate : claimUpdates.lastEntry().getKey()
            PatternPacket adjustedPattern = PatternUtils.adjustedPattern(originalPattern, claimUpdates, ultimate,
                    baseDate, occurrenceDate, updateDate, lastReportedDate, DateTimeUtilities.Days360.US)

            List<ClaimCashflowPacket> claims = []

            GrossClaimRoot claimRoot = new GrossClaimRoot(new ClaimRoot(ultimate, ClaimType.AGGREGATED, periodStartDate, occurrenceDate), adjustedPattern, baseDate)
//            claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
            for (int period = 0; period <= periods; period++) {
                if(period >= grossClaimPeriod) {
                    claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, null, false))
                }
                periodCounter.next()
            }
            List<Map<String, Object>> payments = futurePayments(importer)
            int index = 0
            for (ClaimCashflowPacket claim : claims) {
                if (claim.ultimate() > 0) {
                    LOG.info "${importer.fileName}\n\tultimate @ ${claim.occurrenceDate} ${claim.ultimate()}"
                    assertEquals "ultimate$index", ultimate, claim.ultimate()
                    assertEquals "occurrence date", occurrenceDate, claim.occurrenceDate
                    index++
                }
                else {
                    LOG.info "\tincremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
                }
                if (claim.getPaidIncrementalIndexed() > 0) {
                    assertEquals "incremental$index @ ${claim.getUpdateDate()}", payments[index]['futurePayment'], claim.getPaidIncrementalIndexed(), EPSILON
                    assertEquals "payoutdates$index", payments[index]['paymentDate'], claim.getUpdateDate().toLocalDate()
                    index++
                }
            }
            assertEquals "total", ultimate, claims*.getPaidIncrementalIndexed().sum(), EPSILON

            manageValidationErrors(importer)
        }
    }

    Map generalParameters(SpreadsheetImporter importer) {
        importer.cells([sheet: 'Usage',
                cellMap: ['F3': 'startCoverDate', 'F4': 'updateDate', 'F5': 'payoutPatternBase', 'F8': 'ultimate',
                        'F9' : 'claimPeriod',
                          'F10': 'occurrenceDate', 'F5': 'payoutPatternBase']])
    }

    List<Map<String, Object>> futurePayments(SpreadsheetImporter importer) {
        Map map = [sheet: 'Results', startRow: 1,
                columnMap: ['A' : 'paymentDate', 'B' : 'futurePayment']]
        importer.columns(map, FUTURE_PAYMENTS_VALIDATION)
    }

    private static Map FUTURE_PAYMENTS_VALIDATION = [
            paymentDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue:  new LocalDate(2012,1,1)]),
            futurePayment : ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d])
    ]

    IPeriodCounter periodCounter(SpreadsheetImporter importer, int periods) {
        Map params = generalParameters(importer)
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
            reportedDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)])
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

    TreeMap<DateTime, Double> claimUpdates(SpreadsheetImporter importer, String sheet, int period,
                                           IPeriodCounter periodCounter, DateTime updateDate,
                                           PayoutPatternBase payoutPatternBase, Integer testContractPeriod) {
        IAggregateActualClaimsStrategy actualClaims = actualClaims(importer, 'Claims', payoutPatternBase, testContractPeriod)
        TreeMap<DateTime, Double> updates = actualClaims.historicClaims(testContractPeriod - 1, periodCounter, updateDate, payoutPatternBase, true)?.claimPaidUpdates
        updates ? updates : new TreeMap<DateTime, Double>()
    }

    IAggregateActualClaimsStrategy actualClaims(SpreadsheetImporter importer, String sheet, PayoutPatternBase payoutPatternBase, Integer contractPeriod1) {
        List claimsMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)
        List<Integer> contractPeriod = []
        List<Double> reportedValues = []
        List<Double> paidValues = []
        List<LocalDate> reportedDates = []
        for (Map claim : claimsMap) {
            if(claim.reported > 0 || claim.reported > 0 ) {
                contractPeriod << contractPeriod1
                reportedValues << claim.reported
                paidValues << claim.paid
                reportedDates << claim.reportedDate.toDateTimeAtStartOfDay()
            }
        }
        return AggregateActualClaimsStrategyType.getStrategy(
                AggregateActualClaimsStrategyType.AGGREGATE,
                [history: new ConstrainedMultiDimensionalParameter([
                        contractPeriod, reportedValues, paidValues, reportedDates],
                        AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                        ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER)),
                 payoutPatternBase: payoutPatternBase]
        )
    }

    PatternPacket pattern(SpreadsheetImporter importer, String sheet) {
        List patternMap = importer.columns(patternSheetStructure(sheet), PATTERN_VALIDATION)
        List<Period> periods = []
        for (Double period : patternMap.month) {
            periods << Period.months((Integer) period)
        }
        List<Double> cumulatedValues = []
        double cumulatedValue = 0
        for (Double increment : patternMap.incremental) {
            cumulatedValue += increment
            cumulatedValues << cumulatedValue
        }
        PatternPacket pattern = new PatternPacket(IUpdatingPatternMarker.class, cumulatedValues, periods)
        pattern.origin = new UpdatingPattern(name: '48m')
        pattern
    }

    private Map patternSheetStructure(String sheet) {
        [
                sheet: sheet, startRow:  1,
                columnMap:  ['A' : 'month', 'B': 'incremental']
        ]
    }

    private static Map PATTERN_VALIDATION = [
            month : ([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            incremental : ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0])
    ]
}
