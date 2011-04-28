package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GrossClaimRootTests extends GroovyTestCase {

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 6, 18, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)


    void testHasSynchronizedPatterns() {
        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, annualReportingPattern)
        assertFalse 'different patterns', claimRoot.hasSynchronizedPatterns()

        claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, payoutPattern)
        assertTrue 'same pattern (same instance)', claimRoot.hasSynchronizedPatterns()
    }

    void testGetClaimCashflowPackets() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)
    }
}
