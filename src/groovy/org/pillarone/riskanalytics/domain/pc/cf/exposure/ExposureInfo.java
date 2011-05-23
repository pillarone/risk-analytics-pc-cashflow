package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureInfo {

    private static Log LOG = LogFactory.getLog(ExposureInfo.class);

    private DateTime inceptionDate;
    private Integer inceptionPeriod;

    public ExposureInfo(DateTime inceptionDate, IPeriodCounter periodCounter) {
        this.inceptionDate = inceptionDate;
        try {
            inceptionPeriod = periodCounter.belongsToPeriod(inceptionDate);
        }
        catch (NotInProjectionHorizon ex) {
            LOG.debug("inceptionDate " + inceptionDate + " is not in projection horizon!");
        }
    }

    public ExposureInfo(PeriodScope periodScope) {
        this(periodScope.getPeriodCounter().getCurrentPeriodStart(), periodScope.getPeriodCounter());
    }

    public DateTime getInceptionDate() {
        return inceptionDate;
    }

    public Integer getInceptionPeriod() {
        return inceptionPeriod;
    }
}
