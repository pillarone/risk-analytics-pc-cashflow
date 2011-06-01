package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.segment.ISegmentMarker;

/**
 * Doc: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1540
 * It contains all shared information of several ClaimCashflowPacket objects and is used as key.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): clarify index application order and effect on reported
public final class ClaimRoot implements IClaimRoot, Cloneable {

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

    private IPerilMarker peril;
    private ISegmentMarker segment;
    private IReinsuranceContractMarker reinsuranceContract;
    private ILegalEntityMarker legalEntity;

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

    public ClaimRoot withScale(double scaleFactor, IReinsuranceContractMarker reinsuranceContract) {
        ClaimRoot packet = withScale(scaleFactor);
        packet.reinsuranceContract = reinsuranceContract;
        return packet;
    }

    public ClaimRoot withScale(double scaleFactor) {
        ClaimRoot packet = (ClaimRoot) clone();
        packet.ultimate = ultimate * scaleFactor;
        return packet;
    }

    public ClaimRoot withExposure(ExposureInfo exposure){
        ClaimRoot packet = (ClaimRoot) clone();
        packet.exposure = exposure;
        return packet;
    }

    public double getUltimate() {
        return ultimate;
    }

    public boolean hasEvent() {
        return event != null;
    }

    public EventPacket getEvent() {
        return event;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public ExposureInfo getExposure() {
        return exposure;
    }

    public DateTime getExposureStartDate() {
        return exposureStartDate;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    /**
     * @param periodCounter
     * @return occurrence period in the context of the simulation engine
     */
    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        if (occurrencePeriod == null) {
            try {
                occurrencePeriod = periodCounter.belongsToPeriod(occurrenceDate);
            }
            catch (NotInProjectionHorizon ex) {
                LOG.debug(occurrenceDate + " is not in projection horizon");
            }
        }
        return occurrencePeriod;
    }

    public boolean hasSynchronizedPatterns() {
        return false;
    }

    public boolean hasTrivialPayout() {
        return true;
    }

    public boolean hasIBNR() {
        return false;
    }

    public IPerilMarker peril() { return peril; }
    public ISegmentMarker segment() { return segment; }
    public IReinsuranceContractMarker reinsuranceContract() { return reinsuranceContract; }

    @Override
    public ClaimRoot clone() {
        try {
            return (ClaimRoot) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(ultimate);
        result.append(separator);
        result.append(claimType);
        result.append(separator);
        result.append(occurrenceDate);
        return result.toString();
    }

    public  boolean hasExposureInfo() {
        return this.exposure != null;
    }
}
