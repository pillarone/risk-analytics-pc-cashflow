package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimCashflowPacketTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])
    PatternPacket trivialPattern = PatternPacket.PATTERN_TRIVIAL;

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)

    void testTrivialPayout() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPattern, trivialPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)

        assertEquals "one claim only", 1, claims.size()
        assertEquals "correct ultimate", 1000, claims[0].ultimate()
        assertEquals "#collected fields", 2, claims[0].valuesToSave.size()
    }

//    NotImplementedException
//    void testPayoutPattern() {
//        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)
//
//        ClaimRoot claimRoot = new ClaimRoot(1000, ClaimType.AGGREGATED,
//                date20110418, date20110701, annualPayoutPattern, trivialPattern)
//        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)
//
//        assertEquals "number of claims corresponds to pattern length", annualPayoutPattern.size(), claims.size()
////        println claims[0].valuesToSave
//        assertEquals "correct ultimate", 1000, claims[0].ultimate()
//        assertEquals "#collected fields", 2, claims[0].valuesToSave.size()
//    }

    void testWithIBNR() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPattern)

        int period = 0
        int claimNumber = 0
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, null, true)
        
        assertEquals "#collected fields", 7, claims[claimNumber].valuesToSave.size()
        
        assertEquals "P$period ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period IBNR", 1000, claims[claimNumber].ibnr()
        assertEquals "P$period reserves", 1000, claims[claimNumber].reserved()
        assertEquals "P$period reported", 0, claims[claimNumber].reportedIncremental
        assertEquals "P$period paid", 0, claims[claimNumber].paidIncremental
        assertEquals "P$period outstanding", 0, claims[claimNumber].outstanding()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, null, false))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 300, claims[claimNumber].ibnr()
        assertEquals "P$period reserves", 600, claims[claimNumber].reserved()
        assertEquals "P$period reported", 700, claims[claimNumber].reportedIncremental
        assertEquals "P$period paid", 400, claims[claimNumber].paidIncremental
        assertEquals "P$period outstanding", 300, claims[claimNumber].outstanding()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, null, false))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 200, claims[claimNumber].ibnr(), EPSILON
        assertEquals "P$period reserves", 300, claims[claimNumber].reserved(), EPSILON
        assertEquals "P$period reported", 100, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 300, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period outstanding", 100, claims[claimNumber].outstanding(), EPSILON


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, null, false))

        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 50, claims[claimNumber].ibnr(), EPSILON
        assertEquals "P$period reserves", 150, claims[claimNumber].reserved(), EPSILON
        assertEquals "P$period reported", 150, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period outstanding", 100, claims[claimNumber].outstanding()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, false))
        
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 0, claims[claimNumber].ibnr()
        assertEquals "P$period reserves", 0, claims[claimNumber].reserved()
        assertEquals "P$period reported", 50, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period outstanding", 0, claims[claimNumber].outstanding()

