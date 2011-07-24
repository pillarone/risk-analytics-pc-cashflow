package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization

import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NoStabilizationStrategyTests extends GroovyTestCase {

    DateTime date20100630 = new DateTime(2010,6,30,0,0,0,0)
    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110406 = new DateTime(2011,4,6,0,0,0,0)
    DateTime date20110706 = new DateTime(2011,7,6,0,0,0,0)
    DateTime date20120406 = new DateTime(2012,4,6,0,0,0,0)
    DateTime date20130406 = new DateTime(2013,4,6,0,0,0,0)
    DateTime date20150406 = new DateTime(2015,4,6,0,0,0,0)

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 1d, 1d, 1d])

    void testUsage() {
        IStabilizationStrategy stabilization = StabilizationStrategyType.getDefault()
        List<FactorsPacket> factorsPackets = []
        FactorsPacket clauseInflation = new FactorsPacket()
        clauseInflation.add(date20100630, 104d)
        clauseInflation.add(date20110406, 110d)
        clauseInflation.add(date20110706, 115.94)
        clauseInflation.add(date20120406, 137.42)
        clauseInflation.add(date20130406, 127.43)
        clauseInflation.add(date20150406, 129.98)
        FactorsPacket superImposedInflation = new FactorsPacket()
        superImposedInflation.add(date20110406, 90d)
        superImposedInflation.add(date20110706, 100d)
        superImposedInflation.add(date20120406, 110d)
        superImposedInflation.add(date20130406, 130d)
        superImposedInflation.add(date20150406, 140d)
        factorsPackets << clauseInflation << superImposedInflation
        stabilization.mergeFactors(factorsPackets)
        List<Factors> factors = []
        factors.addAll(IndexUtils.convertFactors([clauseInflation], BaseDateMode.FIXED_DATE, IndexMode.CONTINUOUS, date20100630))
        factors.addAll(IndexUtils.convertFactors([superImposedInflation], BaseDateMode.DATE_OF_LOSS, IndexMode.CONTINUOUS, null))

        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20110101, 5)

        GrossClaimRoot claimRoot = new GrossClaimRoot(1000, ClaimType.AGGREGATED,
                date20110406, date20110406, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2011", 2, claims.size()
        assertEquals "claim reported, incremental @06.04.2011", 740.3846153846154, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2011", 10.576923076923077, claims[0].paidIncrementalIndexed
        assertEquals "claim reported, incremental @06.07.2011", 248.74572649572679, claims[1].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.07.2011", 111.48076923076925, claims[1].paidIncrementalIndexed

        ClaimStorage storage = new ClaimStorage(claims[0])
        assertEquals "no effect @06.04.2011", 1d, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
        storage.addIncrements(claims[1])
        assertEquals "no effect @06.07.2011", 1d, storage.stabilizationFactor(claims[1], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2012", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2012", 586.4081196581193, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2012", 807.4893162393162, claims[0].paidIncrementalIndexed
        storage.addIncrements(claims[0])
        assertEquals "no effect @06.04.2012", 1d, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2013", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2013", 61.95299145299168, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2013", 176.98611111111106, claims[0].paidIncrementalIndexed
        storage.addIncrements(claims[0])
        assertEquals "no effect @06.04.2013", 1d, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2014", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2014", 0, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2014", 0, claims[0].paidIncrementalIndexed
        storage.addIncrements(claims[0])
        assertEquals "no effect @06.04.2013", 1d, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2015", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2015", 52.28525641025635, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2015", 583.2435897435898, claims[0].paidIncrementalIndexed
        storage.addIncrements(claims[0])
        assertEquals "no effect @06.04.2015", 1d, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
    }
}
