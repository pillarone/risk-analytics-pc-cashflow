package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndex
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutPattern
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ISeverityIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsGeneratorSeverityIndexTests extends GroovyTestCase {

    private static final double EPSILON = 1E-10

    static DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
    static DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    static DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)

    static ClaimsGenerator getGenerator(SeverityIndex index, List<FactorsPacket> factors, PayoutPattern pattern,
                                        List<PatternPacket> patterns, DateTime fixedIndexDate,
                                        IndexMode indexMode, BaseDateMode baseDateMode) {
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ClaimsGenerator generator = new ClaimsGenerator()
        generator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.inFactors.addAll(factors)
        generator.inPatterns.addAll(patterns)
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, pattern.name)
        generator.parmPayoutPattern.selectedComponent = pattern
        generator.parmSeverityIndices = new ConstrainedMultiDimensionalParameter(
                [[index.name], [indexMode.toString()], [baseDateMode.toString()], [fixedIndexDate]],
                ["Index","Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
        generator.parmSeverityIndices.comboBoxValues.put(0, ['inflation': index])
        generator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 100]),
                        "claimsSizeModification": DistributionModifier.getDefault()]))
        return generator
    }

    static SeverityIndex getSeverityIndex(String indexName) {
        return new SeverityIndex(name: indexName)
    }

    static PayoutPattern getPayoutPattern(String patternName) {
        return new PayoutPattern(name: patternName)
    }

    FactorsPacket getFactorsPacket(List<DateTime> dates, List<Double> factors, ISeverityIndexMarker origin) {
        FactorsPacket factorsPacket = new FactorsPacket()
        for (int i = 0; i < dates.size(); i++) {
            factorsPacket.add(dates[i], factors[i])
        }
        factorsPacket.origin = origin
        return factorsPacket
    }

    /** IndexMode.CONTINUOUS, BaseDateMode.DATE_OF_LOSS */
    void testUsage() {
        SeverityIndex inflation = getSeverityIndex('inflation')
        List<FactorsPacket> factors = [getFactorsPacket([date20100101, date20110101, date20120101], [1, 1.02, 1.04], inflation)]
        PatternPacket pattern = new PatternPacket([0.5d, 0.8d, 1.0d], [Period.months(0), Period.months(12), Period.months(24)])
        PayoutPattern payoutPattern = getPayoutPattern('5y')
        pattern.origin = payoutPattern
        List<PatternPacket> patterns = [pattern]
        ClaimsGenerator claimsGenerator = getGenerator(inflation, factors, payoutPattern, patterns, null,
                IndexMode.CONTINUOUS, BaseDateMode.DATE_OF_LOSS)

        claimsGenerator.doCalculation()
        double appliedIndex =  claimsGenerator.outClaims[0].paidIncremental / 50d
        assertTrue "P0, index effect on paid claim", -50 > claimsGenerator.outClaims[0].paidIncremental

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        println claimsGenerator.outClaims[0].paidIncremental
        assertTrue "P1, index effect on paid claim", -30 > claimsGenerator.outClaims[0].paidIncremental
        assertEquals "same index applied for P1 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 30d), EPSILON

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        println claimsGenerator.outClaims[0].paidIncremental
        assertTrue "P2, index effect on paid claim", -20 > claimsGenerator.outClaims[0].paidIncremental
        assertEquals "same index applied for P2 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 20d), EPSILON
    }

    /** IndexMode.STEPWISE_PREVIOUS, BaseDateMode.DATE_OF_LOSS */
    void testUsagePrevious() {
        SeverityIndex inflation = getSeverityIndex('inflation')
        List<FactorsPacket> factors = [getFactorsPacket([date20100101, date20110101, date20120101], [1, 1.02, 1.04], inflation)]
        PatternPacket pattern = new PatternPacket([0.5d, 0.8d, 1.0d], [Period.months(0), Period.months(12), Period.months(24)])
        PayoutPattern payoutPattern = getPayoutPattern('5y')
        pattern.origin = payoutPattern
        List<PatternPacket> patterns = [pattern]
        ClaimsGenerator claimsGenerator = getGenerator(inflation, factors, payoutPattern, patterns, null,
                IndexMode.STEPWISE_PREVIOUS, BaseDateMode.DATE_OF_LOSS)

        claimsGenerator.doCalculation()
        double appliedIndex =  claimsGenerator.outClaims[0].paidIncremental / 50d
        assertEquals "P0 applied index", -1.02, appliedIndex
        assertTrue "P0, index effect on paid claim", -50 > claimsGenerator.outClaims[0].paidIncremental

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        assertTrue "P1, index effect on paid claim", -30 > claimsGenerator.outClaims[0].paidIncremental
        assertEquals "same index applied for P1 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 30d), EPSILON

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        assertTrue "P2, index effect on paid claim", -20 > claimsGenerator.outClaims[0].paidIncremental
        assertEquals "same index applied for P2 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 20d), EPSILON
    }

    /** IndexMode.STEPWISE_PREVIOUS, BaseDateMode.DATE_OF_LOSS */
    void testUsagePreviousBeforeProjection() {
        SeverityIndex inflation = getSeverityIndex('inflation')
        List<FactorsPacket> factors = [getFactorsPacket([date20100101, date20110101, date20120101], [1, 1.02, 1.04], inflation)]
        PatternPacket pattern = new PatternPacket([0.5d, 0.8d, 1.0d], [Period.months(0), Period.months(12), Period.months(24)])
        PayoutPattern payoutPattern = getPayoutPattern('5y')
        pattern.origin = payoutPattern
        List<PatternPacket> patterns = [pattern]
        ClaimsGenerator claimsGenerator = getGenerator(inflation, factors, payoutPattern, patterns, null,
                IndexMode.STEPWISE_PREVIOUS, BaseDateMode.DAY_BEFORE_FIRST_PERIOD)

        claimsGenerator.doCalculation()
        double appliedIndex =  claimsGenerator.outClaims[0].paidIncremental / 50d
        assertEquals "P0 applied index", -1.0, appliedIndex
        assertEquals "P0, index effect on paid claim", -50, claimsGenerator.outClaims[0].paidIncremental

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        assertEquals "P1, index effect on paid claim", -30, claimsGenerator.outClaims[0].paidIncremental, EPSILON
        assertEquals "same index applied for P1 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 30d), EPSILON

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        assertEquals "P2, index effect on paid claim", -20, claimsGenerator.outClaims[0].paidIncremental, EPSILON
        assertEquals "same index applied for P2 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 20d), EPSILON
    }

    /** IndexMode.STEPWISE_NEXT, BaseDateMode.DATE_OF_LOSS */
    void testUsageNext() {
        SeverityIndex inflation = getSeverityIndex('inflation')
        List<FactorsPacket> factors = [getFactorsPacket([date20100101, date20110101, date20120101], [1, 1.02, 1.04], inflation)]
        PatternPacket pattern = new PatternPacket([0.5d, 0.8d, 1.0d], [Period.months(0), Period.months(12), Period.months(24)])
        PayoutPattern payoutPattern = getPayoutPattern('5y')
        pattern.origin = payoutPattern
        List<PatternPacket> patterns = [pattern]
        ClaimsGenerator claimsGenerator = getGenerator(inflation, factors, payoutPattern, patterns, null,
                IndexMode.STEPWISE_NEXT, BaseDateMode.DATE_OF_LOSS)

        claimsGenerator.doCalculation()
        double appliedIndex =  claimsGenerator.outClaims[0].paidIncremental / 50d
        assertEquals "P0 applied index", -1.04, appliedIndex
        assertTrue "P0, index effect on paid claim", -50 > claimsGenerator.outClaims[0].paidIncremental

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        assertTrue "P1, index effect on paid claim", -30 > claimsGenerator.outClaims[0].paidIncremental
        assertEquals "same index applied for P1 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 30d), EPSILON

        doCalculationNextPeriod(claimsGenerator, factors, patterns)
        assertTrue "P2, index effect on paid claim", -20 > claimsGenerator.outClaims[0].paidIncremental
        assertEquals "same index applied for P2 as P0", appliedIndex, (claimsGenerator.outClaims[0].paidIncremental / 20d), EPSILON
    }

    private void doCalculationNextPeriod(ClaimsGenerator claimsGenerator, List<FactorsPacket> factors, List<PatternPacket> patterns) {
        claimsGenerator.reset()
        claimsGenerator.periodScope.prepareNextPeriod()
        claimsGenerator.inFactors.addAll(factors)
        claimsGenerator.inPatterns.addAll(patterns)
        claimsGenerator.doCalculation()
    }
}
