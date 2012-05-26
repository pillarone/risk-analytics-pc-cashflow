package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ContractFinancialsPacketTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])
    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);

    DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20111001 = new DateTime(2011,10,1,0,0,0,0)

    void testGetContractFinancialsPacketsByInceptionPeriod() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20100101, 5)
        IPeriodCounter periodCounterNext = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20100101, 5)
        periodCounterNext.next()
        List<ClaimCashflowPacket> cededClaims = [
                getClaim(1000, date20100101, date20100101, periodCounter),
                getClaim(800, date20100101, date20110101, periodCounterNext),
                getClaim(750, date20110101, date20110101, periodCounterNext),
        ]
        List<ClaimCashflowPacket> netClaims = [
                getClaim(-2000, date20100101, date20100101, periodCounter),
                getClaim(-800, date20110101, date20110101, periodCounterNext),
                getClaim(-950, date20110101, date20110101, periodCounterNext),
        ]
        List<CededUnderwritingInfoPacket> cededUwInfo = [
                getCededUwInfo(-1500, -1200, 300, date20100101, periodCounter),
                getCededUwInfo(-2300, -2000, 200, date20110418, periodCounterNext)
        ]
        List<UnderwritingInfoPacket> netUwInfo = [
                getUwInfo(2000, 1800, date20100101, periodCounter),
                getUwInfo(1900, 1500, date20110101, periodCounterNext)
        ]
        List<ContractFinancialsPacket> packets = ContractFinancialsPacket.getContractFinancialsPacketsByInceptionPeriod(
                cededClaims, netClaims, cededUwInfo, netUwInfo, periodCounter)

        assertEquals "2 periods -> 2 claims", 2, packets.size()
        assertEquals "periods", [2010, 2011], packets.inceptionDate*.getYear()

        assertEquals "contract results", [600d, -1350d], packets*.contractResult
        assertEquals "primary results", [300d, 350d], packets*.primaryResult
        assertEquals "ceded premium", [-1500d, -2300d], packets*.cededPremium
        assertEquals "net premium", [2000d, 1900d], packets*.netPremium
        assertEquals "ceded claim", [1800d, 750d], packets*.cededClaim
        assertEquals "net claim", [-2000d, -1750d], packets*.netClaim
        assertEquals "ceded commission", [300d, 200d], packets*.cededCommission
        assertEquals "ceded loss ratio", [-1.2d, -75/230d], packets*.cededLossRatio
    }

    private ClaimCashflowPacket getClaim(double ultimate, DateTime inceptionDate, DateTime occurrenceDate, IPeriodCounter periodCounter) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, ClaimType.AGGREGATED,
                inceptionDate, occurrenceDate, payoutPattern, reportingPattern)
        claimRoot.getClaimCashflowPackets(periodCounter).get(0)
    }

    private UnderwritingInfoPacket getUwInfo(double premiumWritten, double premiumPaid, DateTime inceptionDate, IPeriodCounter periodCounter) {
        return new UnderwritingInfoPacket(premiumWritten: premiumWritten, premiumPaid: premiumPaid,
            period: periodCounter.belongsToPeriod(inceptionDate), exposure: new ExposureInfo(inceptionDate, periodCounter))
    }

    private CededUnderwritingInfoPacket getCededUwInfo(double premiumWritten, double premiumPaid, double commission,
                                                       DateTime inceptionDate, IPeriodCounter periodCounter) {
        return new CededUnderwritingInfoPacket(premiumWritten: premiumWritten, premiumPaid: premiumPaid, commission: commission,
                period: periodCounter.belongsToPeriod(inceptionDate), exposure: new ExposureInfo(inceptionDate, periodCounter))
    }
}
