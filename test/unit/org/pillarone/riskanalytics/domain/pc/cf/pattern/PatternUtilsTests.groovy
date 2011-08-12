package org.pillarone.riskanalytics.domain.pc.cf.pattern

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PatternUtilsTests extends GroovyTestCase {

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
}