//        assertEquals "number of claims corresponds to pattern length", annualPayoutPattern.size(), claims.size()

        println "paid incrementals summed up ${claims.paidIncremental.sum()}"
        println "paid reported summed up ${claims.reportedIncremental.sum()}"
        println "developed ultimate ${claims[-1].developedUltimate()}"
    }

    void testWithIBNRReportingStartsInFirstPeriod() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst)

        int period = 0
        int claimNumber = 0
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)
        
        assertEquals "P$period ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period IBNR", 700, claims[claimNumber].ibnr()
        assertEquals "P$period reserves", 1000, claims[claimNumber].reserved()
        assertEquals "P$period reported", 300, claims[claimNumber].reportedIncremental
        assertEquals "P$period paid", 0, claims[claimNumber].paidIncremental
        assertEquals "P$period outstanding", 300, claims[claimNumber].outstanding()


        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, false))
        
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 400, claims[claimNumber].ibnr()
        assertEquals "P$period reserves", 600, claims[claimNumber].reserved()
        assertEquals "P$period reported", 300, claims[claimNumber].reportedIncremental
        assertEquals "P$period paid", 400, claims[claimNumber].paidIncremental
        assertEquals "P$period outstanding", 200, claims[claimNumber].outstanding()

        
        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, false))
        
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 200, claims[claimNumber].ibnr(), EPSILON
        assertEquals "P$period reserves", 300, claims[claimNumber].reserved(), EPSILON
        assertEquals "P$period reported", 200, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 300, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period outstanding", 100, claims[claimNumber].outstanding(), EPSILON

        
        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, false))
        
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 20, claims[claimNumber].ibnr(), EPSILON
        assertEquals "P$period reserves", 150, claims[claimNumber].reserved(), EPSILON
        assertEquals "P$period reported", 180, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period outstanding", 130, claims[claimNumber].outstanding()

        
        period++
        claimNumber++
        periodCounter.next()
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, false))
        
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed ultimate", 1000, claims[claimNumber].developedUltimate()
        assertEquals "P$period developed result", 0, claims[claimNumber].developmentResult()
        assertEquals "P$period IBNR", 0, claims[claimNumber].ibnr()
        assertEquals "P$period reserves", 0, claims[claimNumber].reserved()
        assertEquals "P$period reported", 20, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 150, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period outstanding", 0, claims[claimNumber].outstanding()

        println "paid incrementals summed up ${claims.paidIncremental.sum()}"
        println "paid reported summed up ${claims.reportedIncremental.sum()}"
        println "developed ultimate ${claims[-1].developedUltimate()}"
    }

    void testPayoutsAndIBNRWithIndex() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        DateTime occurrenceDate = date20110701
        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, occurrenceDate, payoutPattern, reportingPattern)
        FactorsPacket factors = new FactorsPacket()
        factors.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(0)), 1.05)
        factors.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(1)), 1.15)

        int period = 0
        int claimNumber = 0
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "P$period.0 ultimate", 1000, claims[claimNumber].ultimate()
        assertEquals "P$period.0 developed result", 50, claims[claimNumber].developmentResult()
        assertEquals "P$period.0 developed ultimate", 1050, claims[claimNumber].developedUltimate()
        assertEquals "P$period.0 reported", 735, claims[claimNumber].reportedIncremental
        assertEquals "P$period.0 paid", 10.5, claims[claimNumber].paidIncremental
        assertEquals "P$period.0 reserves", 1039.5, claims[claimNumber].reserved()
        assertEquals "P$period.0 outstanding", 724.5, claims[claimNumber].outstanding()
        assertEquals "P$period.0 IBNR", 315, claims[claimNumber].ibnr()
        // as the first pattern step is of lenght 3 months only, two claims fall in the same period
        claimNumber++
        assertEquals "P$period.1 ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period.1 developed result", 149, claims[claimNumber].developmentResult()
        assertEquals "P$period.1 developed ultimate", 1149, claims[claimNumber].developedUltimate()
        assertEquals "P$period.1 reported", 115, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period.1 paid", 103.5, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period.1 reserves", 1035, claims[claimNumber].reserved()
        assertEquals "P$period.1 outstanding", 805, claims[claimNumber].outstanding(), EPSILON
        assertEquals "P$period.1 IBNR", 230, claims[claimNumber].ibnr(), EPSILON

        
        period++    // 1
        periodCounter.next()
        factors.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(2)), 1.60)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, factors, false))

        claimNumber++
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 554, claims[claimNumber].developmentResult()
        assertEquals "P$period developed ultimate", 1554, claims[claimNumber].developedUltimate()
        assertEquals "P$period reported", 160, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 800, claims[claimNumber].paidIncremental
        assertEquals "P$period reserves", 640, claims[claimNumber].reserved()
        assertEquals "P$period outstanding", 480, claims[claimNumber].outstanding()
        assertEquals "P$period IBNR", 160, claims[claimNumber].ibnr()


        period++    // 2
        periodCounter.next()
        factors.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(3)), 1.77)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, factors, false))

        claimNumber++
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 622, claims[claimNumber].developmentResult()
        assertEquals "P$period developed ultimate", 1622, claims[claimNumber].developedUltimate()
        assertEquals "P$period reported", 177, claims[claimNumber].reportedIncremental, EPSILON
        assertEquals "P$period paid", 177, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period reserves", 531, claims[claimNumber].reserved(), EPSILON
        assertEquals "P$period outstanding", 531, claims[claimNumber].outstanding(), EPSILON
        assertEquals "P$period IBNR", 0, claims[claimNumber].ibnr(), EPSILON


        period++    // 3
        period++    // 4 as there is no payout in year 2014
        periodCounter.next()
        periodCounter.next()
        factors.add(occurrenceDate.plus(payoutPattern.getCumulativePeriod(4)), 1.95)
        claims.addAll(claimRoot.getClaimCashflowPackets(periodCounter, factors, false))

        claimNumber++
        assertEquals "P$period ultimate", 0, claims[claimNumber].ultimate()
        assertEquals "P$period developed result", 676, claims[claimNumber].developmentResult()
        assertEquals "P$period developed ultimate", 1676, claims[claimNumber].developedUltimate()
        assertEquals "P$period reported", 0, claims[claimNumber].reportedIncremental  // check for correctly adding up, would expect 54
        assertEquals "P$period paid", 585, claims[claimNumber].paidIncremental, EPSILON
        assertEquals "P$period reserves", 0, claims[claimNumber].reserved()
        assertEquals "P$period outstanding", 0, claims[claimNumber].outstanding()
        assertEquals "P$period IBNR", 0, claims[claimNumber].ibnr()

        println "paid incrementals summed up ${claims.paidIncremental.sum()}"
        // todo(sku): discuss, is currently lower as indices are applied earlier than for paids
        println "paid reported summed up ${claims.reportedIncremental.sum()}"
        println "developed ultimate ${claims[-1].developedUltimate()}"
    }
}
