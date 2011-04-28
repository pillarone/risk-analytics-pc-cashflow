package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimRoot {

    public double getUltimate();
    public boolean hasEvent();
    public EventPacket getEvent();
    public ClaimType getClaimType();
    public ExposureInfo getExposureInfo();
    public DateTime getExposureStartDate();
    public DateTime getOccurrenceDate();
      /**
     * @return payout and reported pattern have the same period entries. True even if one of them is null
     */
    public boolean hasSynchronizedPatterns();
    public boolean hasTrivialPayout();
    public boolean hasIBNR();
}
