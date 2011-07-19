package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PayoutReportingCombinedPatternTests extends GroovyTestCase {

    static final double EPSILON = 1E-10

    PayoutReportingCombinedPattern patterns
    ClaimsGenerator claimsGenerator

    void testUsageCumulative() {
        patterns = new PayoutReportingCombinedPattern(
                parmPattern: PayoutReportingCombinedPatternStrategyType.getStrategy(
                        PayoutReportingCombinedPatternStrategyType.CUMULATIVE,
                        [cumulativePattern: new ConstrainedMultiDimensionalParameter([[0, 12, 24], [0.4d, 0.8, 1d], [0.1d, 0.5d, 1d]],
                                [PatternTableConstraints.MONTHS,
                                        PayoutReportingCombinedPatternStrategyType.CUMULATIVE_REPORTED,
                                        PayoutReportingCombinedPatternStrategyType.CUMULATIVE_PAYOUT],
                                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))])
        )
        patterns.doCalculation()

        assertEquals "#patterns", 2, patterns.outPattern.size()
        assertTrue "payout pattern", patterns.outPattern[0].isPayoutPattern()
        assertTrue "reporting pattern", patterns.outPattern[1].isReportingPattern()
        assertEquals "cumulative payout values", [0.1d, 0.5d, 1d], patterns.outPattern[0].cumulativeValues
        assertEquals "payout periods", [Period.months(0), Period.months(12), Period.months(24)], patterns.outPattern[0].cumulativePeriods
        assertEquals "cumulative reporting values", [0.4d, 0.8d, 1d], patterns.outPattern[1].cumulativeValues
        assertEquals "reporting periods", [Period.months(0), Period.months(12), Period.months(24)], patterns.outPattern[1].cumulativePeriods
    }

    void testUsageAgeToAge() {
        patterns = new PayoutReportingCombinedPattern(
                parmPattern: PayoutReportingCombinedPatternStrategyType.getStrategy(
                        PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE,
                        [ageToAgePattern: new ConstrainedMultiDimensionalParameter([[2, 3, 12, 24, 48, 50], [10.0, 6.0, 0.7/0.6, 1.0/0.7, 1.0,1.0], [10.0, 6.0, 0.7/0.6, 0.8/0.7, 1.0/0.8, 1.0]],
                                [PatternTableConstraints.MONTHS,
                                        PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_REPORTED,
                                        PayoutReportingCombinedPatternStrategyType.LINK_RATIOS_PAYOUT],
                                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))])
        )
        patterns.doCalculation()

        assertEquals "#patterns", 2, patterns.outPattern.size()
        assertTrue "payout pattern", patterns.outPattern[0].isPayoutPattern()
        assertTrue "reporting pattern", patterns.outPattern[1].isReportingPattern()
        assertEquals "cumulative payout value 0", 0.0, patterns.outPattern[0].cumulativeValues[0]
        assertEquals "cumulative payout value 1", 0.01, patterns.outPattern[0].cumulativeValues[1], 1E-8
        assertEquals "cumulative payout value 2", 0.10, patterns.outPattern[0].cumulativeValues[2], 1E-8
        assertEquals "cumulative payout value 3", 0.6, patterns.outPattern[0].cumulativeValues[3], 1E-8
        assertEquals "cumulative payout value 4", 0.7, patterns.outPattern[0].cumulativeValues[4], 1E-8
        assertEquals "cumulative payout value 5", 0.8, patterns.outPattern[0].cumulativeValues[5], 1E-8
        assertEquals "cumulative payout value 6", 1.0, patterns.outPattern[0].cumulativeValues[6], 1E-8
        assertEquals "payout periods", [Period.months(0), Period.months(2), Period.months(3), Period.months(12), Period.months(24), Period.months(48), Period.months(50)],
                patterns.outPattern[0].cumulativePeriods
        assertEquals "cumulative reporting value 0", 0.0, patterns.outPattern[1].cumulativeValues[0]
        assertEquals "cumulative reporting value 1", 0.01, patterns.outPattern[1].cumulativeValues[1], 1E-8
        assertEquals "cumulative reporting value 2", 0.10, patterns.outPattern[1].cumulativeValues[2], 1E-8
        assertEquals "cumulative reporting value 3", 0.6, patterns.outPattern[1].cumulativeValues[3], 1E-8
        assertEquals "cumulative reporting value 4", 0.7, patterns.outPattern[1].cumulativeValues[4], 1E-8
        assertEquals "cumulative reporting value 5", 1.0, patterns.outPattern[1].cumulativeValues[5], 1E-8
        assertEquals "cumulative reporting value 6", 1.0, patterns.outPattern[1].cumulativeValues[6], 1E-8
        assertEquals "reporting periods", [Period.months(0), Period.months(2), Period.months(3), Period.months(12), Period.months(24), Period.months(48), Period.months(50)],
                patterns.outPattern[1].cumulativePeriods
    }

    void testUsageIncremental() {
        patterns = new PayoutReportingCombinedPattern(
                parmPattern: PayoutReportingCombinedPatternStrategyType.getStrategy(
                        PayoutReportingCombinedPatternStrategyType.INCREMENTAL,
                        [incrementalPattern: new ConstrainedMultiDimensionalParameter([[0, 12, 24], [0.4d, 0.4, 0.2d], [0.1d, 0.4d, 0.5d]],
                                [PatternTableConstraints.MONTHS,
                                        PayoutReportingCombinedPatternStrategyType.INCREMENTS_REPORTED,
                                        PayoutReportingCombinedPatternStrategyType.INCREMENTS_PAYOUT],
                                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))])
        )
        patterns.doCalculation()

        assertEquals "#patterns", 2, patterns.outPattern.size()
        assertTrue "payout pattern", patterns.outPattern[0].isPayoutPattern()
        assertTrue "reporting pattern", patterns.outPattern[1].isReportingPattern()
        assertEquals "cumulative payout values", [0.1d, 0.5d, 1d], patterns.outPattern[0].cumulativeValues
        assertEquals "payout periods", [Period.months(0), Period.months(12), Period.months(24)], patterns.outPattern[0].cumulativePeriods
        assertEquals "cumulative reporting values", [0.4d, 0.8d, 1d], patterns.outPattern[1].cumulativeValues
        assertEquals "reporting periods", [Period.months(0), Period.months(12), Period.months(24)], patterns.outPattern[1].cumulativePeriods
    }

    void testInteractionWithClaimsGenerator() {
        patterns = new PayoutReportingCombinedPattern(
                name: 'marine',
                parmPattern: PayoutReportingCombinedPatternStrategyType.getStrategy(
                        PayoutReportingCombinedPatternStrategyType.INCREMENTAL,
                        [incrementalPattern: new ConstrainedMultiDimensionalParameter([[0, 12, 24], [0.4d, 0.4, 0.2d], [0.1d, 0.4d, 0.5d]],
                                [PatternTableConstraints.MONTHS,
                                        PayoutReportingCombinedPatternStrategyType.INCREMENTS_REPORTED,
                                        PayoutReportingCombinedPatternStrategyType.INCREMENTS_PAYOUT],
                                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))])
        )
        claimsGenerator = new ClaimsGenerator(name: "motor hull")
        claimsGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(new DateTime(2011, 1, 1, 0, 0, 0, 0), 5)
        claimsGenerator.periodStore = new PeriodStore(claimsGenerator.periodScope)
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))
//        ConstraintsFactory.registerConstraint(new DoubleConstraints())
        claimsGenerator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, 'marine')
        claimsGenerator.parmPayoutPattern.selectedComponent = patterns
        claimsGenerator.parmReportingPattern = new ConstrainedString(IReportingPatternMarker, 'marine')
        claimsGenerator.parmReportingPattern.selectedComponent = patterns

        WiringUtils.use(WireCategory) {
            claimsGenerator.inPatterns = patterns.outPattern
        }

        List<PatternPacket> patternPackets = new TestProbe(patterns, "outPattern").result
        List<ClaimCashflowPacket> claims = new TestProbe(claimsGenerator, "outClaims").result

        patterns.start()

        assertEquals "#patterns", 2, patternPackets.size()
        assertEquals "#claims", 1, claims.size()
        assertEquals "claims ultimate", -123d, claims[0].ultimate()
        assertEquals "claims paid", -12.3, claims[0].paidIncrementalIndexed
        assertEquals "claims reported", -49.2, claims[0].reportedIncrementalIndexed
        assertEquals "claims reservesIndexed", -110.7, claims[0].reservedIndexed()
        assertEquals "claims outstandingIndexed", -36.9, claims[0].outstandingIndexed(), EPSILON
        assertEquals "claims IBNR_INDEXED", -73.8, claims[0].ibnrIndexed()
    }
}
