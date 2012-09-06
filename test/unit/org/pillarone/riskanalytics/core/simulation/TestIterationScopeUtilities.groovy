package org.pillarone.riskanalytics.core.simulation

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TestIterationScopeUtilities {

    public static IterationScope getIterationScope(DateTime date, int numberOfPeriods) {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(date, numberOfPeriods)
        IterationScope iterationScope = new IterationScope(periodScope: periodScope)
        iterationScope.periodStores.add(new PeriodStore(periodScope))
        iterationScope.currentIteration = 1
        return iterationScope
    }

}
