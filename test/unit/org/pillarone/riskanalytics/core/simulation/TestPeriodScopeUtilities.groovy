package org.pillarone.riskanalytics.core.simulation

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestPeriodScopeUtilities {

    static PeriodScope getPeriodScope(DateTime date, int numberOfPeriods) {
        IPeriodCounter periodCounter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(date, numberOfPeriods);
        return new PeriodScope(periodCounter: periodCounter)
    }
}