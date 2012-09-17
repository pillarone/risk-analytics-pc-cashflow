package org.pillarone.riskanalytics.domain.pc.cf.claim

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimUtilsTests extends GroovyTestCase {

    private static final double EPSILON = 1E-10

    DateTime periodStartDate = new DateTime(2011,1,1,0,0,0,0)

    /** no index applied -> no dev result, no premium and reserve risk */
    void testScaleIncludingBaseClaimAndKeepingKeyClaim() {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(periodStartDate, 7);
        List<ClaimCashflowPacket> claims = grossClaims(periodCounter, 1000, null)
        double factor = 0.8
        ClaimCashflowPacket scaledClaim = ClaimUtils.scale(claims[0], factor, true, true)

        assertEquals "ultimate", -1000d * factor, scaledClaim.ultimate()
        assertEquals "nominal ultimate", scaledClaim.ultimate(), scaledClaim.nominalUltimate()
        assertEquals "developed ultimate", scaledClaim.ultimate(), scaledClaim.developedUltimate()
        assertEquals "developed result cumulative", 0, scaledClaim.developmentResultCumulative()
        assertEquals "reported incremental", -700d * factor, scaledClaim.reportedIncrementalIndexed
        assertEquals "reported cumulated", -700d * factor, scaledClaim.reportedCumulatedIndexed
        assertEquals "paid incremental", 0, scaledClaim.paidIncrementalIndexed
        assertEquals "paid cumulated", 0, scaledClaim.paidCumulatedIndexed
        assertEquals "reserves", -1000d * factor, scaledClaim.reservedIndexed()
        assertEquals "IBNR", -(1000 - 700) * factor, scaledClaim.ibnrIndexed()
        assertEquals "outstanding", -700d * factor, scaledClaim.outstandingIndexed()
        assertEquals "reserve risk", 0, scaledClaim.reserveRisk()
        assertEquals "premium risk", -800d, scaledClaim.premiumRisk()

        assertNotSame "base claim instance", claims[0].baseClaim, scaledClaim.baseClaim
        assertEquals "base claim", -800d, scaledClaim.baseClaim.ultimate
        assertEquals "base claim, remaining reserves", -800d, scaledClaim.baseClaim.remainingReserves
        assertEquals "base claim, previous IBNR", -240d, scaledClaim.baseClaim.previousIBNR

        assertEquals "key claim instance", claims[0].keyClaim, scaledClaim.keyClaim
        assertEquals "base claim", -1000d, scaledClaim.keyClaim.ultimate
        assertEquals "base claim, remaining reserves", -1000d, scaledClaim.keyClaim.remainingReserves
        assertEquals "base claim, previous IBNR", -300d, scaledClaim.keyClaim.previousIBNR


        periodCounter.next()
        claims = claims[0].baseClaim.getClaimCashflowPackets(periodCounter, null, true)
        factor = 0.7
        scaledClaim = ClaimUtils.scale(claims[0], factor, true, true)

        assertEquals "ultimate", 0d, scaledClaim.ultimate()
        assertEquals "nominal ultimate", -700d, scaledClaim.nominalUltimate()
        assertEquals "developed ultimate", scaledClaim.nominalUltimate(), scaledClaim.developedUltimate()
        assertEquals "developed result cumulative", 0, scaledClaim.developmentResultCumulative()
        assertEquals "reported incremental", -70d, scaledClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "reported cumulated", -560d, scaledClaim.reportedCumulatedIndexed
        assertEquals "paid incremental", -490d, scaledClaim.paidIncrementalIndexed, EPSILON
        assertEquals "paid cumulated", -490d, scaledClaim.paidCumulatedIndexed, EPSILON
        assertEquals "reserves", -(700 - 490), scaledClaim.reservedIndexed()
        assertEquals "IBNR", -(1000 - 800) * factor, scaledClaim.ibnrIndexed(), EPSILON
        assertEquals "outstanding", -(560 - 490), scaledClaim.outstandingIndexed(), EPSILON
        assertEquals "reserve risk", 0d, scaledClaim.reserveRisk(), EPSILON
        assertEquals "premium risk", 0d, scaledClaim.premiumRisk()

        // no link to first period as base claim is created from scratch
        assertNotSame "base claim instance", claims[0].baseClaim, scaledClaim.baseClaim
        assertEquals "base claim", -700d, scaledClaim.baseClaim.ultimate
        assertEquals "base claim, remaining reserves", -210d, scaledClaim.baseClaim.remainingReserves, EPSILON
        assertEquals "base claim, previous IBNR", -140d, scaledClaim.baseClaim.previousIBNR, EPSILON

        assertEquals "key claim instance", claims[0].keyClaim, scaledClaim.keyClaim
        assertEquals "base claim", -1000d, scaledClaim.keyClaim.ultimate
        assertEquals "base claim, remaining reserves", -300d, scaledClaim.keyClaim.remainingReserves, EPSILON
        assertEquals "base claim, previous IBNR", -200d, scaledClaim.keyClaim.previousIBNR, EPSILON
    }

    /** no index applied -> no dev result, no premium and reserve risk */
    void testScaleIncludingBaseClaimAndKeepingKeyClaimWithDevelopment() {
        FactorsPacket factorsPacket = new FactorsPacket()
        factorsPacket.add(periodStartDate.minusYears(1), 1d)
        factorsPacket.add(periodStartDate, 0.95d)
        factorsPacket.add(periodStartDate.plusYears(1), 1.045d)
        List<Factors> factors = [new Factors(factorsPacket, BaseDateMode.DAY_BEFORE_FIRST_PERIOD, IndexMode.STEPWISE_PREVIOUS, periodStartDate)]

        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(periodStartDate, 7);
        List<ClaimCashflowPacket> claims = grossClaims(periodCounter, 1000, factors)
        double factor = 0.8
        ClaimCashflowPacket scaledClaim = ClaimUtils.scale(claims[0], factor, true, true)

        assertEquals "ultimate", -800d, scaledClaim.ultimate()
        assertEquals "nominal ultimate", scaledClaim.ultimate(), scaledClaim.nominalUltimate()
        assertEquals "developed ultimate", scaledClaim.ultimate() * 0.95, scaledClaim.developedUltimate()
        assertEquals "developed result cumulative", 40, scaledClaim.developmentResultCumulative()
        assertEquals "reported incremental", -532, scaledClaim.reportedIncrementalIndexed
        assertEquals "reported cumulated", -532, scaledClaim.reportedCumulatedIndexed
        assertEquals "paid incremental", 0, scaledClaim.paidIncrementalIndexed
        assertEquals "paid cumulated", 0, scaledClaim.paidCumulatedIndexed
        assertEquals "reserves", -760, scaledClaim.reservedIndexed()
        assertEquals "IBNR", -(760 - 532), scaledClaim.ibnrIndexed()
        assertEquals "outstanding", -532, scaledClaim.outstandingIndexed()
        assertEquals "reserve risk", 0, scaledClaim.reserveRisk()
        assertEquals "premium risk", -760, scaledClaim.premiumRisk()

        assertNotSame "base claim instance", claims[0].baseClaim, scaledClaim.baseClaim
        assertEquals "base claim", -800, scaledClaim.baseClaim.ultimate
        assertEquals "base claim, remaining reserves", -760, scaledClaim.baseClaim.remainingReserves
        assertEquals "base claim, previous IBNR", -228, scaledClaim.baseClaim.previousIBNR

        assertEquals "key claim instance", claims[0].keyClaim, scaledClaim.keyClaim
        assertEquals "base claim", -1000d, scaledClaim.keyClaim.ultimate
        assertEquals "base claim, remaining reserves", -950d, scaledClaim.keyClaim.remainingReserves
        assertEquals "base claim, previous IBNR", -285d, scaledClaim.keyClaim.previousIBNR


        periodCounter.next()
        claims = claims[0].baseClaim.getClaimCashflowPackets(periodCounter, factors, true)
        factor = 0.7
        scaledClaim = ClaimUtils.scale(claims[0], factor, true, true)

        assertEquals "ultimate", 0d, scaledClaim.ultimate()
        assertEquals "nominal ultimate", -700d, scaledClaim.nominalUltimate()
        assertEquals "developed ultimate", -731.5d, scaledClaim.developedUltimate()
        assertEquals "developed result cumulative", -31.5, scaledClaim.developmentResultCumulative()
        assertEquals "reported incremental", -119.7d, scaledClaim.reportedIncrementalIndexed, EPSILON
        assertEquals "reported cumulated", -585.2, scaledClaim.reportedCumulatedIndexed
//        assertEquals "paid incremental", -512.05d, scaledClaim.paidIncrementalIndexed, EPSILON
//        assertEquals "paid cumulated", -512.05d, scaledClaim.paidCumulatedIndexed, EPSILON
//        assertEquals "reserves", -(700 - 490), scaledClaim.reservedIndexed()
//        assertEquals "IBNR", -(1000 - 800) * factor, scaledClaim.ibnrIndexed(), EPSILON
//        assertEquals "outstanding", -(560 - 490), scaledClaim.outstandingIndexed(), EPSILON
//        assertEquals "reserve risk", 0d, scaledClaim.reserveRisk(), EPSILON
//        assertEquals "premium risk", 0d, scaledClaim.premiumRisk()
//
//        // no link to first period as base claim is created from scratch
//        assertNotSame "base claim instance", claims[0].baseClaim, scaledClaim.baseClaim
//        assertEquals "base claim", -700d, scaledClaim.baseClaim.ultimate
//        assertEquals "base claim, remaining reserves", -210d, scaledClaim.baseClaim.remainingReserves, EPSILON
//        assertEquals "base claim, previous IBNR", -140d, scaledClaim.baseClaim.previousIBNR, EPSILON
//
//        assertEquals "key claim instance", claims[0].keyClaim, scaledClaim.keyClaim
//        assertEquals "base claim", -1000d, scaledClaim.keyClaim.ultimate
//        assertEquals "base claim, remaining reserves", -300d, scaledClaim.keyClaim.remainingReserves, EPSILON
//        assertEquals "base claim, previous IBNR", -200d, scaledClaim.keyClaim.previousIBNR, EPSILON
    }


//    void testScaleKeepingKeyClaim() {
//        ClaimCashflowPacket scaledClaim = ClaimUtils.scale(claim, 0.8, false, true)
//    }
//
//    void testScaleIncludingBaseClaimAndNotKeepingKeyClaim() {
//        ClaimCashflowPacket scaledClaim = ClaimUtils.scale(claim, 0.8, true, false)
//    }
//
//    void testScaleNotIncludingBaseClaimAndNotKeepingKeyClaim() {
//        ClaimCashflowPacket scaledClaim = ClaimUtils.scale(claim, 0.8, false, false)
//    }
//
//    void testGetCededClaim() {
//
//    }
//
//    void testGetCededClaimReportedAbsolute() {
//
//    }

    private List<ClaimCashflowPacket> grossClaims(IPeriodCounter periodCounter, double ultimate,
                                                  List<Factors> factors, ClaimType claimType = ClaimType.AGGREGATED) {
        PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36], [0.7d, 0.8d, 0.95d, 1.0d])
        PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.7d, 0.8d, 0.95d, 1.0d])

        DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
        DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)

        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, claimType,
                date20110418, date20110701, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        return claims
    }



}
