package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

/**
 * Doc: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1540
 * It contains all shared information of several ClaimCashflowPacket objects and is used as key.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): clarify index application order and effect on reported
public class ClaimRoot implements IClaimRoot, Cloneable {

    private static Log LOG = LogFactory.getLog(ClaimRoot.class);

    private double ultimate;
    private EventPacket event;
    private ClaimType claimType;
    private ExposureInfo exposure;
    private DateTime exposureStartDate;
    private DateTime occurrenceDate;
    private Integer occurrencePeriod;

    /** counts the currently existing ClaimCashflowPacket referencing this instance */
    private int childCounter;

    public ClaimRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate, DateTime occurrenceDate) {
        this.ultimate = ultimate;
        this.claimType = claimType;
        this.exposureStartDate = exposureStartDate;
        this.occurrenceDate = occurrenceDate;
    }

    public ClaimRoot(double ultimate, ClaimType claimType, DateTime exposureStartDate, DateTime occurrenceDate, EventPacket event){
        this(ultimate, claimType, exposureStartDate, occurrenceDate);
        this.event = event;
    }

    /**
     * Helper c'tor in order to get a derived ClaimRoot object with a modified ultimate value
     * @param ultimate
     * @param claimRoot
     */
    public ClaimRoot(double ultimate, IClaimRoot claimRoot) {
        this(ultimate, claimRoot.getClaimType(), claimRoot.getExposureStartDate(), claimRoot.getOccurrenceDate(), claimRoot.getEvent());
    }

    /**
     * copy c'tor
     * @param claimRoot
     */
    public ClaimRoot(ClaimRoot claimRoot) {
        this(claimRoot.getUltimate(), claimRoot);
    }

    public final IClaimRoot withScale(double scaleFactor) {
        ClaimRoot packet = new ClaimRoot(this);
        packet.ultimate = ultimate * scaleFactor;
        return packet;
    }

    public final ClaimRoot withExposure(ExposureInfo exposure){
        ClaimRoot packet = new ClaimRoot(this);
        packet.exposure = exposure;
        return packet;
    }

    public final double getUltimate() {
        return ultimate;
    }

    public final boolean hasEvent() {
        return event != null;
    }

    public final EventPacket getEvent() {
        return event;
    }

    public final ClaimType getClaimType() {
        return claimType;
    }

    public final ExposureInfo getExposureInfo() {
        return exposure;
    }

    public final boolean hasExposureInfo() {
        return exposure != null;
    }

    public final DateTime getExposureStartDate() {
        return exposureStartDate;
    }

    public final DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    /**
     * @param periodCounter
     * @return occurrence period in the context of the simulation engine. If the occurrenceDate is outside the projection
     *          horizon the framework will throw a NotInProjectionHorizon.
     */
    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        if (occurrencePeriod == null) {
            occurrencePeriod = periodCounter.belongsToPeriod(occurrenceDate);
        }
        return occurrencePeriod;
    }

    /**
     * @param periodScope
     * @return true if occurrence period is the same as the current period of the periodScope
     */
    public final boolean occurrenceInCurrentPeriod(PeriodScope periodScope) {
        return getOccurrencePeriod(periodScope.getPeriodCounter()) == periodScope.getCurrentPeriod();
    }

    /**
     * @param periodScope
     * @return true if inception period is the same as the current period of the periodScope
     */
    public final boolean exposureStartInCurrentPeriod(PeriodScope periodScope) {
        return periodScope.getPeriodCounter().belongsToCurrentPeriod(exposureStartDate);
    }

    /**
     * @param periodCounter
     * @return if exposure info is attached return its inception period, else the period the exposureStartDate belongs to
     *          or else the occurrence period. If the exposureStartDate is not within the projection horizon, the projection
     *          horizon the framework will throw a NotInProjectionHorizon.
     */
    public Integer getInceptionPeriod(IPeriodCounter periodCounter) {
        if (hasExposureInfo()) {
            return exposure.getInceptionPeriod();
        }
        else if (exposureStartDate != null) {
            return periodCounter.belongsToPeriod(exposureStartDate);
        }
        return getOccurrencePeriod(periodCounter);
    }

    public final boolean hasSynchronizedPatterns() {
        return false;
    }

    public final boolean hasTrivialPayout() {
        return true;
    }

    public final boolean hasIBNR() {
        return false;
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(ultimate);
        result.append(separator);
        result.append(claimType);
        result.append(separator);
        result.append(DateTimeUtilities.formatDate.print(occurrenceDate));
        return result.toString();
    }
}
