package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPeriodStrategy extends IParameterObject {

    DateTime getStartCover();
    DateTime getEndCover();

    /**
     * We assume all periods are annual.
     * @return number of years
     */
    int getNumberOfPeriods();

    /**
     * @return all start period dates and the last end period date
     */
    List<DateTime> getDates();

    /**
     * @param date
     * @return true if date is between start and end of cover
     */
    boolean isCovered(DateTime date);

    /**
     * Used in order to synchronize the projection start date with the start cover 
     * @param date
     */
    void initStartCover(DateTime date);

    boolean currentPeriodContainsCover(IPeriodCounter periodCounter);
}
