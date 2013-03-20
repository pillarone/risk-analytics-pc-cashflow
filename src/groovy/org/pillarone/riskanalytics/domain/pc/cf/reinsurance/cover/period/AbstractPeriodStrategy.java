package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractPeriodStrategy extends AbstractParameterObject implements IPeriodStrategy {

    public boolean isCovered(DateTime date) {
        // interval right closed, left open
        return !getStartCover().isAfter(date) && date.isBefore(getEndCover());
    }

    public void initStartCover(DateTime date) {
    }
}
