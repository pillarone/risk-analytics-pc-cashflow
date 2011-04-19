package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Pattern
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimRootTests extends GroovyTestCase {

    Pattern annualReportingPattern = PatternTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    Pattern annualPayoutPattern = PatternTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    Pattern payoutPattern = PatternTests.getPattern([0, 6, 18, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)


    void testHasSynchronizedPatterns() {
        ClaimRoot claimRoot = new ClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, annualReportingPattern)
        assertFalse 'different patterns', claimRoot.hasSynchronizedPatterns()

        claimRoot = new ClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, payoutPattern)
        assertTrue 'same pattern (same instance)', claimRoot.hasSynchronizedPatterns()
    }

    void testGetClaimCashflowPackets() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        ClaimRoot claimRoot = new ClaimRoot(1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)
        println claims
    }
}
