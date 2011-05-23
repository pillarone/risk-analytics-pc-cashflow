package org.pillarone.riskanalytics.core.simulation

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestIterationScopeUtilities {

    static IterationScope getIterationScope(DateTime date, int numberOfPeriods) {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(date, numberOfPeriods)
        IterationScope iterationScope = new IterationScope(periodScope: periodScope)
//        for (int i = 0; i < numberOfPeriods; i++) {
            iterationScope.periodStores.add(new PeriodStore(periodScope))
//        }
        return iterationScope
    }

}
