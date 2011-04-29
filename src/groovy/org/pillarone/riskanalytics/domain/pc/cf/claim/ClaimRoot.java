package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.segment.ISegmentMarker;

/**
 * Doc: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1540
 * It contains all shared information of several ClaimCashflowPacket objects and is used as key.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): clarify index application order and effect on reported
public final class ClaimRoot implements IClaimRoot {

    private static Log LOG = LogFactory.getLog(ClaimRoot.class);

    private double ultimate;
    private EventPacket event;
    private ClaimType claimType;
    private ExposureInfo exposureInfo;
    private DateTime exposureStartDate;
    private DateTime occurrenceDate;

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

    public ExposureInfo getExposureInfo() {
        return exposureInfo;
    }

    public DateTime getExposureStartDate() {
        return exposureStartDate;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
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
}
