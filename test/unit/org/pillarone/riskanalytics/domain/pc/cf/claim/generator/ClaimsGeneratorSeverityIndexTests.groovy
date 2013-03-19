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
    static DateTime date20100701 = new DateTime(2010,7,1,0,0,0,0)
    static DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    static DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)
    static DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
    static DateTime date20140101 = new DateTime(2014,1,1,0,0,0,0)
    static DateTime date20150101 = new DateTime(2015,1,1,0,0,0,0)
    static DateTime date20160101 = new DateTime(2016,1,1,0,0,0,0)

    static ClaimsGenerator getGenerator(SeverityIndex index, List<FactorsPacket> factors, PayoutPattern pattern,
                                        List<PatternPacket> patterns, DateTime fixedIndexDate, DateTime projectionStart,
                                        IndexMode indexMode, BaseDateMode baseDateMode) {
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ClaimsGenerator generator = new ClaimsGenerator()
        generator.periodScope = TestPeriodScopeUtilities.getPeriodScope(projectionStart, 5)
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.inFactors.addAll(factors)
        generator.inPatterns.addAll(patterns)
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, pattern.name)
        generator.parmPayoutPattern.selectedComponent = pattern
        generator.parmRunOffIndices = new ConstrainedMultiDimensionalParameter(
                [[index.name], [indexMode.toString()], [baseDateMode.toString()], [fixedIndexDate]],
                ["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
        generator.parmRunOffIndices.comboBoxValues.put(0, ['inflation': index])
        generator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 100]),
                        "claimsSizeModification": DistributionModifier.getDefault(),
                        'occurrenceDateDistribution': DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0.49789d])]))
        return generator
    }

    static ClaimsGenerator getGenerator(DateTime fixedIndexDate, IndexMode indexMode, BaseDateMode baseDateMode) {
        return getGenerator(fixedIndexDate, date20110101, indexMode, baseDateMode)
    }

    static ClaimsGenerator getGenerator(DateTime fixedIndexDate, DateTime projectionStart, IndexMode indexMode, BaseDateMode baseDateMode) {
        SeverityIndex inflation = getSeverityIndex('inflation')
        List<FactorsPacket> factors = [getFactorsPacket(
                [date20100101, date20100701, date20110101, date20120101, date20130101, date20140101],
                [1, 1.02, 1.03, 1.06, 1.07, 1.1], inflation)]
        PatternPacket pattern = new PatternPacket(IPayoutPatternMarker.class, [0.5d, 0.8d, 1.0d],
                                                  [Period.months(0), Period.months(12), Period.months(24)])
        PayoutPattern payoutPattern = getPayoutPattern('5y')
        pattern.origin = payoutPattern
        List<PatternPacket> patterns = [pattern]
        return getGenerator(inflation, factors, payoutPattern, patterns, fixedIndexDate, projectionStart, indexMode, baseDateMode)
    }

    static SeverityIndex getSeverityIndex(String indexName) {
        return new SeverityIndex(name: indexName)
    }

    static PayoutPattern getPayoutPattern(String patternName) {
        return new PayoutPattern(name: patternName)
    }

    static FactorsPacket getFactorsPacket(List<DateTime> dates, List<Double> factors, ISeverityIndexMarker origin) {
        FactorsPacket factorsPacket = new FactorsPacket()
        for (int i = 0; i < dates.size(); i++) {
            factorsPacket.add(dates[i], factors[i])
        }
        factorsPacket.origin = origin
        return factorsPacket
    }

    /** IndexMode.CONTINUOUS, BaseDateMode.DATE_OF_LOSS */
    void testUsageContinuousDoL() {
        ClaimsGenerator claimsGenerator = getGenerator(null, IndexMode.CONTINUOUS, BaseDateMode.DATE_OF_LOSS)

        claimsGenerator.doCalculation()
        assertTrue "P0, index effect on paid claim", -50 == claimsGenerator.outClaims[0].paidIncrementalIndexed

        doCalculationNextPeriod(claimsGenerator)
        println claimsGenerator.outClaims[0].paidIncrementalIndexed
        assertEquals "P1, index effect on paid claim", 1.0193266424, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        println claimsGenerator.outClaims[0].paidIncrementalIndexed
        assertEquals "P2, index effect on paid claim", 1.0382897717, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.STEPWISE_PREVIOUS, BaseDateMode.DATE_OF_LOSS */
    void testUsagePreviousDoL() {
        ClaimsGenerator claimsGenerator = getGenerator(null, IndexMode.STEPWISE_PREVIOUS, BaseDateMode.DATE_OF_LOSS)

        claimsGenerator.doCalculation()
        assertTrue "P0, index effect on paid claim", -50 == claimsGenerator.outClaims[0].paidIncrementalIndexed

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P1, index effect on paid claim", 1.0291262136, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P2, index effect on paid claim", 1.0388349515, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.STEPWISE_NEXT, BaseDateMode.DATE_OF_LOSS */
    void testUsageNextDoL() {
        ClaimsGenerator claimsGenerator = getGenerator(null, IndexMode.STEPWISE_NEXT, BaseDateMode.DATE_OF_LOSS)

        claimsGenerator.doCalculation()
        assertTrue "P0, index effect on paid claim", -50 == claimsGenerator.outClaims[0].paidIncrementalIndexed

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P1, index effect on paid claim", 1.0094339623, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P2, index effect on paid claim", 1.0377358491, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.CONTINUOUS, BaseDateMode.START_OF_PROJECTION */
    void testUsageContinuousStartOfProjection() {
        ClaimsGenerator claimsGenerator = getGenerator(null, date20100101, IndexMode.CONTINUOUS, BaseDateMode.START_OF_PROJECTION)

        claimsGenerator.doCalculation()
        assertEquals "P0, index effect on paid claim", 1.02, claimsGenerator.outClaims[0].paidIncrementalIndexed / -50, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        println claimsGenerator.outClaims[0].paidIncrementalIndexed
        assertEquals "P1, index effect on paid claim", 1.0447690628, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        println claimsGenerator.outClaims[0].paidIncrementalIndexed
        assertEquals "P2, index effect on paid claim", 1.0649609409, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.STEPWISE_PREVIOUS, BaseDateMode.START_OF_PROJECTION */
    void testUsagePreviousStartOfProjection() {
        ClaimsGenerator claimsGenerator = getGenerator(null, date20100101, IndexMode.STEPWISE_PREVIOUS, BaseDateMode.START_OF_PROJECTION)

        claimsGenerator.doCalculation()
        assertEquals "P0, index effect on paid claim", 1.02, claimsGenerator.outClaims[0].paidIncrementalIndexed / -50, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P1, index effect on paid claim", 1.03, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P2, index effect on paid claim", 1.06, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.STEPWISE_NEXT, BaseDateMode.START_OF_PROJECTION */
    void testUsageNextStartOfProjection() {
        ClaimsGenerator claimsGenerator = getGenerator(null, date20100101, IndexMode.STEPWISE_NEXT, BaseDateMode.START_OF_PROJECTION)

        claimsGenerator.doCalculation()
        // not 1.03 as the payout date is exactly at an index date
        assertEquals "P0, index effect on paid claim", 1.02, claimsGenerator.outClaims[0].paidIncrementalIndexed / -50, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P1, index effect on paid claim", 1.06, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P2, index effect on paid claim", 1.07, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.CONTINUOUS, BaseDateMode.FIXED_DATE */
    void testUsageContinuousFixedDate() {
        ClaimsGenerator claimsGenerator = getGenerator(date20100701, IndexMode.CONTINUOUS, BaseDateMode.FIXED_DATE)

        claimsGenerator.doCalculation()
        assertEquals "P0, index effect on paid claim", 1.0242833949, claimsGenerator.outClaims[0].paidIncrementalIndexed / -50, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        println claimsGenerator.outClaims[0].paidIncrementalIndexed
        assertEquals "P1, index effect on paid claim", 1.0440793538, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        println claimsGenerator.outClaims[0].paidIncrementalIndexed
        assertEquals "P2, index effect on paid claim", 1.0635029722, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.STEPWISE_PREVIOUS, BaseDateMode.FIXED_DATE */
    void testUsagePreviousFixedDate() {
        ClaimsGenerator claimsGenerator = getGenerator(date20100701, IndexMode.STEPWISE_PREVIOUS, BaseDateMode.FIXED_DATE)

        claimsGenerator.doCalculation()
        assertEquals "P0, index effect on paid claim", 1.0098039216, claimsGenerator.outClaims[0].paidIncrementalIndexed / -50, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P1, index effect on paid claim", 1.0392156863, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P2, index effect on paid claim", 1.0490196078, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }

    /** IndexMode.STEPWISE_NEXT, BaseDateMode.FIXED_DATE */
    void testUsageNextFixedDate() {
        ClaimsGenerator claimsGenerator = getGenerator(date20100701, IndexMode.STEPWISE_NEXT, BaseDateMode.FIXED_DATE)

        claimsGenerator.doCalculation()
        assertEquals "P0, index effect on paid claim", 1.0392156863, claimsGenerator.outClaims[0].paidIncrementalIndexed / -50, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P1, index effect on paid claim", 1.0490196078, claimsGenerator.outClaims[0].paidIncrementalIndexed / -30, EPSILON

        doCalculationNextPeriod(claimsGenerator)
        assertEquals "P2, index effect on paid claim", 1.0784313725, claimsGenerator.outClaims[0].paidIncrementalIndexed / -20, EPSILON
    }



    private void doCalculationNextPeriod(ClaimsGenerator claimsGenerator) {
        claimsGenerator.resetOutChannels()      // in order to keep factors and patterns added for the first period
        claimsGenerator.periodScope.prepareNextPeriod()
        claimsGenerator.doCalculation()
    }
}
