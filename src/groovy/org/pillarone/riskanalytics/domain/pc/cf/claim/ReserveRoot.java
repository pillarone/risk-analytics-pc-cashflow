package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;

/**
 * Doc: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1540
 * It contains all shared information of several ClaimCashflowPacket objects and is used as key. As reserves are modelled
 * using this object too it might occur in this case that the occurrence and inception date are outside the projection
 * horizon and therefore the corresponding period properties will be null.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public final class ReserveRoot extends ClaimRoot {

    private static Log LOG = LogFactory.getLog(ReserveRoot.class);

    private Integer occurrencePeriod;

    public ReserveRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate, DateTime occurrenceDate) {
        super(ultimate, claimType, exposureStartDate, occurrenceDate);
    }

    /**
     * @param periodCounter
     * @return occurrence period in the context of the simulation engine or null if the occurrenceDate is not within
     *          the projection horizon.
     */
    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        return null;
    }

    /**
     * @param periodCounter
     * @return if exposure info is attached return its inception period, else the period the exposureStartDate belongs to
     *          or else the occurrence period. If the exposureStartDate is not within the projection horizon, null
     *          is returned.
     */
    public Integer getInceptionPeriod(IPeriodCounter periodCounter) {
        return null;
    }
}
