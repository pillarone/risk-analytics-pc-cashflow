package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PatternUtilsTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    private static DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    private static DateTime date20110630 = new DateTime(2011,6,30,0,0,0,0)
    private static DateTime date20111231 = new DateTime(2011,12,31,0,0,0,0)
    private static DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)
    private static DateTime date20120401 = new DateTime(2012,4,1,0,0,0,0)
    private static DateTime date20120630 = new DateTime(2012,6,30,0,0,0,0)
    private static DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
    private static DateTime date20130401 = new DateTime(2013,4,1,0,0,0,0)
    private static DateTime date20130731 = new DateTime(2013,7,31,0,0,0,0)
    private static DateTime date20140101 = new DateTime(2014,1,1,0,0,0,0)
    private static DateTime date20140731 = new DateTime(2014,7,31,0,0,0,0)
    private static DateTime date20150101 = new DateTime(2015,1,1,0,0,0,0)
    private static DateTime date20150731 = new DateTime(2015,7,31,0,0,0,0)
    private static DateTime date20130331 = new DateTime(2013,3,31,0,0,0,0)
    private static DateTime date20120331 = new DateTime(2012,3,31,0,0,0,0)
    private static DateTime date20160101 = new DateTime(2016,1,1,0,0,0,0)
    private static DateTime date20160731 = new DateTime(2016,7,31,0,0,0,0)
    private static DateTime date20140801 = new DateTime(2014,8,1,0,0,0,0)
    private static DateTime date20150801 = new DateTime(2015,8,1,0,0,0,0)
    private static DateTime date20160801 = new DateTime(2016,8,1,0,0,0,0)
    private static DateTime date20121215 = new DateTime(2012,12,15,0,0,0,0)
    private static DateTime date20170101 = new DateTime(2017,1,1,0,0,0,0)
    private static DateTime date20180101 = new DateTime(2018,1,1,0,0,0,0)


    void testSynchronizePatternsIdenticalPeriods() {
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24], [0d, 0.2d, 0.8d, 1d])
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24], [0.3d, 0.6d, 0.95d, 1d])

        PatternPacket payoutPatternCloned = (PatternPacket) payoutPattern.clone()
        PatternPacket reportingPatternCloned = (PatternPacket) reportingPattern.clone()

        PatternUtils.synchronizePatterns(payoutPattern, reportingPattern)

        assertTrue "payout pattern", payoutPatternCloned.equals(payoutPattern)
        assertTrue "reporting pattern", reportingPatternCloned.equals(reportingPattern)
    }

    void testSynchronizePatternsIdenticalPeriodsPayoutPatternLonger() {
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 36], [0d, 0.2d, 0.8d, 0.9d, 1d])
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24], [0.3d, 0.6d, 0.95d, 1d])

        PatternPacket payoutPatternCloned = (PatternPacket) payoutPattern.clone()
        PatternPacket reportingPatternCloned = (PatternPacket) reportingPattern.clone()

        PatternUtils.synchronizePatterns(payoutPattern, reportingPattern)

        assertTrue "payout pattern", payoutPatternCloned.equals(payoutPattern)
        assertTrue "reporting pattern", reportingPatternCloned.equals(reportingPattern)
    }

    void testSynchronizePatternsIdenticalPeriodsReportingPatternLonger() {
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24], [0d, 0.2d, 0.8d, 1d])
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 36], [0.3d, 0.6d, 0.95d, 0.98d, 1d])

        shouldFail {
            PatternUtils.synchronizePatterns(payoutPattern, reportingPattern)
        }
    }

    void testSynchronizePatternsAdditionalPayouts() {
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 9, 12, 24, 36], [0d, 0.2d, 0.6d, 0.8d, 0.9d, 1d])
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24], [0.3d, 0.6d, 0.95d, 1d])

        PatternPacket payoutPatternCloned = (PatternPacket) payoutPattern.clone()

        PatternUtils.synchronizePatterns(payoutPattern, reportingPattern)

        assertTrue "payout pattern", payoutPatternCloned.equals(payoutPattern)
        assertEquals "reporting pattern value after 9 months", reportingPattern.getCumulativeValues().get(1), reportingPattern.getCumulativeValues().get(2)
        assertEquals "last element not added, lower size", 5, reportingPattern.size()
    }

    void testSynchronizePatternsAdditionalReported() {
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 36], [0d, 0.2d, 0.8d, 0.9d, 1d])
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 9, 12, 15, 24, 36], [0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.95d, 1d])

        PatternPacket reportingPatternCloned = (PatternPacket) reportingPattern.clone()

        PatternUtils.synchronizePatterns(payoutPattern, reportingPattern)

        assertEquals "payout pattern value after 9 months", payoutPattern.getCumulativeValues().get(1), payoutPattern.getCumulativeValues().get(2)
        assertEquals "payout pattern value after 15 months", payoutPattern.getCumulativeValues().get(3), payoutPattern.getCumulativeValues().get(4)
        assertTrue "reporting pattern", reportingPatternCloned.equals(reportingPattern)
    }

    void testSynchronizePatternsAdditionalReportedAndPayout() {
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 30, 36], [0d, 0.2d, 0.8d, 0.9d, 0.92d, 1d])
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 9, 12, 15, 24, 36], [0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.95d, 1d])

        PatternUtils.synchronizePatterns(payoutPattern, reportingPattern)

        assertEquals "payout pattern value after 9 months", payoutPattern.getCumulativeValues().get(1), payoutPattern.getCumulativeValues().get(2)
        assertEquals "payout pattern value after 15 months", payoutPattern.getCumulativeValues().get(3), payoutPattern.getCumulativeValues().get(4)
        assertEquals "reporting pattern value after 30 months", reportingPattern.getCumulativeValues().get(5), reportingPattern.getCumulativeValues().get(6)
    }

    void testAdjustedPattern() {
        PatternPacket originalPattern = PatternPacketTests.getPattern([15, 27, 43, 55, 67], [0.2d, 0.45d, 0.75d, 0.95d, 1d])
        DateTime periodStartDate = new DateTime(2011,1,1,0,0,0,0)
        DateTime updateDate = new DateTime(2012,12,14,0,0,0,0)


        TreeMap<DateTime, Double> claimUpdates = new TreeMap<DateTime, Double>()
        claimUpdates.put(date20110630, 500d)
        claimUpdates.put(date20111231, 1000d)
        claimUpdates.put(date20120630, 1250d)
        PatternPacket adjustedPattern = PatternUtils.adjustedPattern(originalPattern, claimUpdates, 15000d,
                periodStartDate, periodStartDate, updateDate, date20120630, DateTimeUtilities.Days360.US)

        int periods = 7
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(periodStartDate, periods);
        List<ClaimCashflowPacket> claims = []
        GrossClaimRoot claimRoot = new GrossClaimRoot(new ClaimRoot(15000, ClaimType.AGGREGATED, periodStartDate, periodStartDate), adjustedPattern)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        for (int period = 0; period < periods; period++) {
            periodCounter.next()
            claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        }

        List<Double> incrementalClaims = [0d, 500d, 500d, 250d, 3505.4092191909685d, 5587.958607714017d, 3725.305738476009d, 931.3264346190031d]
        List<DateTime> payoutDates = [date20110101, date20110630, date20111231, date20120630, date20130331, date20140731, date20150731, date20160731]

        int index = 0
        for (ClaimCashflowPacket claim : claims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental$index @ ${claim.getUpdateDate()}", incrementalClaims[index], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates$index", payoutDates[index++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, claims*.getPaidIncrementalIndexed().sum(), EPSILON

        /* Test a second development strategy */
        List<ClaimCashflowPacket> someMoreClaims = claimRoot.paidPackets(periodCounter)
        List<Double> incrementalMoreClaims = [0d, 0d, 500d, 500d, 0d, 250d, 0d, 3505.4092191909685d, 0d, 5587.958607714017d, 0d, 3725.305738476009d, 0d, 931.3264346190031d, 0d, 0d]
        List<DateTime> payoutMoreDates = [date20110101, date20110101, date20110630,  date20111231, date20120101, date20120630, date20130101,
                date20130331, date20140101, date20140731, date20150101, date20150731, date20160101, date20160731, date20170101, date20180101,]

        int anotherIndex = 0
        for (ClaimCashflowPacket claim : someMoreClaims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental $anotherIndex @ ${claim.getUpdateDate()}", incrementalMoreClaims[anotherIndex], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates $anotherIndex", payoutMoreDates[anotherIndex++], claim.getUpdateDate()
        }


        assertEquals "total", 15000d, someMoreClaims*.getPaidIncrementalIndexed().sum(), EPSILON
    }

    void testAdjustedPatternNoUpdateInFirstContractPeriod() {
        PatternPacket originalPattern = PatternPacketTests.getPattern([15, 27, 43, 55, 67], [0.2d, 0.45d, 0.75d, 0.95d, 1d])
        DateTime periodStartDate = new DateTime(2010,1,1,0,0,0,0)
        DateTime updateDate = new DateTime(2012,12,14,0,0,0,0)


        TreeMap<DateTime, Double> claimUpdates = new TreeMap<DateTime, Double>()
        claimUpdates.put(date20110630, 500d)
        claimUpdates.put(date20111231, 1000d)
        claimUpdates.put(date20120630, 1250d)
        PatternPacket adjustedPattern = PatternUtils.adjustedPattern(originalPattern, claimUpdates, 15000d,
                periodStartDate, periodStartDate, updateDate, date20120630, DateTimeUtilities.Days360.US)

        int periods = 7
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(periodStartDate, periods);
        List<ClaimCashflowPacket> claims = []
        GrossClaimRoot claimRoot = new GrossClaimRoot(new ClaimRoot(15000, ClaimType.AGGREGATED, periodStartDate, periodStartDate), adjustedPattern)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        for (int period = 0; period < periods; period++) {
            periodCounter.next()
            claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        }
        List<Double> incrementalClaims = [0d, 500d, 500d, 250d, 6796.77623261694d, 5562.579013906447d, 1390.6447534766137d]
        List<DateTime> payoutDates = [periodStartDate, date20110630, date20111231, date20120630, date20130731, date20140731, date20150731]
        int index = 0
        for (ClaimCashflowPacket claim : claims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental$index @ ${claim.getUpdateDate()}", incrementalClaims[index], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates$index", payoutDates[index++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, claims*.getPaidIncrementalIndexed().sum(), EPSILON

        /* Test a second development strategy */
        List<ClaimCashflowPacket> someMoreClaims = claimRoot.paidPackets(periodCounter)
        List<Double> incrementalMoreClaims = [0d, 0d, 0d, 500d, 500d, 0d, 250d, 0d, 6796.77623261694d, 0d, 5562.579013906447d, 0d, 1390.6447534766137d, 0d, 0d, ]
        List<DateTime> payoutMoreDates = [periodStartDate, periodStartDate, date20110101, date20110630, date20111231, date20120101, date20120630, date20130101,
                date20130731, date20140101, date20140731, date20150101, date20150731, date20160101, date20170101]

        int anotherIndex = 0
        for (ClaimCashflowPacket claim : someMoreClaims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental $anotherIndex @ ${claim.getUpdateDate()}", incrementalMoreClaims[anotherIndex], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates $anotherIndex", payoutMoreDates[anotherIndex++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, someMoreClaims*.getPaidIncrementalIndexed().sum(), EPSILON
    }


//    To discuss with stefan, I'm not sure this is a valid test!
    void testAdjustedPatternHistoryVoid() {
        PatternPacket originalPattern = PatternPacketTests.getPattern([15, 27, 43, 55, 67], [0.2d, 0.45d, 0.75d, 0.95d, 1d])
        DateTime periodStartDate = new DateTime(2011,1,1,0,0,0,0)
        DateTime updateDate = new DateTime(2011,3,1,0,0,0,0)

        TreeMap<DateTime, Double> claimUpdates = new TreeMap<DateTime, Double>()
        PatternPacket adjustedPattern = PatternUtils.adjustedPattern(originalPattern, claimUpdates, 15000d,
                new DateTime(2011, 1, 1, 0, 0, 0, 0), new DateTime(2011, 1, 1, 0, 0, 0, 0), updateDate, periodStartDate, DateTimeUtilities.Days360.US)

        int periods = 7
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(periodStartDate, periods);
        List<ClaimCashflowPacket> claims = []
        GrossClaimRoot claimRoot = new GrossClaimRoot(new ClaimRoot(15000, ClaimType.AGGREGATED, periodStartDate, periodStartDate), adjustedPattern)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        for (int period = 0; period < periods; period++) {
            periodCounter.next()
            claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        }
        List<Double> incrementalClaims = [0d, 3000d, 3750d, 4500d, 3000d, 750d]
        List<DateTime> payoutDates = [periodStartDate, date20120331, date20130331, date20140731, date20150731, date20160731]
        int index = 0
        for (ClaimCashflowPacket claim : claims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental$index @ ${claim.getUpdateDate()}", incrementalClaims[index], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates$index", payoutDates[index++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, claims*.getPaidIncrementalIndexed().sum(), EPSILON

        /* Test a second development strategy */
        List<ClaimCashflowPacket> someMoreClaims = claimRoot.paidPackets(periodCounter)
        List<Double> incrementalMoreClaims = [0d,0d,0d, 3000d, 0d, 3750d, 0d, 4500d, 0d, 3000d, 0d, 750d, 0d,0d,]
        List<DateTime> payoutMoreDates = [periodStartDate,periodStartDate, date20120101, date20120331, date20130101, date20130331, date20140101, date20140731, date20150101, date20150731, date20160101, date20160731, date20170101, date20180101]

        int anotherIndex = 0
        for (ClaimCashflowPacket claim : someMoreClaims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental $anotherIndex @ ${claim.getUpdateDate()}", incrementalMoreClaims[anotherIndex], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates $anotherIndex", payoutMoreDates[anotherIndex++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, someMoreClaims*.getPaidIncrementalIndexed().sum(), EPSILON
    }

    void testAdjustedPatternFullyDeveloped() {
        PatternPacket originalPattern = PatternPacketTests.getPattern([15, 27, 43, 55, 67], [0.2d, 0.45d, 0.75d, 0.95d, 1d])
        DateTime periodStartDate = new DateTime(2011,1,1,0,0,0,0)
        DateTime updateDate = new DateTime(2013,3,1,0,0,0,0)

        TreeMap<DateTime, Double> claimUpdates = new TreeMap<DateTime, Double>()
        claimUpdates.put(date20110630, 500d)
        claimUpdates.put(date20111231, 1000d)
        claimUpdates.put(date20120630, 1250d)
        claimUpdates.put(date20121215, 15000d)
        PatternPacket adjustedPattern = PatternUtils.adjustedPattern(originalPattern, claimUpdates, 15000d,
                periodStartDate, periodStartDate, updateDate, date20121215, DateTimeUtilities.Days360.US)

        int periods = 1
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(periodStartDate, periods);
        List<ClaimCashflowPacket> claims = []
        GrossClaimRoot claimRoot = new GrossClaimRoot(new ClaimRoot(15000, ClaimType.AGGREGATED, periodStartDate, periodStartDate), adjustedPattern)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        for (int period = 0; period < periods; period++) {
            periodCounter.next()
            claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))
        }
        List<Double> incrementalClaims = [0d, 500d, 500d, 250d, 13750d]
        List<DateTime> payoutDates = [date20110101, date20110630, date20111231, date20120630, date20121215]
        int index = 0
        for (ClaimCashflowPacket claim : claims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental$index @ ${claim.getUpdateDate()}", incrementalClaims[index], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates$index", payoutDates[index++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, claims*.getPaidIncrementalIndexed().sum(), EPSILON

        /* Test a second development strategy */
        List<ClaimCashflowPacket> someMoreClaims = claimRoot.paidPackets(periodCounter)
        List<Double> incrementalMoreClaims = [0d,0d,500d, 500d, 0d, 250d, 13750d, 0d,0d,0d,0d,]
        List<DateTime> payoutMoreDates = [date20110101, date20110101,  date20110630,  date20111231, date20120101, date20120630, date20121215, date20130331, date20140731, date20150731, date20160731 ]


        int anotherIndex = 0
        for (ClaimCashflowPacket claim : someMoreClaims) {
//            println "incremental$index @ ${claim.getUpdateDate()} ${claim.getPaidIncrementalIndexed()}"
            assertEquals "incremental $anotherIndex @ ${claim.getUpdateDate()}", incrementalMoreClaims[anotherIndex], claim.getPaidIncrementalIndexed(), EPSILON
            assertEquals "payoutdates $anotherIndex", payoutMoreDates[anotherIndex++], claim.getUpdateDate()
        }
        assertEquals "total", 15000d, someMoreClaims*.getPaidIncrementalIndexed().sum(), EPSILON
    }

    void testInterpolatedRate() {
        PatternPacket originalPattern = PatternPacketTests.getPattern([15, 27, 43, 55, 67], [0.2d, 0.45d, 0.75d, 0.95d, 1d])
        DateTime periodStartDate = new DateTime(2011,1,1,0,0,0,0)

        TreeMap<DateTime, Double> claimUpdates = new TreeMap<DateTime, Double>()
        claimUpdates.put(date20110630, 500d)
        claimUpdates.put(date20111231, 1000d)
        claimUpdates.put(date20120630, 1250d)

        double elapsedMonthsTillLastReportedDate = DateTimeUtilities.days360(periodStartDate, date20120630) / 30d;
        assertEquals "elapsed months", 17.966666666666665, elapsedMonthsTillLastReportedDate
        assertEquals "interpolated", 0.26180555555555557,  PatternUtils.interpolatedRate(originalPattern, 1, elapsedMonthsTillLastReportedDate)
        assertEquals "next", 0.45, PatternUtils.interpolatedRate(originalPattern, 2, 27)

    }
}
