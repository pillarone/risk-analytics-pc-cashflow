package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractPeriodStrategy extends AbstractParameterObject implements IPeriodStrategy {

    public boolean isCovered(DateTime date) {
        // interval left closed, right open
        return !getStartCover().isAfter(date) && date.isBefore(getEndCover());
    }

    public boolean currentPeriodContainsCover(IPeriodCounter periodCounter) {
        int currentPeriod = periodCounter.currentPeriodIndex();
        int startCoverPeriod = periodCounter.belongsToPeriod(getStartCover());
        int endCoverPeriod = periodCounter.belongsToPeriod(getEndCover().minusDays(1));
        return (startCoverPeriod <= currentPeriod && currentPeriod <= endCoverPeriod);
    }

    public void initStartCover(DateTime date) {
    }

    public List<DateTime> getDates() {
        throw new SimulationException("Not implemented");
    }
}
