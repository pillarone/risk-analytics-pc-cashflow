package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimPacketAggregatorTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20111001 = new DateTime(2011,10,1,0,0,0,0)

    ClaimPacketAggregator aggregator = new ClaimPacketAggregator()
    PacketList<ClaimCashflowPacket> claims = new PacketList<ClaimCashflowPacket>()
    IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.2d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.1d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 18, 24], [0.2d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 18, 24], [0.1d, 0.4d, 0.7d, 0.85d, 1.0d])

    /** one update per claim and period */
    void testUsage() {
        GrossClaimRoot claimRoot0101 = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110101, date20110101, annualPayoutPattern, annualReportingPattern, "0")
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        GrossClaimRoot claimRoot0418 = new GrossClaimRoot(800, ClaimType.AGGREGATED,
                date20110101, date20110101, annualPayoutPattern, annualReportingPattern, "0")
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        GrossClaimRoot claimRoot0701 = new GrossClaimRoot(350, ClaimType.AGGREGATED,
                date20110101, date20110101, annualPayoutPattern, annualReportingPattern, "0")
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        ClaimCashflowPacket aggregateClaim = aggregator.aggregate(claims)

        int period = 0
        assertEquals "$period ultimate", 2150, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 430, aggregateClaim.reportedIncrementalIndexed
        assertEquals "$period reported cumulative", 430, aggregateClaim.reportedCumulatedIndexed
        assertEquals "$period paid incremental", 215, aggregateClaim.paidIncrementalIndexed
        assertEquals "$period paid cumulative", 215, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 1935, aggregateClaim.reservedIndexed()
        assertEquals "$period outstanding", 215, aggregateClaim.outstandingIndexed()
        assertEquals "$period ibnr", 1720, aggregateClaim.ibnrIndexed()

        period++    // 12
        periodCounter.next()
        claims.clear()
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        aggregateClaim = aggregator.aggregate(claims)

        assertEquals "$period ultimate", 0, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 1075, aggregateClaim.reportedIncrementalIndexed
        assertEquals "$period reported cumulative", 1505, aggregateClaim.reportedCumulatedIndexed
        assertEquals "$period paid incremental", 645, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 860, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 1290, aggregateClaim.reservedIndexed()
        assertEquals "$period outstanding", 645, aggregateClaim.outstandingIndexed()
        assertEquals "$period ibnr", 645, aggregateClaim.ibnrIndexed()

        period++    // 24
        periodCounter.next()
        claims.clear()
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        aggregateClaim = aggregator.aggregate(claims)

        assertEquals "$period ultimate", 0, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 215, aggregateClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "$period reported cumulative", 1720, aggregateClaim.reportedCumulatedIndexed, EPSILON
        assertEquals "$period paid incremental", 645, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 1505, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 645, aggregateClaim.reservedIndexed(), EPSILON
        assertEquals "$period outstanding", 215, aggregateClaim.outstandingIndexed(), EPSILON
        assertEquals "$period ibnr", 430, aggregateClaim.ibnrIndexed(), EPSILON

        period++    // 36
        periodCounter.next()
        claims.clear()
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        aggregateClaim = aggregator.aggregate(claims)

        assertEquals "$period ultimate", 0, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 322.5, aggregateClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "$period reported cumulative", 2042.5, aggregateClaim.reportedCumulatedIndexed, EPSILON
        assertEquals "$period paid incremental", 322.5, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 1827.5, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 322.5d, aggregateClaim.reservedIndexed(), EPSILON
        assertEquals "$period outstanding", 215d, aggregateClaim.outstandingIndexed(), EPSILON
        assertEquals "$period ibnr", 107.5, aggregateClaim.ibnrIndexed(), EPSILON

        period++    // 48
        periodCounter.next()
        claims.clear()
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        aggregateClaim = aggregator.aggregate(claims)

        assertEquals "$period ultimate", 0, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 107.5, aggregateClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "$period reported cumulative", 2150, aggregateClaim.reportedCumulatedIndexed, EPSILON
        assertEquals "$period paid incremental", 322.5, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 2150, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 0, aggregateClaim.reservedIndexed(), EPSILON
        assertEquals "$period outstanding", 0, aggregateClaim.outstandingIndexed(), EPSILON
        assertEquals "$period ibnr", 0, aggregateClaim.ibnrIndexed(), EPSILON
    }

    /** two/two/one updates per claim and period */
    void testMultipleUpdatePerPeriod() {
        GrossClaimRoot claimRoot0101 = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110101, date20110101, payoutPattern, reportingPattern, "0")
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        GrossClaimRoot claimRoot0418 = new GrossClaimRoot(800, ClaimType.AGGREGATED,
                date20110101, date20110101, payoutPattern, reportingPattern, "0")
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        GrossClaimRoot claimRoot0701 = new GrossClaimRoot(350, ClaimType.AGGREGATED,
                date20110101, date20110101, payoutPattern, reportingPattern, "0")
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        ClaimCashflowPacket aggregateClaim = aggregator.aggregate(claims)

        int period = 0

        assertEquals "$period ultimate", 2150, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 1505, aggregateClaim.reportedIncrementalIndexed
        assertEquals "$period reported cumulative", 1505, aggregateClaim.reportedCumulatedIndexed
        assertEquals "$period paid incremental", 860, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 860, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 1290, aggregateClaim.reservedIndexed()
        assertEquals "$period outstanding", 645, aggregateClaim.outstandingIndexed()
        assertEquals "$period ibnr", 645, aggregateClaim.ibnrIndexed()

        period++    // 12
        periodCounter.next()
        claims.clear()
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        aggregateClaim = aggregator.aggregate(claims)

        assertEquals "$period ultimate", 0, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 537.5, aggregateClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "$period reported cumulative", 2042.5, aggregateClaim.reportedCumulatedIndexed, EPSILON
        assertEquals "$period paid incremental", 967.5, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 1827.5, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 322.5d, aggregateClaim.reservedIndexed(), EPSILON
        assertEquals "$period outstanding", 215d, aggregateClaim.outstandingIndexed(), EPSILON
        assertEquals "$period ibnr", 107.5, aggregateClaim.ibnrIndexed(), EPSILON

        period++    // 24
        periodCounter.next()
        claims.clear()
        claims.addAll claimRoot0101.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0418.getClaimCashflowPackets(periodCounter)
        claims.addAll claimRoot0701.getClaimCashflowPackets(periodCounter)
        aggregateClaim = aggregator.aggregate(claims)

        assertEquals "$period ultimate", 0, aggregateClaim.ultimate()
        assertEquals "$period reported incremental", 107.5, aggregateClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "$period reported cumulative", 2150, aggregateClaim.reportedCumulatedIndexed, EPSILON
        assertEquals "$period paid incremental", 322.5, aggregateClaim.paidIncrementalIndexed, EPSILON
        assertEquals "$period paid cumulative", 2150, aggregateClaim.paidCumulatedIndexed
        assertEquals "$period reserves", 0, aggregateClaim.reservedIndexed(), EPSILON
        assertEquals "$period outstanding", 0, aggregateClaim.outstandingIndexed(), EPSILON
        assertEquals "$period ibnr", 0, aggregateClaim.ibnrIndexed(), EPSILON
    }

}
