package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period

import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.utils.constraint.DateTimeConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class CustomPeriodStrategyTests {

    private DateTime date20110101 = new DateTime(2011,  1,  1, 0, 0, 0, 0)
    private DateTime date20120101 = new DateTime(2012,  1,  1, 0, 0, 0, 0)
    private DateTime date20120630 = new DateTime(2012,  6, 30, 0, 0, 0, 0)
    private DateTime date20120701 = new DateTime(2012,  7,  1, 0, 0, 0, 0)
    private DateTime date20121231 = new DateTime(2012, 12, 31, 0, 0, 0, 0)
    private DateTime date20130830 = new DateTime(2013,  8, 30, 0, 0, 0, 0)
    private DateTime date20130831 = new DateTime(2013,  8, 31, 0, 0, 0, 0)
    private DateTime date20131231 = new DateTime(2013, 12, 31, 0, 0, 0, 0)
    private DateTime date20140101 = new DateTime(2014,  1,  1, 0, 0, 0, 0)
    private DateTime date20150101 = new DateTime(2015,  1,  1, 0, 0, 0, 0)

    private IPeriodCounter periodCounter3y

    @Before
    void setUp() throws Exception {
        periodCounter3y = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20120101, 3)
    }

    @Test
    void isCoveredOneInterval() {
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
            [[new DateTime(2012, 1, 1, 0, 0, 0, 0)], [new DateTime(2013, 8, 31, 0, 0, 0, 0)]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))
        assertFalse periodStrategy.isCovered(date20110101)
        assertTrue periodStrategy.isCovered(date20120101)
        assertTrue periodStrategy.isCovered(date20120701)
        assertTrue periodStrategy.isCovered(date20121231)
        assertFalse periodStrategy.isCovered(date20131231)
    }

    @Test
    void isCoveredGap() {
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
            [[date20120101, date20130831], [date20120701, date20140101]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))
        assertFalse periodStrategy.isCovered(date20110101)
        assertTrue periodStrategy.isCovered(date20120101)
        assertTrue periodStrategy.isCovered(date20120630)
        assertFalse periodStrategy.isCovered(date20120701)
        assertFalse periodStrategy.isCovered(date20121231)
        assertFalse periodStrategy.isCovered(date20130830)
        assertTrue periodStrategy.isCovered(date20130831)
        assertTrue periodStrategy.isCovered(date20131231)
    }

    @Test
    void isCoveredTwoIntervals() {
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
            [[date20120101, date20120701], [date20120701, date20140101]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))
        assertFalse periodStrategy.isCovered(date20110101)
        assertTrue periodStrategy.isCovered(date20120101)
        assertTrue periodStrategy.isCovered(date20120630)
        assertTrue periodStrategy.isCovered(date20120701)
        assertTrue periodStrategy.isCovered(date20121231)
        assertTrue periodStrategy.isCovered(date20130830)
        assertTrue periodStrategy.isCovered(date20130831)
        assertTrue periodStrategy.isCovered(date20131231)
    }

    @Test
    void currentPeriodContainsCoverOneInterval() {
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
            [[new DateTime(2012, 1, 1, 0, 0, 0, 0)], [new DateTime(2013, 8, 31, 0, 0, 0, 0)]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))

        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter3y)
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter3y.next())
        assertFalse periodStrategy.currentPeriodContainsCover(periodCounter3y.next())
    }

    @Test
    void currentPeriodContainsCoverGap() {
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
            [[date20120101, date20140101], [date20120701, date20140101]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))

        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter3y)
        assertFalse periodStrategy.currentPeriodContainsCover(periodCounter3y.next())
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter3y.next())
    }

    @Test
    void currentPeriodContainsCoverTwoInterval() {
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
            [[date20120101, date20120701], [date20120701, date20131231]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))

        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter3y)
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter3y.next())
        assertFalse periodStrategy.currentPeriodContainsCover(periodCounter3y.next())
    }

    @Test
    void dateList(){
        CustomPeriodStrategy periodStrategy = new CustomPeriodStrategy(periods: new ConstrainedMultiDimensionalParameter(
                [
                [date20120101, date20120701, date20140101],
                [date20120701, date20131231, date20150101]
                ],
                ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER)))

        assert periodStrategy.getDates().contains(date20120101)
        assert periodStrategy.getDates().contains(date20120701)
        assert periodStrategy.getDates().contains(date20140101)
        assert periodStrategy.getDates().contains(date20150101)
        assert periodStrategy.getDates().size() == 4
        periodStrategy
    }
}
