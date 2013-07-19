package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period

import org.joda.time.DateTime
import org.junit.Test
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class AnnualPeriodStrategyTests extends GroovyTestCase {
    DateTime date20100101 = new DateTime(2010, 1, 1, 0, 0, 0, 0)
    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    DateTime date20120101 = new DateTime(2012, 1, 1, 0, 0, 0, 0)
    DateTime date20130101 = new DateTime(2013, 1, 1, 0, 0, 0, 0)
    DateTime date20140101 = new DateTime(2014, 1, 1, 0, 0, 0, 0)
    DateTime date20121231 = new DateTime(2012, 12, 31, 0, 0, 0, 0)

    @Test
    void currentPeriodContainsCover() {

        IPeriodStrategy periodStrategy = PeriodStrategyType.getStrategy(PeriodStrategyType.ANNUAL, ['startCover': date20100101, 'numberOfYears': 3])
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date20100101, 3)
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter)
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter.next())
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter.next())

        periodStrategy = PeriodStrategyType.getStrategy(PeriodStrategyType.ANNUAL, ['startCover': date20100101, 'numberOfYears': 2])
        periodCounter.reset()
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter)
        assertTrue periodStrategy.currentPeriodContainsCover(periodCounter.next())
        assertFalse periodStrategy.currentPeriodContainsCover(periodCounter.next())
    }

    @Test
    void getDates(){
        IPeriodStrategy periodStrategy = PeriodStrategyType.getStrategy(PeriodStrategyType.ANNUAL, ['startCover': date20100101, 'numberOfYears': 3])
        assert periodStrategy.getDates().contains(date20100101)
        assert periodStrategy.getDates().contains(date20110101)
        assert periodStrategy.getDates().contains(date20120101)
        assert periodStrategy.getDates().contains(date20130101)
        assert periodStrategy.getDates().size() == 4
    }
}
