package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.grails.plugins.excelimport.ExpectedPropertyType
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter

import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.joda.time.Months
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimCashflowPacketSpreadsheetTests extends SpreadsheetUnitTest {

    void doSetUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    @Override
    List<String> getSpreadsheetNames() {
        ["ClaimCashflowPacket.xlsx"]
    }

    void testUsage() {
        // enable the following line while writing/debugging the test case but comment it out before committing!
//        setCheckedForValidationErrors(true)
        List<String> sheets = ['Example1527', 'OverReserving', 'NoTrendIndex']

        SpreadsheetImporter importer = importers[0]
        for (String sheet : sheets) {
            List<GrossClaimRoot> claimRoots = getClaimRoots(importer, sheet)
            List<Factors> factors = getFactors(importer, sheet)
            List<TestClaim> referenceClaims = getResults(importer, sheet)

            List<ClaimCashflowPacket> claims = []
            int numberOfPeriods = Months.monthsBetween(beginOfCover, referenceClaims.date.max()).months / 12 + 1
            IPeriodCounter periodCounter = TestPeriodScopeUtilities.getPeriodScope(beginOfCover, numberOfPeriods).periodCounter
            for (int period = 0; period < numberOfPeriods; period++) {
                for (GrossClaimRoot claimRoot : claimRoots) {
                    claims.addAll claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
                }
                periodCounter.next()
            }

            assertEquals "equal claim number", referenceClaims.size(), claims.size()
            for (int i = 0; i < referenceClaims.size(); i++) {
                equalClaimProperties(sheet, i, referenceClaims[i], claims[i])
            }
        }
        manageValidationErrors(importer)
    }

    void equalClaimProperties(String sheet, int number, TestClaim expectedClaim, ClaimCashflowPacket claim) {
        assertEquals "$sheet $number initial", expectedClaim.initial, claim.ultimate(), EPSILON
        assertEquals "$sheet $number reported cumulated", expectedClaim.reportedCumulated, claim.reportedCumulatedIndexed, EPSILON
        assertEquals "$sheet $number reported incremental", expectedClaim.reportedIncremental, claim.reportedIncrementalIndexed, EPSILON
        assertEquals "$sheet $number paid cumulated", expectedClaim.paidCumulated, claim.paidCumulatedIndexed, EPSILON
        assertEquals "$sheet $number paid incremental", expectedClaim.paidIncremental, claim.paidIncrementalIndexed, EPSILON
        assertEquals "$sheet $number oustanding", expectedClaim.outstanding, claim.outstandingIndexed(), EPSILON
        assertEquals "$sheet $number IBNR", expectedClaim.ibnr, claim.ibnrIndexed(), EPSILON
        assertEquals "$sheet $number reserves", expectedClaim.reserves, claim.reservedIndexed(), EPSILON
        assertEquals "$sheet $number change in IBNR", expectedClaim.changeInIBNR, claim.changeInIBNRIndexed, EPSILON
        assertEquals "$sheet $number change in reserves", expectedClaim.changeInReserves, claim.changeInReservesIndexed, EPSILON
        assertEquals "$sheet $number developed ultimate", expectedClaim.developedUltimate, claim.developedUltimate(), EPSILON
    }

    private Map claimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 0, // startRow counting starts at 0, first line with content
                columnMap: ['A': 'rowHeader', 'B': 'Dev0', 'C': 'Dev1', 'D': 'Dev2', 'E': 'Dev3', 'F': 'Dev4']
        ]
    }

    private static Map CLAIMS_VALIDATION = [
            months: ([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            reportingPattern: ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            payoutPattern: ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d])
    ]

    List<GrossClaimRoot> getClaimRoots(SpreadsheetImporter importer, String sheet) {
        List rowMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)

        int numberOfDevelopmentSteps = rowMap[0].size() - 1
        List<Integer> months = []
        List<Double> initials = []
        List<Double> reportedCumulated = []
        List<Double> payoutCumulated = []
        for (int devPeriod = 0; devPeriod < numberOfDevelopmentSteps; devPeriod++) {
            months << ((Double) rowMap[0]["Dev$devPeriod"]).intValue()
            reportedCumulated << rowMap[1]["Dev$devPeriod"]
            payoutCumulated << rowMap[2]["Dev$devPeriod"]
            initials << rowMap[4]["Dev$devPeriod"]
        }

        PatternPacket reportingPattern = PatternPacketTests.getPattern(months, reportedCumulated)
        PatternPacket payoutPattern = PatternPacketTests.getPattern(months, payoutCumulated)
        List<GrossClaimRoot> claimRoots = []
        DateTime occurrenceDate = beginOfCover
        for (int i = 0; i < initials.size(); i++) {
            if (initials.get(i) != 0) {
                occurrenceDate = i == 0 ? occurrenceDate.plusMonths(months.get(i)) : occurrenceDate.plusMonths(months.get(i) - months.get(i-1))
                claimRoots << new GrossClaimRoot(initials.get(i), ClaimType.ATTRITIONAL, occurrenceDate, occurrenceDate, payoutPattern, reportingPattern)
            }
        }

        claimRoots
    }

    List<Factors> getFactors(SpreadsheetImporter importer, String sheet) {
        List rowMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)

        int numberOfDevelopmentSteps = rowMap[0].size() - 1
        List<Integer> months = []
        List<Double> factorList = []
        for (int devPeriod = 0; devPeriod < numberOfDevelopmentSteps; devPeriod++) {
            months << ((Double) rowMap[0]["Dev$devPeriod"]).intValue()
            factorList << rowMap[3]["Dev$devPeriod"]
        }

        FactorsPacket factorsPacket = new FactorsPacket()
        DateTime date = beginOfCover
        factorsPacket.add(date.minusYears(1), 1)
        for (int i = 0; i < months.size(); i++) {
            date = i == 0 ? date.plusMonths(months.get(i)) : date.plusMonths(months.get(i) - months.get(i-1))
            factorsPacket.add(date, factorList.get(i))
        }

        [new Factors(factorsPacket, BaseDateMode.DAY_BEFORE_FIRST_PERIOD, IndexMode.STEPWISE_PREVIOUS, null)]
    }

    List<TestClaim> getResults(SpreadsheetImporter importer, String sheet) {
        List rowMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)

        int numberOfDevelopmentSteps = rowMap[0].size() - 1
        List<TestClaim> claims = []
        for (int devPeriod = 0; devPeriod < numberOfDevelopmentSteps; devPeriod++) {
            int month = ((Double) rowMap[0]["Dev$devPeriod"]).intValue()
            int row = 4
            claims << new TestClaim((Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    (Double) rowMap[row++]["Dev$devPeriod"],
                                    beginOfCover.plusMonths(month))
        }
        claims
    }

    private class TestClaim {
        Double initial
        Double reportedCumulated
        Double reportedIncremental
        Double paidCumulated
        Double paidIncremental
        Double outstanding
        Double ibnr
        Double changeInIBNR
        Double reserves
        Double changeInReserves
        Double developedUltimate

        DateTime date

        TestClaim(Double initial, Double reportedCumulated, Double reportedIncremental, Double paidCumulated,
                  Double paidIncremental, Double outstanding, Double ibnr, Double changeInIBNR,
                  Double reserves, Double changeInReserves, Double developedUltimate, DateTime date) {
            this.initial = initial
            this.developedUltimate = developedUltimate
            this.reportedCumulated = reportedCumulated
            this.reportedIncremental = reportedIncremental
            this.paidCumulated = paidCumulated
            this.paidIncremental = paidIncremental
            this.outstanding = outstanding
            this.ibnr = ibnr
            this.changeInIBNR = changeInIBNR
            this.reserves = reserves
            this.changeInReserves = changeInReserves
            this.date = date
        }

    }

    DateTime beginOfCover = new DateTime(2012, 1, 1, 0, 0, 0, 0)
    private static final double EPSILON = 1E-10
}

