package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.UpdatingPattern
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import com.google.common.collect.ArrayListMultimap
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateUpdatingMethodTests extends GroovyTestCase {

    private static final double EPSILON = 1E-10
    private static final DateTimeUtilities.Days360 days360 = DateTimeUtilities.Days360.US;

    DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)
    DateTime date20100310 = new DateTime(2010,3,10,0,0,0,0)
    DateTime date20110210 = new DateTime(2011,2,10,0,0,0,0)
    DateTime date20120330 = new DateTime(2012,3,30,0,0,0,0)
    DateTime date20120404 = new DateTime(2012,4,4,0,0,0,0)
    DateTime date20120501 = new DateTime(2012,5,1,0,0,0,0)

    PayoutPatternBase periodBase = PayoutPatternBase.PERIOD_START_DATE
    PayoutPatternBase occurenceBase = PayoutPatternBase.CLAIM_OCCURANCE_DATE

    void testOriginal() {
        List<ClaimRoot> baseClaims1 = [ new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310) ]
        List<ClaimRoot> baseClaims2 = [ new ClaimRoot(75000d, ClaimType.AGGREGATED, date20110101, date20110210) ]
        List<ClaimRoot> baseClaims3 = [ new ClaimRoot(75000d, ClaimType.AGGREGATED, date20120101, date20120330) ]

        IAggregateActualClaimsStrategy actualClaims = AggregateActualClaimsStrategyType.getStrategy(
                AggregateActualClaimsStrategyType.AGGREGATE,
                [history: new ConstrainedMultiDimensionalParameter([
                        [1,2,3],
                        [80000d, 42500d, 7500d],
                        [45000d, 25000d, 0d],
                        [date20120330, date20120330, date20120330]],
                        AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                        ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER))]
        )
        IPeriodCounter periodCounter = TestPeriodScopeUtilities.getPeriodScope(date20100101, 3).periodCounter
        DateTime updateDate = date20120404
        PatternPacket pattern = new PatternPacket(IUpdatingPatternMarker.class, [0.0d, 0.65d, 0.85d, 1.0d, 1.0d],
                [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)])
        pattern.origin = new UpdatingPattern(name: '48m')

        IAggregateUpdatingMethodologyStrategy updatingMethodology = new AggregateUpdatingOriginalUltimateMethodology()
        List<ClaimRoot> updatedClaims1 = updatingMethodology.updatingUltimate(baseClaims1, actualClaims, periodCounter, updateDate, [pattern], 1, days360, periodBase )
        List<ClaimRoot> updatedClaims2 = updatingMethodology.updatingUltimate(baseClaims2, actualClaims, periodCounter, updateDate, [pattern], 2, days360, periodBase )
        List<ClaimRoot> updatedClaims3 = updatingMethodology.updatingUltimate(baseClaims3, actualClaims, periodCounter, updateDate, [pattern], 3, days360, periodBase )

        assertEquals 'adjusted ultimates', 80000d, updatedClaims1[0].getUltimate()
        assertEquals 'adjusted ultimates', 75000d , updatedClaims2[0].getUltimate()
        assertEquals 'adjusted ultimates', 75000d , updatedClaims3[0].getUltimate()
    }

    void testReportedBF() {
        List<ClaimRoot> baseClaims1 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310)]
        List<ClaimRoot> baseClaims2 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210)]
        List<ClaimRoot> baseClaims3 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330)]
        IAggregateActualClaimsStrategy actualClaims = AggregateActualClaimsStrategyType.getStrategy(
                AggregateActualClaimsStrategyType.AGGREGATE,
                [history: new ConstrainedMultiDimensionalParameter([
                        [1,2,3],
                        [50000d, 42500d, 7500d],
                        [45000d, 25000d, 0d],
                        [date20120330, date20120330, date20120330]],
                        AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                        ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER))]
        )
        IPeriodCounter periodCounter = TestPeriodScopeUtilities.getPeriodScope(date20100101, 3).periodCounter
        DateTime updateDate = date20120404
        PatternPacket pattern = new PatternPacket(IUpdatingPatternMarker.class, [0.0d, 0.65d, 0.85d, 1.0d, 1.0d],
                [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)])
        pattern.origin = new UpdatingPattern(name: '48m')

        ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker, pattern.origin.name)
        updatingPattern.selectedComponent = pattern.origin

        IAggregateUpdatingMethodologyStrategy updatingMethodology = new AggregateUpdatingBFReportingMethodology(updatingPattern: updatingPattern)
        List<ClaimRoot> updatedClaims1 = updatingMethodology.updatingUltimate(baseClaims1, actualClaims, periodCounter, updateDate, [pattern], 1, days360, periodBase)
        List<ClaimRoot> updatedClaims2 = updatingMethodology.updatingUltimate(baseClaims2, actualClaims, periodCounter, updateDate, [pattern], 2, days360, periodBase)
        List<ClaimRoot> updatedClaims3 = updatingMethodology.updatingUltimate(baseClaims3, actualClaims, periodCounter, updateDate, [pattern], 3, days360, periodBase)


        assertEquals 'adjusted ultimates', 58468.75, updatedClaims1[0].getUltimate(), EPSILON
        assertEquals 'adjusted ultimates', 65041.666666666664,updatedClaims2[0].getUltimate(), EPSILON
        assertEquals 'adjusted ultimates', 70447.91666666666, updatedClaims3[0].getUltimate(), EPSILON
    }

    void testReportedBFStartDateEqualsUpdateDate() {
        List<ClaimRoot> baseClaims1 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310)]
        List<ClaimRoot> baseClaims2 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210)]
        List<ClaimRoot> baseClaims3 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330)]
        IAggregateActualClaimsStrategy actualClaims = AggregateActualClaimsStrategyType.getStrategy(
                AggregateActualClaimsStrategyType.AGGREGATE,
                [history: new ConstrainedMultiDimensionalParameter([
                        [1,2,3],
                        [50000d, 42500d, 7500d],
                        [45000d, 25000d, 0d],
                        [date20120330, date20120330, date20120330]],
                        AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                        ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER))]
        )
        IPeriodCounter periodCounter = TestPeriodScopeUtilities.getPeriodScope(date20100101, 3).periodCounter
        DateTime updateDate = date20100101
        PatternPacket pattern = new PatternPacket(IUpdatingPatternMarker.class, [0.0d, 0.65d, 0.85d, 1.0d, 1.0d],
                [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)])
        pattern.origin = new UpdatingPattern(name: '48m')

        ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker, pattern.origin.name)
        updatingPattern.selectedComponent = pattern.origin

        IAggregateUpdatingMethodologyStrategy updatingMethodology = new AggregateUpdatingBFReportingMethodology(updatingPattern: updatingPattern)
        List<ClaimRoot> updatedClaims1 = updatingMethodology.updatingUltimate(baseClaims1, actualClaims, periodCounter, updateDate, [pattern], 1, days360, periodBase)
        List<ClaimRoot> updatedClaims2 = updatingMethodology.updatingUltimate(baseClaims2, actualClaims, periodCounter, updateDate, [pattern], 2, days360, periodBase)
        List<ClaimRoot> updatedClaims3 = updatingMethodology.updatingUltimate(baseClaims3, actualClaims, periodCounter, updateDate, [pattern], 3, days360, periodBase)

        assertEquals 'adjusted ultimates', 75000d , updatedClaims1[0].getUltimate()
        assertEquals 'adjusted ultimates', 75000d, updatedClaims2[0].getUltimate()
        assertEquals 'adjusted ultimates', 75000d , updatedClaims3[0].getUltimate()
    }

    void testReportedBFUpdateDateBeforeLastReportedDate() {
        List<ClaimRoot> baseClaims1 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310)]
        List<ClaimRoot> baseClaims2 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210)]
        List<ClaimRoot> baseClaims3 = [new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330)]
        IAggregateActualClaimsStrategy actualClaims = AggregateActualClaimsStrategyType.getStrategy(
                AggregateActualClaimsStrategyType.AGGREGATE,
                [history: new ConstrainedMultiDimensionalParameter([
                        [1,2,3],
                        [50000d, 42500d, 7500d],
                        [45000d, 25000d, 0d],
                        [date20120330, date20120330, date20120501]],
                        AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                        ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER))]
        )
        IPeriodCounter periodCounter = TestPeriodScopeUtilities.getPeriodScope(date20100101, 3).periodCounter
        DateTime updateDate = date20120404
        PatternPacket pattern = new PatternPacket(IUpdatingPatternMarker.class, [0.0d, 0.65d, 0.85d, 1.0d, 1.0d],
                [Period.months(0), Period.months(12), Period.months(24), Period.months(36), Period.months(48)])
        pattern.origin = new UpdatingPattern(name: '48m')

        ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker, pattern.origin.name)
        updatingPattern.selectedComponent = pattern.origin

        IAggregateUpdatingMethodologyStrategy updatingMethodology = new AggregateUpdatingBFReportingMethodology(updatingPattern: updatingPattern)
        List<ClaimRoot> updatedClaims1 = updatingMethodology.updatingUltimate(baseClaims1, actualClaims, periodCounter, updateDate, [pattern], 1, days360, periodBase)
        List<ClaimRoot> updatedClaims2 = updatingMethodology.updatingUltimate(baseClaims2, actualClaims, periodCounter, updateDate, [pattern], 2, days360, periodBase)
        List<ClaimRoot> updatedClaims3 = updatingMethodology.updatingUltimate(baseClaims3, actualClaims, periodCounter, updateDate, [pattern], 3, days360, periodBase)

        assertEquals 'adjusted ultimates', 58468.75, updatedClaims1[0].getUltimate()
        assertEquals 'adjusted ultimates', 65041.666666666664,  updatedClaims2[0].getUltimate()
        assertEquals 'adjusted ultimates', 75000d, updatedClaims3[0].getUltimate()
    }
}
