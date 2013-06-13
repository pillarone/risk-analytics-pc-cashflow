package org.pillarone.riskanalytics.core.simulation

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TestPeriodScopeUtilities {

    public static PeriodScope getPeriodScope(DateTime date, int numberOfPeriods) {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date, numberOfPeriods);
        return new PeriodScope(periodCounter: periodCounter)
    }

    public static PeriodScope getPeriodScopeWithDates(DateTime date, int numberOfPeriods) {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounterWithDates(date, numberOfPeriods);
        return new PeriodScope(periodCounter: periodCounter)
    }
}
