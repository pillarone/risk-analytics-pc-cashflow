package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.grails.plugins.excelimport.ExpectedPropertyType
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.pattern.ReportingPattern
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutPattern
import org.apache.commons.lang.builder.HashCodeBuilder
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareEventLimitContractSpreadsheetTests extends SpreadsheetUnitTest {

    void doSetUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    @Override
    List<String> getSpreadsheetNames() {
        ["QuotaShareEventLimit.xlsx"]
    }

    void testUsage() {
        // enable the following line while writing/debugging the test case but comment it out before committing!
        setCheckedForValidationErrors(true)
        SpreadsheetImporter importer = importers[0]
        ReinsuranceContract contract = getQuotaShareContract(importer)
        IterationScope iterationScope = contract.iterationScope
        PatternPacket reportingPattern = reportingPattern(importer, 'Pattern')
        PatternPacket payoutPattern = payoutPattern(importer, 'Pattern')
        Map<Integer, List<GrossClaimRoot>> claimsByPeriod = getClaims(importer, 'GrossClaims', -1, reportingPattern, payoutPattern)
        initCededReferenceClaims(importer, 'CededClaims', reportingPattern)
        int maxPeriod = payoutPattern.cumulativePeriods[-1].months / 12 + claimsByPeriod.keySet().max()
        for (int period = 0; period <= maxPeriod; period++) {
            for (GrossClaimRoot claimRoot : claimsByPeriod.get(0)) {
                contract.inClaims.addAll claimRoot.getClaimCashflowPackets(iterationScope.periodScope.periodCounter)
            }
            if (period > 0) {
                for (GrossClaimRoot claimRoot : claimsByPeriod.get(1)) {
                    contract.inClaims.addAll claimRoot.getClaimCashflowPackets(iterationScope.periodScope.periodCounter)
                }
            }
            contract.doCalculation()
            for (ClaimCashflowPacket cededClaim : contract.outClaimsCeded) {
                ReferenceClaim referenceClaim = cededReferenceClaims.get(cededClaim)
                assertEquals "[${importer.fileName}] correct ceded ultimate ${referenceClaim.dateSummary()}", referenceClaim.ultimate, cededClaim.ultimate(), EPSILON
                assertEquals "[${importer.fileName}] correct ceded reported ${referenceClaim.dateSummary()}", referenceClaim.reported, cededClaim.reportedIncrementalIndexed, EPSILON
                assertEquals "[${importer.fileName}] correct ceded paid ${referenceClaim.dateSummary()}", referenceClaim.paid, cededClaim.paidIncrementalIndexed, EPSILON
            }
            contract.reset()
            iterationScope.periodScope.prepareNextPeriod()
        }
        //        manageValidationErrors(importer)
    }

    private ReinsuranceContract getQuotaShareContract(SpreadsheetImporter importer) {
        Map contractParams = importer.cells([sheet: 'Calc', cellMap: ['B21': 'quote', 'B22': 'eventLimit']])
        QuotaShareContractTests.getQuotaShareContractEventLimit(contractParams.quote, contractParams.eventLimit, beginOfCover, 2)
    }

    private Map claimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 2, // startRow counting starts at 0
                columnMap: ['A' : 'period', 'B' : 'event', 'C' : 'eventDate', 'D' : 'occurrenceDate', 'E': 'inceptionDate', 'F' : 'ultimate']
        ]
    }

    private static Map CLAIMS_VALIDATION = [
            period:([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            event:([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            eventDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            occurrenceDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            inceptionDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            ultimate:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
    ]

    private Map<Integer, List<GrossClaimRoot>> getClaims(SpreadsheetImporter importer, String sheet, Double sign,
                                                         PatternPacket reportingPattern, PatternPacket payoutPattern) {
        List claimsMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)
        Map<Integer, List<GrossClaimRoot>> claimsByPeriod = [:]
        Map<Integer, EventPacket> eventByNumber = [:]
        for (Map claim : claimsMap) {
            int eventNumber = claim.event
            DateTime eventDate = claim.eventDate?.toDateTimeAtStartOfDay()
            DateTime occurrenceDate = claim.occurrenceDate.toDateTimeAtStartOfDay()
            DateTime inceptionDate = claim.inceptionDate.toDateTimeAtStartOfDay()
            GrossClaimRoot baseClaim
            if (eventNumber == null || eventNumber == 0) {
                baseClaim = new GrossClaimRoot(sign * claim.ultimate, ClaimType.AGGREGATED, inceptionDate,
                    occurrenceDate, payoutPattern, reportingPattern)
            }
            else {
                EventPacket event = eventByNumber.get(eventNumber)
                if (event == null) {
                    event = new EventPacket(eventDate)
                    eventByNumber.put(eventNumber, event)
                }
                baseClaim = new GrossClaimRoot(sign * claim.ultimate, ClaimType.AGGREGATED_EVENT, inceptionDate,
                        occurrenceDate, payoutPattern, reportingPattern, event)
            }
            int period = claim.period
            List<GrossClaimRoot> periodClaims = claimsByPeriod.get(period)
            if (periodClaims == null) {
                periodClaims = []
                claimsByPeriod.put(period, periodClaims)
            }
            periodClaims << baseClaim
        }
        claimsByPeriod
    }

    private Map cededClaimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 2, // startRow counting starts at 0
                columnMap: ['A' : 'period', 'B' : 'event', 'C' : 'eventDate', 'D' : 'occurrenceDate', 'E': 'inceptionDate',
                        'F' : 'ultimate', 'G' : 'reportedDev0', 'H' : 'reportedDev1', 'I' : 'reportedDev2', 'J' : 'reportedDev3',
                        'K' : 'reportedDev4', 'L' : 'paidDev0', 'M' : 'paidDev1', 'N' : 'paidDev2', 'O' : 'paidDev3', 'P' : 'paidDev4']
        ]
    }

    private static Map CEDED_CLAIMS_VALIDATION = [
            period:([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            event:([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            eventDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            occurrenceDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            inceptionDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            ultimate:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            reportedDev0:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            reportedDev1:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            reportedDev2:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            reportedDev3:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            reportedDev4:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            paidDev0:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            paidDev1:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            paidDev2:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            paidDev3:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            paidDev4:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
    ]

    private void initCededReferenceClaims(SpreadsheetImporter importer, String sheet, PatternPacket reportingPattern) {
        cededReferenceClaims = new ReferenceClaimContainer()
        List claimsMap = importer.columns(cededClaimsSheetStructure(sheet), CEDED_CLAIMS_VALIDATION)
        for (Map claim : claimsMap) {
            DateTime updateDate = claim.occurrenceDate?.toDateTimeAtStartOfDay()
            DateTime occurrenceDate = claim.occurrenceDate?.toDateTimeAtStartOfDay()
            DateTime inceptionDate = claim.inceptionDate?.toDateTimeAtStartOfDay()
            DateTime eventDate = claim.eventDate?.toDateTimeAtStartOfDay()
            cededReferenceClaims.add(
                    new ReferenceClaim(occurrenceDate, inceptionDate, eventDate, updateDate,
                                       claim.ultimate, claim.reportedDev0, claim.paidDev0))
            updateDate = occurrenceDate.plus(reportingPattern.cumulativePeriods.get(1))
            cededReferenceClaims.add(
                    new ReferenceClaim(occurrenceDate, inceptionDate, eventDate, updateDate,
                            0, claim.reportedDev1, claim.paidDev1))
            updateDate = occurrenceDate.plus(reportingPattern.cumulativePeriods.get(2))
            cededReferenceClaims.add(
                    new ReferenceClaim(occurrenceDate, inceptionDate, eventDate, updateDate,
                            0, claim.reportedDev2, claim.paidDev2))
            updateDate = occurrenceDate.plus(reportingPattern.cumulativePeriods.get(3))
            cededReferenceClaims.add(
                    new ReferenceClaim(occurrenceDate, inceptionDate, eventDate, updateDate,
                            0, claim.reportedDev3, claim.paidDev3))
            updateDate = occurrenceDate.plus(reportingPattern.cumulativePeriods.get(4))
            cededReferenceClaims.add(
                    new ReferenceClaim(occurrenceDate, inceptionDate, eventDate, updateDate,
                            0, claim.reportedDev4, claim.paidDev4))
        }
    }

    PatternPacket reportingPattern(SpreadsheetImporter importer, String sheet) {
        List patternMap = importer.columns(patternSheetStructure(sheet), PATTERN_VALIDATION)
        List<Period> periods = []
        for (Double period : patternMap.month) {
            periods << Period.months((Integer) period)
        }
        List<Double> cumulatedValues = []
        double cumulatedValue = 0
        for (Double increment : patternMap.reportedIncremental) {
            cumulatedValue += increment
            cumulatedValues << cumulatedValue
        }
        PatternPacket pattern = new PatternPacket(IReportingPatternMarker.class, cumulatedValues, periods)
        pattern.origin = new ReportingPattern(name: 'reporting36m')
        pattern
    }

    PatternPacket payoutPattern(SpreadsheetImporter importer, String sheet) {
        List patternMap = importer.columns(patternSheetStructure(sheet), PATTERN_VALIDATION)
        List<Period> periods = []
        for (Double period : patternMap.month) {
            periods << Period.months((Integer) period)
        }
        List<Double> cumulatedValues = []
        double cumulatedValue = 0
        for (Double increment : patternMap.paidIncremental) {
            cumulatedValue += increment
            cumulatedValues << cumulatedValue
        }
        PatternPacket pattern = new PatternPacket(IPayoutPatternMarker.class, cumulatedValues, periods)
        pattern.origin = new PayoutPattern(name: 'reporting36m')
        pattern
    }

    private Map patternSheetStructure(String sheet) {
        [
                sheet: sheet, startRow:  1,
                columnMap:  ['A' : 'month', 'B': 'reportedIncremental', 'C': 'paidIncremental']
        ]
    }

    private static Map PATTERN_VALIDATION = [
            month : ([expectedType: ExpectedPropertyType.IntType, defaultValue: 0]),
            reportedIncremental : ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0]),
            paidIncremental : ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0])
    ]

    private DateTime beginOfCover = new DateTime(2012,1,1,0,0,0,0)

    private ReferenceClaimContainer cededReferenceClaims

    private static final double EPSILON = 1E-8


    private class ReferenceClaimContainer {

        Map<ReferenceClaimKey, ReferenceClaim> container = [:]

        void add(ReferenceClaim claim) {
            container.put(new ReferenceClaimKey(claim.occurrenceDate, claim.updateDate), claim)
        }

        ReferenceClaim get(ClaimCashflowPacket claim) {
            container.get(new ReferenceClaimKey(claim.occurrenceDate, claim.updateDate))
        }
    }


    private class ReferenceClaimKey {
        DateTime occurrenceDate
        DateTime updateDate

        ReferenceClaimKey(DateTime occurrenceDate, DateTime updateDate) {
            this.occurrenceDate = occurrenceDate
            this.updateDate = updateDate
        }

        @Override
        int hashCode() {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(occurrenceDate);
            hashCodeBuilder.append(updateDate);
            return hashCodeBuilder.toHashCode();
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof ReferenceClaimKey) {
                return ((ReferenceClaimKey) obj).occurrenceDate.equals(occurrenceDate) && ((ReferenceClaimKey) obj).updateDate.equals(updateDate)
            } else {
                return false;
            }
        }

        @Override
        String toString() {
            "${format(occurrenceDate)} (${format(updateDate)})"
        }
    }


    private class ReferenceClaim {

        DateTime occurrenceDate
        DateTime inceptionDate
        DateTime eventDate
        DateTime updateDate

        double ultimate
        double reported
        double paid

        ReferenceClaim(DateTime occurrenceDate, DateTime inceptionDate, DateTime eventDate, DateTime updateDate, double ultimate, double reported, double paid) {
            this.occurrenceDate = occurrenceDate
            this.inceptionDate = inceptionDate
            this.eventDate = eventDate
            this.updateDate = updateDate
            this.ultimate = ultimate
            this.reported = reported
            this.paid = paid
        }

        String dateSummary() {
            "${format(occurrenceDate)} (${format(updateDate)})"
        }

        @Override
        String toString() {
            "${dateSummary()} $ultimate $reported $paid"
        }
    }

    private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy"
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT)

    /**
     * @param dateTime
     * @return "dd.MM.yyyy"
     */
    private static String format(DateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return formatter.print(dateTime);
    }
}
