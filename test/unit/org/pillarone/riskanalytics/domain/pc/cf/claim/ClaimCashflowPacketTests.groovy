package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimCashflowPacketTests extends GroovyTestCase {

    static Log LOG = LogFactory.getLog(ClaimCashflowPacketTests.class);
    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])
    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20111001 = new DateTime(2011,10,1,0,0,0,0)

    static ClaimCashflowPacket getClaimCashflowPacket(double ultimate, double incrementalPaid,
                                                      IReinsuranceContractMarker contract, boolean hasUltimate = false) {
        ClaimRoot baseClaim = new ClaimRoot(ultimate, ClaimType.ATTRITIONAL, null, null)
        DateTime updateDate = new DateTime(2011,1,1,0,0,0,0)
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(updateDate, 1)
        ClaimCashflowPacket claim = new ClaimCashflowPacket(baseClaim, ultimate, ultimate, incrementalPaid, incrementalPaid, ultimate, ultimate, ultimate - incrementalPaid, 0, 0, null, updateDate, periodCounter)
        if (contract != null) {
            claim.setMarker contract
        }
        claim
    }

    static ClaimCashflowPacket getClaimCashflowPacket(double ultimate, double incrementalPaid, double reported,
                                                      IReinsuranceContractMarker contract = null, boolean hasUltimate = false) {
        ClaimRoot baseClaim = new ClaimRoot(ultimate, ClaimType.ATTRITIONAL, null, null)
        DateTime updateDate = new DateTime(2011, 1, 1, 0, 0, 0, 0)
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(updateDate, 1)
        ClaimCashflowPacket claim = new ClaimCashflowPacket(baseClaim, ultimate, ultimate, incrementalPaid, incrementalPaid,
                reported, reported, ultimate - incrementalPaid, ultimate - incrementalPaid, ultimate - reported, null, updateDate, periodCounter)
        if (contract != null) {
            claim.setMarker contract
        }
        claim
    }


    void testTrivialPayout() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern, "0")
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)

        assertEquals "one claim only", 1, claims.size()
        assertEquals "ultimate", 1000, claims[0].ultimate()
        assertEquals "developed ultimate", 1000, claims[0].developedUltimate()
        assertEquals "developed result", 0, claims[0].developmentResultCumulative()
        assertEquals "reported incremental", 1000, claims[0].reportedIncrementalIndexed
        assertEquals "reported cumulated", 1000, claims[0].reportedCumulatedIndexed
        assertEquals "paid incremental", 1000, claims[0].paidIncrementalIndexed
        assertEquals "paid cumulated", 1000, claims[0].paidCumulatedIndexed
        assertEquals "outstanding", 0, claims[0].outstandingIndexed()
        assertEquals "reservedIndexed", 0, claims[0].reservedIndexed()
        assertEquals "changeInReservesIndexed", 0, claims[0].changeInReservesIndexed
        assertEquals "ibnrIndexed", 0, claims[0].ibnrIndexed()
        assertEquals "changeInIBNRIndexed", 0, claims[0].changeInIBNRIndexed
    }

    void testPayoutPattern() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualReportingPatternInclFirst, trivialReportingPattern, "0")
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)

        int period = 0
        int claimNumber = 0
        assertEquals "P$period correct ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period reported incremental", 1000, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period reported cumulated", 1000, claims[claimNumber].reportedCumulatedIndexed
        assertEquals "P$period paid incremental", 300, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period paid cumulated", 300, claims[claimNumber].paidCumulatedIndexed
        assertEquals "P$period outstandingIndexed", 700, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period reservedIndexed", 700, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", 700, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period ibnrIndexed", 0, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", 0, claims[claimNumber].changeInIBNRIndexed


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period correct ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period reported incremental", 0, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period reported cumulated", 1000, claims[claimNumber].reportedCumulatedIndexed
        assertEquals "P$period paid incremental", 300, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period paid cumulated", 600, claims[claimNumber].paidCumulatedIndexed
        assertEquals "P$period outstandingIndexed", 400, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period reservedIndexed", 400, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -300, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period ibnrIndexed", 0, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", 0, claims[claimNumber].changeInIBNRIndexed


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period correct ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period reported incremental", 0, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period reported cumulated", 1000, claims[claimNumber].reportedCumulatedIndexed
        assertEquals "P$period paid incremental", 200, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period paid cumulated", 800, claims[claimNumber].paidCumulatedIndexed
        assertEquals "P$period outstandingIndexed", 200, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period reservedIndexed", 200, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -200, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period ibnrIndexed", 0, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", 0, claims[claimNumber].changeInIBNRIndexed


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period correct ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period reported incremental", 0, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period reported cumulated", 1000, claims[claimNumber].reportedCumulatedIndexed
        assertEquals "P$period paid incremental", 180, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period paid cumulated", 980, claims[claimNumber].paidCumulatedIndexed
        assertEquals "P$period outstandingIndexed", 20, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period reservedIndexed", 20, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -180, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period ibnrIndexed", 0, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", 0, claims[claimNumber].changeInIBNRIndexed


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period correct ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period reported incremental", 0, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period reported cumulated", 1000, claims[claimNumber].reportedCumulatedIndexed
        assertEquals "P$period paid incremental", 20, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period paid cumulated", 1000, claims[claimNumber].paidCumulatedIndexed
        assertEquals "P$period outstandingIndexed", 0, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period reservedIndexed", 0, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -20, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period ibnrIndexed", 0, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", 0, claims[claimNumber].changeInIBNRIndexed
    }

    void testWithIBNR() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPattern, "0")

        int period = 0
        int claimNumber = 0
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)

        assertEquals "#collected fields", 16, claims[claimNumber].valuesToSave.size()

        assertEquals "P$period ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period IBNR_INDEXED", 1000, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", 1000, claims[claimNumber].changeInIBNRIndexed
        assertEquals "P$period reservesIndexed", 1000, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", 1000, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period reported", 0, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period paid", 0, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period outstandingIndexed", 0, claims[claimNumber].outstandingIndexed()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 300, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", -700, claims[claimNumber].changeInIBNRIndexed
        assertEquals "P$period reservesIndexed", 600, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -400, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period reported", 700, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period paid", 400, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period outstandingIndexed", 300, claims[claimNumber].outstandingIndexed()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 200, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", -100, claims[claimNumber].changeInIBNRIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 300, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -300, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period reported", 100, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 300, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 100, claims[claimNumber].outstandingIndexed(), EPSILON


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 50, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", -150, claims[claimNumber].changeInIBNRIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 150, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -150, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period reported", 150, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 100, claims[claimNumber].outstandingIndexed()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 0, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", -50, claims[claimNumber].changeInIBNRIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 0, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -150, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period reported", 50, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 0, claims[claimNumber].outstandingIndexed()

        LOG.info "paid incrementals summed up ${claims.paidIncrementalIndexed.sum()}"
        LOG.info "paid reported summed up ${claims.reportedIncrementalIndexed.sum()}"
        LOG.info "developed ultimate ${claims[-1].developedUltimate()}"
    }

    void testWithIBNRReportingStartsInFirstPeriod() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst, "0")

        int period = 0
        int claimNumber = 0
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)

        assertEquals "P$period ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period IBNR_INDEXED", 700, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", 700, claims[claimNumber].changeInIBNRIndexed
        assertEquals "P$period reservesIndexed", 1000, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", 1000, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period reported", 300, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period paid", 0, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period outstandingIndexed", 300, claims[claimNumber].outstandingIndexed()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 400, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", -300, claims[claimNumber].changeInIBNRIndexed
        assertEquals "P$period reservesIndexed", 600, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -400, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period reported", 300, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period paid", 400, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period outstandingIndexed", 200, claims[claimNumber].outstandingIndexed()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 200, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", -200, claims[claimNumber].changeInIBNRIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 300, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -300, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period reported", 200, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 300, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 100, claims[claimNumber].outstandingIndexed(), EPSILON


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 20, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", -180, claims[claimNumber].changeInIBNRIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 150, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -150, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period reported", 180, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 130, claims[claimNumber].outstandingIndexed()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period IBNR_INDEXED", 0, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", -20, claims[claimNumber].changeInIBNRIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 0, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -150, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period reported", 20, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 0, claims[claimNumber].outstandingIndexed()

        LOG.info "paid incrementals summed up ${claims.paidIncrementalIndexed.sum()}"
        LOG.info "paid reported summed up ${claims.reportedIncrementalIndexed.sum()}"
        LOG.info "developed ultimate ${claims[-1].developedUltimate()}"
    }

    void testPayoutsAndIBNRWithIndex() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        DateTime occurrenceDate = date20110701
        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, occurrenceDate, payoutPattern, reportingPattern, "0")
        FactorsPacket factorsPacket = new FactorsPacket()
        factorsPacket.add(date20110101, 1.0)
        factorsPacket.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(0)), 1.05)
        factorsPacket.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(1)), 1.15)
        Factors factors = new Factors(factorsPacket, BaseDateMode.START_OF_PROJECTION, IndexMode.STEPWISE_PREVIOUS, null)

        int period = 0
        int claimNumber = 0
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, [factors], true)
        assertEquals "P$period.0 ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period.0 developed result", 50, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period.0 developed ultimate", 1050, claims[claimNumber].developedUltimate()
        assertEquals "P$period.0 reported", 735, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period.0 paid", 10.5, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period.0 reservesIndexed", 1039.5, claims[claimNumber].reservedIndexed()
        assertEquals "P$period.0 changeInReservesIndexed", 1039.5, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period.0 outstandingIndexed", 724.5, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period.0 IBNR_INDEXED", 315, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period.0 changeInIBNRIndexed", 315, claims[claimNumber].changeInIBNRIndexed
        // as the first pattern step is of length 3 months only, two claims fall in the same period
        claimNumber++
        assertEquals "P$period.1 ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period.1 developed result", 149, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period.1 developed ultimate", 1149, claims[claimNumber].developedUltimate()
        assertEquals "P$period.1 reported", 184, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period.1 paid", 103.5, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period.1 reservesIndexed", 1035, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period.1 changeInReservesIndexed", -4.5, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period.1 outstandingIndexed", 805, claims[claimNumber].outstandingIndexed(), EPSILON
        assertEquals "P$period.1 IBNR_INDEXED", 230, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period.1 changeInIBNRIndexed", -85, claims[claimNumber].changeInIBNRIndexed, EPSILON


        period++    // 1
        periodCounter.next()
        factorsPacket.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(2)), 1.60)
        factors = new Factors(factorsPacket, BaseDateMode.START_OF_PROJECTION, IndexMode.STEPWISE_PREVIOUS, null)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, [factors], true))

        claimNumber++
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 554, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period developed ultimate", 1554, claims[claimNumber].developedUltimate()
        assertEquals "P$period reported", 475, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 800, claims[claimNumber].paidIncrementalIndexed
        assertEquals "P$period reservesIndexed", 640, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -395, claims[claimNumber].changeInReservesIndexed
        assertEquals "P$period outstandingIndexed", 480, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period IBNR_INDEXED", 160, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", -70, claims[claimNumber].changeInIBNRIndexed, EPSILON


        period++    // 2
        periodCounter.next()
        factorsPacket.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(3)), 1.77)
        factors = new Factors(factorsPacket, BaseDateMode.START_OF_PROJECTION, IndexMode.STEPWISE_PREVIOUS, null)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, [factors], true))

        claimNumber++
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 622, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period developed ultimate", 1622, claims[claimNumber].developedUltimate()
        assertEquals "P$period reported", 228, claims[claimNumber].reportedIncrementalIndexed, EPSILON
        assertEquals "P$period paid", 177, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 531, claims[claimNumber].reservedIndexed(), EPSILON
        assertEquals "P$period changeInReservesIndexed", -109, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 531, claims[claimNumber].outstandingIndexed(), EPSILON
        assertEquals "P$period IBNR_INDEXED", 0, claims[claimNumber].ibnrIndexed(), EPSILON
        assertEquals "P$period changeInIBNRIndexed", -160, claims[claimNumber].changeInIBNRIndexed, EPSILON


        period++    // 3
        period++    // 4 as there is no payout in year 2014
        periodCounter.next()
        periodCounter.next()
        factorsPacket.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(4)), 1.95)
        factors = new Factors(factorsPacket, BaseDateMode.START_OF_PROJECTION, IndexMode.STEPWISE_PREVIOUS, null)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, [factors], true))

        claimNumber++
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 676, claims[claimNumber].developmentResultCumulative()
        assertEquals "P$period developed ultimate", 1676, claims[claimNumber].developedUltimate()
        assertEquals "P$period reported", 54, claims[claimNumber].reportedIncrementalIndexed
        assertEquals "P$period paid", 585, claims[claimNumber].paidIncrementalIndexed, EPSILON
        assertEquals "P$period reservesIndexed", 0, claims[claimNumber].reservedIndexed()
        assertEquals "P$period changeInReservesIndexed", -531, claims[claimNumber].changeInReservesIndexed, EPSILON
        assertEquals "P$period outstandingIndexed", 0, claims[claimNumber].outstandingIndexed()
        assertEquals "P$period IBNR_INDEXED", 0, claims[claimNumber].ibnrIndexed()
        assertEquals "P$period changeInIBNRIndexed", 0, claims[claimNumber].changeInIBNRIndexed

        LOG.info "paid incrementals summed up ${claims.paidIncrementalIndexed.sum()}"
        // todo(sku): discuss, is currently lower as changes are applied earlier than for paids
        LOG.info "paid reported summed up ${claims.reportedIncrementalIndexed.sum()}"
        LOG.info "developed ultimate ${claims[-1].developedUltimate()}"
    }

    // use case described in https://issuetracking.intuitive-collaboration.com/jira/browse/ART-687
    void testDelayedUltimate() {

    }
}
