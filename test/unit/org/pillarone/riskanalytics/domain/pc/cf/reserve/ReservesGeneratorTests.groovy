package org.pillarone.riskanalytics.domain.pc.cf.reserve


import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutPattern
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.pattern.ReportingPattern
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket

import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType

import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.DeterministicIndexTableConstraints

import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode

import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndex

import org.pillarone.riskanalytics.domain.pc.cf.pattern.Pattern
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReservesIndexSelectionTableConstraints
import org.pillarone.riskanalytics.core.packets.SingleValuePacket

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class ReservesGeneratorTests extends GroovyTestCase {

    double EPSILON = 1E0

    ReservesGenerator reservesGenerator
    Pattern payoutPatterns
    Pattern reportingPatterns
    SeverityIndex inflationIndex

    DateTime projectionStart = new DateTime(2008, 1, 1, 0, 0, 0, 0)
    DateTime averageInceptionDate = new DateTime(2008, 7, 1, 0, 0, 0, 0)
    DateTime reportingDate = new DateTime(2011, 3, 31, 0, 0, 0, 0)

    DateTime date20080701 = new DateTime(2008, 7, 1, 0, 0, 0, 0)
    DateTime date20090101 = new DateTime(2009, 1, 1, 0, 0, 0, 0)
    DateTime date20090701 = new DateTime(2009, 7, 1, 0, 0, 0, 0)
    DateTime date20100101 = new DateTime(2010, 1, 1, 0, 0, 0, 0)
    DateTime date20110701 = new DateTime(2011, 7, 1, 0, 0, 0, 0)
    DateTime date20120701 = new DateTime(2012, 7, 1, 0, 0, 0, 0)
    DateTime date20130701 = new DateTime(2013, 7, 1, 0, 0, 0, 0)
    DateTime date20140701 = new DateTime(2014, 7, 1, 0, 0, 0, 0)
    DateTime date20141001 = new DateTime(2014, 10, 1, 0, 0, 0, 0)

    void setUp() {
        ConstraintsFactory.registerConstraint(new PatternTableConstraints())
        ConstraintsFactory.registerConstraint(new DeterministicIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new ReservesIndexSelectionTableConstraints())

        payoutPatterns = new PayoutPattern(
                name: 'motor hull',
                parmPattern: PatternStrategyType.getStrategy(
                        PatternStrategyType.CUMULATIVE,
                        [cumulativePattern: new ConstrainedMultiDimensionalParameter([[0, 6, 12, 18, 36, 48, 60, 72, 75], [0.0, 0.1, 0.6, 0.8, 0.85, 0.9, 0.95, 0.99, 1.0]],
                                [PatternTableConstraints.MONTHS,
                                        PatternStrategyType.CUMULATIVE2,],
                                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))])
        )
        reportingPatterns = new ReportingPattern(
                name: 'motor hull',
                parmPattern: PatternStrategyType.getStrategy(
                        PatternStrategyType.CUMULATIVE,
                        [cumulativePattern: new ConstrainedMultiDimensionalParameter([[0, 6, 12, 18, 36, 48, 60, 72, 75], [0.2, 0.8, 0.9, 0.95, 1.10, 1.0, 1.0, 1.0, 1.0]],
                                [PatternTableConstraints.MONTHS,
                                        PatternStrategyType.CUMULATIVE2,],
                                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))])
        )
        inflationIndex = new SeverityIndex(name: 'inflation', parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICINDEXSERIES,
                [indices: new ConstrainedMultiDimensionalParameter(
                        [[date20080701, date20090101, date20090701, date20100101, date20110701, date20120701, date20130701, date20140701, date20141001],
                                [102.0, 104.0, 106.0, 110.0, 115.0, 125.0, 140.0, 160.0, 162.0]],
                        DeterministicIndexTableConstraints.COLUMN_TITLES,
                        ConstraintsFactory.getConstraints(DeterministicIndexTableConstraints.IDENTIFIER))]))

        reservesGenerator = new ReservesGenerator(name: "motor hull")
        reservesGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(projectionStart, 7)
        reservesGenerator.periodStore = new PeriodStore(reservesGenerator.periodScope)
        reservesGenerator.parmUltimateEstimationMethod = ReserveCalculationType.getStrategy(
                ReserveCalculationType.REPORTEDBASED, [
                        'reportedAtReportingDate': (double) 3500.0,
                        'averageInceptionDate': averageInceptionDate,
                        'reportingDate': reportingDate,
                        'interpolationMode': InterpolationMode.NONE])
        reservesGenerator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, 'motor hull')
        reservesGenerator.parmPayoutPattern.selectedComponent = payoutPatterns
        reservesGenerator.parmReportingPattern = new ConstrainedString(IReportingPatternMarker, 'motor hull')
        reservesGenerator.parmReportingPattern.selectedComponent = reportingPatterns
        reservesGenerator.parmIndices = new ConstrainedMultiDimensionalParameter(
                [[inflationIndex.name], [IndexMode.CONTINUOUS.toString()]],
                ReservesIndexSelectionTableConstraints.COLUMN_TITLES,
                ConstraintsFactory.getConstraints(ReservesIndexSelectionTableConstraints.IDENTIFIER))
        reservesGenerator.parmIndices.comboBoxValues.put(0, ['inflation': inflationIndex])

        WiringUtils.use(WireCategory) {
            reservesGenerator.inPatterns = payoutPatterns.outPattern
            reservesGenerator.inPatterns = reportingPatterns.outPattern
            reservesGenerator.inFactors = inflationIndex.outFactors
        }

    }

    void testAverageInceptionDateInFirstPeriodReportedBasedStrategy() {

        List<PatternPacket> payoutPatternPackets = new TestProbe(payoutPatterns, "outPattern").result
        List<PatternPacket> reportedPatternPackets = new TestProbe(reportingPatterns, "outPattern").result
        List<FactorsPacket> indexPackets = new TestProbe(inflationIndex, "outFactors").result
        List<ClaimCashflowPacket> reserves = new TestProbe(reservesGenerator, "outReserves").result
        List<SingleValuePacket> ultimates = new TestProbe(reservesGenerator, "outNominalUltimates").result

        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()

        assertEquals "# payout patterns", 1, payoutPatternPackets.size()
        assertEquals "# reporting patterns", 1, reportedPatternPackets.size()
        assertEquals "# indices", 1, indexPackets.size()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " ultimate", -3684d, ultimates[0].value, EPSILON
        assertEquals " incr. paid", -0d, reserves[0].paidIncrementalIndexed
        assertEquals " cum paid", 0d, reserves[0].paidCumulatedIndexed
        assertEquals " outstandingIndexed", -658d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -658d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -658d, reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -3292d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3292d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20080701, reserves[0].updateDate
        assertEquals "update period", 0, reserves[0].updatePeriod

        reserves.clear()
        ultimates.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 2, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -336d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -336d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -2350d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -2685d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(2685-658d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -3021d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3357d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20090101, reserves[0].updateDate
        assertEquals "update period", 1, reserves[0].updatePeriod
        assertEquals " ultimate", 0d, reserves[1].ultimate, EPSILON
        assertEquals " incr paid", -1711d, reserves[1].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -2046d, reserves[1].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -1026d, reserves[1].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3073d, reserves[1].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3073d-2685d), reserves[1].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -1369d, reserves[1].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3415d, reserves[1].developedUltimate(), EPSILON
        assertEquals "update Date", date20090701, reserves[1].updateDate
        assertEquals "update period", 1, reserves[1].updatePeriod

        reserves.clear()
        ultimates.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -710d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -2756d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -533d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3289d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3289d-3073d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -710d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3467d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20100101, reserves[0].updateDate
        assertEquals "update period", 2, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -186d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -2942d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -928d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3870d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3870d-3289d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -557d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3499d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20110701, reserves[0].updateDate
        assertEquals "update period", 3, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -202d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3144d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -403d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3547d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3547d-3870d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -403d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3547d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20120701, reserves[0].updateDate
        assertEquals "update period", 4, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -226d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3370d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -226d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3596d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3596d-3547d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -226d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3596d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20130701, reserves[0].updateDate
        assertEquals "update period", 5, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 2, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -207d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3576d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -52d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3628d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3628d-3596d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -52d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3628d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20140701, reserves[0].updateDate
        assertEquals "update period", 6, reserves[0].updatePeriod
        assertEquals " ultimate", 0d, reserves[1].ultimate, EPSILON
        assertEquals " incr paid", -52d, reserves[1].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3629d, reserves[1].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", 0d, reserves[1].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3629d, reserves[1].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3629d-3628d), reserves[1].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", 0d, reserves[1].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3629d, reserves[1].developedUltimate(), EPSILON
        assertEquals "update Date", date20141001, reserves[1].updateDate
        assertEquals "update period", 6, reserves[1].updatePeriod

    }

    void testAverageInceptionDateBeforeFirstPeriodOutstandingBasedStrategy() {

        reservesGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20100101, 7)
        reservesGenerator.periodStore = new PeriodStore(reservesGenerator.periodScope)

        reservesGenerator.setParmUltimateEstimationMethod(ReserveCalculationType.getStrategy(
                ReserveCalculationType.OUTSTANDINGBASED, ["outstandingAtReportingDate": (double) 3684.0 * 0.15, "averageInceptionDate": averageInceptionDate,
                        "reportingDate": reportingDate, "interpolationMode": InterpolationMode.NONE]))

        List<PatternPacket> payoutPatternPackets = new TestProbe(payoutPatterns, "outPattern").result
        List<PatternPacket> reportedPatternPackets = new TestProbe(reportingPatterns, "outPattern").result
        List<FactorsPacket> indexPackets = new TestProbe(inflationIndex, "outFactors").result
        List<ClaimCashflowPacket> reserves = new TestProbe(reservesGenerator, "outReserves").result
        List<SingleValuePacket> ultimates = new TestProbe(reservesGenerator, "outNominalUltimates").result

        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " ultimate", -3684d, ultimates[0].value, EPSILON
        assertEquals " incr. paid", -710d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -2756d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -533d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3289d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3289d-3073d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -710d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3467d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20100101, reserves[0].updateDate
        assertEquals "update period", 0, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -186d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -2942d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -928d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3870d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3870d-3289d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -557d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3499d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20110701, reserves[0].updateDate
        assertEquals "update period", 1, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -202d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3144d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -403d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3547d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3547d-3870d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -403d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3547d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20120701, reserves[0].updateDate
        assertEquals "update period", 2, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 1, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -226d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3370d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -226d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " incr reported", -(3596d-3547d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " cum reported", -3596d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " reservedIndexed", -226d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3596d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20130701, reserves[0].updateDate
        assertEquals "update period", 3, reserves[0].updatePeriod

        reserves.clear()
        reservesGenerator.periodScope.prepareNextPeriod()
        payoutPatterns.start()
        reportingPatterns.start()
        inflationIndex.start()
        assertEquals "# reservesIndexed", 2, reserves.size()
        assertEquals " ultimate", 0d, reserves[0].ultimate, EPSILON
        assertEquals " incr. paid", -207d, reserves[0].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3576d, reserves[0].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", -52d, reserves[0].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3628d, reserves[0].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3628d-3596d), reserves[0].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", -52d, reserves[0].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3628d, reserves[0].developedUltimate(), EPSILON
        assertEquals "update Date", date20140701, reserves[0].updateDate
        assertEquals "update period", 4, reserves[0].updatePeriod
        assertEquals " ultimate", 0d, reserves[1].ultimate, EPSILON
        assertEquals " incr paid", -52d, reserves[1].paidIncrementalIndexed, EPSILON
        assertEquals " cum paid", -3629d, reserves[1].paidCumulatedIndexed, EPSILON
        assertEquals " outstandingIndexed", 0d, reserves[1].outstandingIndexed(), EPSILON
        assertEquals " cum reported", -3629d, reserves[1].reportedCumulatedIndexed, EPSILON
        assertEquals " incr reported", -(3629d-3628d), reserves[1].reportedIncrementalIndexed, EPSILON
        assertEquals " reservedIndexed", 0d, reserves[1].reservedIndexed(), EPSILON
        assertEquals " ultimate indexed", -3629d, reserves[1].developedUltimate(), EPSILON
        assertEquals "update Date", date20141001, reserves[1].updateDate
        assertEquals "update period", 4, reserves[1].updatePeriod

    }


}
