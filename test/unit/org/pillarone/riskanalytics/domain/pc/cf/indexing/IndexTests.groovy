package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.util.MathUtils
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    DateTime date20010101 = new DateTime(2001, 1, 1, 0, 0, 0, 0)
    DateTime date20020101 = new DateTime(2002, 1, 1, 0, 0, 0, 0)
    DateTime date20030222 = new DateTime(2003, 2, 22, 0, 0, 0, 0)
    DateTime date20030316 = new DateTime(2003, 3, 16, 0, 0, 0, 0)
    DateTime date20041212 = new DateTime(2004, 12, 12, 0, 0, 0, 0)
    DateTime date20050421 = new DateTime(2005, 4, 21, 0, 0, 0, 0)
    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)

    void testTrivialIndex() {
        Index index = new Index(parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.NONE, [:]))
        index.doCalculation()
        assertEquals "no factor", 0, index.outFactors.size()
    }

    void testStochasticIndex() {
        MathUtils.initRandomStreamBase(123)

        Index index = new Index(parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.STOCHASTIC,
                [startDate: date20110101,
                        distribution: DistributionType.getStrategy(DistributionType.LOGNORMAL, ['mean': 0.03, 'stDev': 0.2])]))
        index.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        index.doCalculation()

        assertEquals "one factor only", 1, index.outFactors.size()
        assertEquals "factor for 2011-01-01", 1d, index.outFactors[0].getFactorAtDate(date20110101) // 1 by definition

        index.reset()
        index.periodScope.prepareNextPeriod()
        index.doCalculation()

        assertEquals "one factor only", 1, index.outFactors.size()
        assertEquals "factor for 2012-01-01", 1.000028716181532, index.outFactors[0].getFactorAtDate(date20110101.plusYears(1)), EPSILON

        index.reset()
        index.periodScope.prepareNextPeriod()
        index.doCalculation()
        assertEquals "one factor only", 1, index.outFactors.size()
        assertEquals "factor for 2013-01-01", 1.1039751667482924, index.outFactors[0].getFactorAtDate(date20110101.plusYears(2)), EPSILON

        index.reset()
        index.periodScope.prepareNextPeriod()
        index.doCalculation()
        assertEquals "one factor only", 1, index.outFactors.size()
        assertEquals "factor for 2014-01-01", 1.1049224089826477, index.outFactors[0].getFactorAtDate(date20110101.plusYears(3)), EPSILON
    }

    void testStochasticIndexSystematicSeverities() {

        EventSeverity severity1 = new EventSeverity(value: 0.8, event: new EventPacket(new DateTime(2011, 1, 2, 0, 0, 0, 0)))
        EventSeverity severity2 = new EventSeverity(value: 0.9, event: new EventPacket(new DateTime(2011, 2, 2, 0, 0, 0, 0)))
        EventSeverity severity3 = new EventSeverity(value: 0.95, event: new EventPacket(new DateTime(2011, 3, 2, 0, 0, 0, 0)))
        List<String> targets = new ArrayList<String>(["motor hull", "hail", "index"])
        List<EventSeverity> severities = new ArrayList<EventSeverity>([severity1, severity2, severity3])

        Index index = new Index(name: "index", parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.STOCHASTIC,
                [startDate: date20110101,
                        distribution: DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 4.5])]))
        index.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)

        EventDependenceStream stream1 = new EventDependenceStream(targets, severities)
        index.inEventSeverities << stream1
        index.doCalculation()

        assertEquals "one factor only", 1, index.outFactors.size()
        assertEquals "factor for 2012-01-01", 1, index.outFactors[0].getFactorAtDate(date20110101)
        assertEquals "factor for 2012-01-01", 1 + 4.5 * 0.95d, index.outFactors[0].getFactorAtDate(date20110101.plusYears(1))

    }

    void testDeterministicAnnualChange() {
        Index index = new Index(parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICANNUALCHANGE,
                [changes: new ConstrainedMultiDimensionalParameter(
                        [[date20010101, date20020101, date20030222, date20030316, date20041212, date20050421],
                               [0.0222, 0d, 0.0094, 0.0188, 0.0267, 0.0155]],
                        [AnnualIndexTableConstraints.DATE, AnnualIndexTableConstraints.ANNUAL_CHANGE],
                        ConstraintsFactory.getConstraints(AnnualIndexTableConstraints.IDENTIFIER))]))
        index.doCalculation()

        double factor1 = Math.pow(1.0094,22d/365d)
        double factor2 = Math.pow(1.0188,291d/365d+346d/366d)
        double factor3 = Math.pow(1.0267,20d/366d+110d/365d)
        assertEquals "number of packet", 1, index.outFactors.size()
        assertEquals "factor for 2001-01-01", 1d, index.outFactors[0].getFactorAtDate(date20010101), EPSILON
        assertEquals "factor for 2002-01-01", 1.0222, index.outFactors[0].getFactorAtDate(date20020101), EPSILON
        assertEquals "factor for 2003-02-22", 1.0222, index.outFactors[0].getFactorAtDate(date20030222), EPSILON
        assertEquals "factor for 2003-03-16", 1.0222*factor1, index.outFactors[0].getFactorAtDate(date20030316), EPSILON
        assertEquals "factor for 2004-12-12", 1.0222*factor1*factor2, index.outFactors[0].getFactorAtDate(date20041212), EPSILON
        assertEquals "factor for 2005-04-21", 1.0222*factor1*factor2*factor3, index.outFactors[0].getFactorAtDate(date20050421), EPSILON
    }



    void testAgeToAgeIndexRatios() {

        Index index = new Index(parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.AGE_TO_AGE,
                [ratios: new ConstrainedMultiDimensionalParameter(
                        [[date20010101, date20020101, date20030222, date20030316, date20041212, date20050421],
                                [1.05, 0.99, 1.1, 1.4, 1.2, 1.0]],
                        [LinkRatioIndexTableConstraints.DATE, LinkRatioIndexTableConstraints.LINK_TO_LINK_RATIO],
                        ConstraintsFactory.getConstraints(LinkRatioIndexTableConstraints.IDENTIFIER))]))
        index.doCalculation()

        assertEquals "number of packet", 1, index.outFactors.size()
        assertEquals "factor for 2001-01-01", 1d, index.outFactors[0].getFactorAtDate(date20010101), 1E-5
        assertEquals "factor for 2002-01-01", 1.05, index.outFactors[0].getFactorAtDate(date20020101), 1E-5
        assertEquals "factor for 2003-02-22", 1.0395, index.outFactors[0].getFactorAtDate(date20030222), 1E-5
        assertEquals "factor for 2003-03-16", 1.14345, index.outFactors[0].getFactorAtDate(date20030316), 1E-5
        assertEquals "factor for 2004-12-12", 1.60083, index.outFactors[0].getFactorAtDate(date20041212), 1E-5
        assertEquals "factor for 2005-04-21", 1.920996, index.outFactors[0].getFactorAtDate(date20050421), 1E-5
    }
}
