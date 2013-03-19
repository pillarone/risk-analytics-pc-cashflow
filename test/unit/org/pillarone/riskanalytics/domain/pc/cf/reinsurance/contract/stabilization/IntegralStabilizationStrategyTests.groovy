package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization

import org.joda.time.DateTime

import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IntegralStabilizationStrategyTests extends GroovyTestCase {

    static DateTime date20100630 = new DateTime(2010,6,30,0,0,0,0)
    static DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    static DateTime date20110406 = new DateTime(2011,4,6,0,0,0,0)
    static DateTime date20110706 = new DateTime(2011,7,6,0,0,0,0)
    static DateTime date20120406 = new DateTime(2012,4,6,0,0,0,0)
    static DateTime date20130406 = new DateTime(2013,4,6,0,0,0,0)
    static DateTime date20150406 = new DateTime(2015,4,6,0,0,0,0)

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 1d, 1d, 1d])

    SeverityIndex clauseInflation
    SeverityIndex superImposedInflation
    IPeriodCounter periodCounter
    GrossClaimRoot claimRoot

    void setUp() {
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        clauseInflation = new SeverityIndex(name : 'clauseInflation')
        superImposedInflation = new SeverityIndex(name : 'superImposedInflation')
        periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)
        claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110406, date20110406, payoutPattern, reportingPattern)
    }

    void testUsage() {
        IStabilizationStrategy stabilization = FullStabilizationStrategyTests.getStabilization(clauseInflation, superImposedInflation,
                StabilizationBasedOn.PAID, StabilizationStrategyType.INTEGRAL, 0.08)
        List<Factors> factors = FullStabilizationStrategyTests.getFactors(clauseInflation, superImposedInflation, stabilization)

        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        ClaimStorage storage = new ClaimStorage(claims[0])
        assertEquals "no effect @06.04.2011", 1.0, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
        assertEquals "effect @06.07.2011", 1.2135755258126195, storage.stabilizationFactor(claims[1], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2012", 1.547756786565624, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2013", 1.5794598468905359, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2013", 1.5794598468905359, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2015", 1.6888024003245992, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
    }

    void testBasedOnReported() {
        IStabilizationStrategy stabilization = FullStabilizationStrategyTests.getStabilization(clauseInflation, superImposedInflation,
                StabilizationBasedOn.REPORTED, StabilizationStrategyType.INTEGRAL, 0.08)
        List<Factors> factors = FullStabilizationStrategyTests.getFactors(clauseInflation, superImposedInflation, stabilization)

        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)

        ClaimStorage storage = new ClaimStorage(claims[0])
        assertEquals "no effect @06.04.2011", 1.0, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
        assertEquals "effect @06.07.2011", 1.235521925961513, storage.stabilizationFactor(claims[1], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2012", 1.5746300211416489, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2013", 1.6365472910927454, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2014", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2014", 0, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2014", 0, claims[0].paidIncrementalIndexed
        assertEquals "effect @06.04.2013", 1.6365472910927454, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2015", 1.6888024003245992, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
    }

}
