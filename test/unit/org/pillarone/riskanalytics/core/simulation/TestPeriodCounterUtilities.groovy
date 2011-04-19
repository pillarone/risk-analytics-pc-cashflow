package org.pillarone.riskanalytics.core.simulation

import org.joda.time.DateTime
import org.joda.time.Period

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestPeriodCounterUtilities {

    static IPeriodCounter getLimitedContinuousPeriodCounter(DateTime date, int numberOfPeriods) {
        return getLimitedContinuousPeriodCounter(date, Period.years(1), numberOfPeriods)
    }

    static IPeriodCounter getLimitedContinuousPeriodCounter(DateTime date, Period period, int numberOfPeriods) {
        return new LimitedContinuousPeriodCounter(date, period, numberOfPeriods)
    }
}
