package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FullStabilizationStrategyTests extends GroovyTestCase {

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
                date20110406, date20110406, payoutPattern, reportingPattern, "0")
    }

    /** this is the base test case including comparison of claims values */
    void testUsage() {
        IStabilizationStrategy stabilization = getStabilization(clauseInflation, superImposedInflation,
                StabilizationBasedOn.PAID, StabilizationStrategyType.FULL, 0)
        List<Factors> factors = getFactors(clauseInflation, superImposedInflation, stabilization)

        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2011", 2, claims.size()
        assertEquals "claim reported, incremental @06.04.2011", 740.3846153846154, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2011", 10.576923076923077, claims[0].paidIncrementalIndexed
        assertEquals "claim reported, incremental @06.07.2011", 248.74572649572679, claims[1].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.07.2011", 111.48076923076925, claims[1].paidIncrementalIndexed

        ClaimStorage storage = new ClaimStorage(claims[0])
        assertEquals "effect @06.04.2011", 1.0576923076923077, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
        assertEquals "effect @06.07.2011", 1.2205769230769232, storage.stabilizationFactor(claims[1], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2012", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2012", 586.4081196581193, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2012", 807.4893162393162, claims[0].paidIncrementalIndexed
        assertEquals "effect @06.04.2012", 1.5492450142450143, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2013", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2013", 61.95299145299168, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2013", 176.98611111111106, claims[0].paidIncrementalIndexed
        assertEquals "effect @06.04.2013", 1.5807615995115996, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2014", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2014", 0, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2014", 0, claims[0].paidIncrementalIndexed
        assertEquals "effect @06.04.2013", 1.5807615995115996, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2015", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2015", 52.28525641025635, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2015", 583.2435897435898, claims[0].paidIncrementalIndexed
        assertEquals "effect @06.04.2015", 1.6897767094017095, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
    }

    void testBasedOnReported() {
        IStabilizationStrategy stabilization = getStabilization(clauseInflation, superImposedInflation,
                StabilizationBasedOn.REPORTED, StabilizationStrategyType.FULL, 0)
        List<Factors> factors = getFactors(clauseInflation, superImposedInflation, stabilization)

        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)

        ClaimStorage storage = new ClaimStorage(claims[0])
        assertEquals "effect @06.04.2011", 1.0576923076923077, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
        assertEquals "effect @06.07.2011", 1.2364129273504274, storage.stabilizationFactor(claims[1], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2012", 1.5755384615384613, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2013", 1.6374914529914528, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "#claims 2014", 1, claims.size()
        assertEquals "claim reported, incremental @06.04.2014", 0, claims[0].reportedIncrementalIndexed
        assertEquals "claim paid, incremental @06.04.2014", 0, claims[0].paidIncrementalIndexed
        assertEquals "effect @06.04.2013", 1.6374914529914528, storage.stabilizationFactor(claims[0], stabilization, periodCounter)

        periodCounter.next()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, factors, true)
        assertEquals "effect @06.04.2015", 1.6897767094017095, storage.stabilizationFactor(claims[0], stabilization, periodCounter)
    }

    public static List<Factors> getFactors(SeverityIndex clauseInflation, SeverityIndex superImposedInflation, IStabilizationStrategy stabilization) {
        List<FactorsPacket> factorsPackets = []
        FactorsPacket clauseInflationOrigin = new FactorsPacket()
        clauseInflationOrigin.origin = clauseInflation
        clauseInflationOrigin.add(date20100630, 104d)
        clauseInflationOrigin.add(date20110406, 110d)
        clauseInflationOrigin.add(date20110706, 115.94)
        clauseInflationOrigin.add(date20120406, 137.42)
        clauseInflationOrigin.add(date20130406, 127.43)
        clauseInflationOrigin.add(date20150406, 129.98)
        FactorsPacket superImposedInflationOrigin = new FactorsPacket()
        superImposedInflationOrigin.origin = superImposedInflation
        superImposedInflationOrigin.add(date20110406, 90d)
        superImposedInflationOrigin.add(date20110706, 100d)
        superImposedInflationOrigin.add(date20120406, 110d)
        superImposedInflationOrigin.add(date20130406, 130d)
        superImposedInflationOrigin.add(date20150406, 140d)
        factorsPackets << clauseInflationOrigin << superImposedInflationOrigin
        stabilization.mergeFactors(factorsPackets)
        List<Factors> factors = []
        factors.addAll(IndexUtils.convertFactors([clauseInflationOrigin], BaseDateMode.FIXED_DATE, IndexMode.CONTINUOUS, date20100630))
        factors.addAll(IndexUtils.convertFactors([superImposedInflationOrigin], BaseDateMode.DATE_OF_LOSS, IndexMode.CONTINUOUS, null))
        return factors
    }

    public static IStabilizationStrategy getStabilization(SeverityIndex clauseInflation, SeverityIndex superImposedInflation,
                                                    StabilizationBasedOn stabilizationBasedOn,
                                                    StabilizationStrategyType stabilizationStrategy, double franchise) {
        IStabilizationStrategy stabilization = StabilizationStrategyType.getStrategy(stabilizationStrategy,
                ['inflationIndices': new ConstrainedMultiDimensionalParameter(
                        [['clauseInflation', 'superImposedInflation'],
                                [IndexMode.CONTINUOUS.toString(), IndexMode.CONTINUOUS.toString()],
                                [BaseDateMode.FIXED_DATE.toString(), BaseDateMode.DATE_OF_LOSS.toString()],
                                [date20100630, date20100630]],
                        SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
                        ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER)),
                        stabilizationBasedOn: stabilizationBasedOn,
                'franchise': franchise])
        stabilization.inflationIndices.comboBoxValues[0] = ['clauseInflation': clauseInflation, 'superImposedInflation': superImposedInflation]
//        if (stabilizationStrategy.equals(StabilizationStrategyType.INTEGRAL
//            || stabilizationStrategy.equals(StabilizationStrategyType.SIC))) {
//            stabilization.franchise = franchise
//        }
        return stabilization
    }
}
