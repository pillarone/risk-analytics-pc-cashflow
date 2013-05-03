package org.pillarone.riskanalytics.core.simulation

import com.google.common.collect.Lists
import org.joda.time.DateTime
import org.joda.time.Period

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestPeriodCounterUtilities {

    static IPeriodCounter getLimitedContinuousPeriodCounter(DateTime date, int numberOfPeriods) {
        return getLimitedContinuousPeriodCounter(date, Period.years(1), numberOfPeriods)
    }

    static IPeriodCounter getLimitedContinuousPeriodCounterWithDates(DateTime date, int numberOfPeriods) {
        List<DateTime> dates = Lists.newArrayList()
        dates.add(date)
        for( i in 1 .. numberOfPeriods ) {
            dates.add(date.plusYears(i))
        }
        return new JVariableLengthPeriodCounter(dates)
    }

    static IPeriodCounter getLimitedContinuousPeriodCounter(DateTime date, Period period, int numberOfPeriods) {
        return new LimitedContinuousPeriodCounter(date, period, numberOfPeriods)
    }
}
