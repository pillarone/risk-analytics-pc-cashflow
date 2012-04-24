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

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateUpdatingMethodTests extends GroovyTestCase {

    DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
    DateTime date20100310 = new DateTime(2010,3,10,0,0,0,0)
    DateTime date20110210 = new DateTime(2011,2,10,0,0,0,0)
    DateTime date20120330 = new DateTime(2012,3,30,0,0,0,0)
    DateTime date20120404 = new DateTime(2012,4,4,0,0,0,0)
    DateTime date20120501 = new DateTime(2012,5,1,0,0,0,0)

    void testOriginal() {
        List<ClaimRoot> baseClaims = [
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330),
        ]
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

        ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker, pattern.origin.name)

        AggregateUpdatingMethod original = AggregateUpdatingMethod.ORIGINAL_ULTIMATE
        List<ClaimRoot> updatedClaims = original.update(baseClaims, actualClaims, periodCounter, updateDate, [pattern], updatingPattern)
        updatedClaims.each {
            println it
        }
        assertEquals 'adjusted ultimates', [80000d, 75000d, 75000d], updatedClaims*.getUltimate()
    }

    void testReportedBF() {
        List<ClaimRoot> baseClaims = [
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330),
        ]
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

        AggregateUpdatingMethod original = AggregateUpdatingMethod.REPORTED_BF
        List<ClaimRoot> updatedClaims = original.update(baseClaims, actualClaims, periodCounter, updateDate, [pattern], updatingPattern)

        assertEquals 'adjusted ultimates', [58468.75, 65041.666666666664, 70447.91666666666], updatedClaims*.getUltimate()
    }

    void testReportedBFStartDateEqualsUpdateDate() {
        List<ClaimRoot> baseClaims = [
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330),
        ]
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

        AggregateUpdatingMethod original = AggregateUpdatingMethod.REPORTED_BF
        List<ClaimRoot> updatedClaims = original.update(baseClaims, actualClaims, periodCounter, updateDate, [pattern], updatingPattern)

        assertEquals 'adjusted ultimates', [75000d] * 3, updatedClaims*.getUltimate()
    }

    void testReportedBFUpdateDateBeforeLastReportedDate() {
        List<ClaimRoot> baseClaims = [
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20100310),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20110210),
                new ClaimRoot(75000d, ClaimType.AGGREGATED, date20100101, date20120330),
        ]
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

        AggregateUpdatingMethod original = AggregateUpdatingMethod.REPORTED_BF
        List<ClaimRoot> updatedClaims = original.update(baseClaims, actualClaims, periodCounter, updateDate, [pattern], updatingPattern)

        assertEquals 'adjusted ultimates', [58468.75, 65041.666666666664, 75000d], updatedClaims*.getUltimate()
    }
}
