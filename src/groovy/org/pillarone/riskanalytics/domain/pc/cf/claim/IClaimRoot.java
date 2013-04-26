package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimRoot {

    public double getUltimate();
    public boolean hasEvent();
    public IEvent getEvent();
    public ClaimType getClaimType();
    /** in some contexts this is used to retrieve the inception date */
    public DateTime getExposureStartDate();
    public DateTime getOccurrenceDate();
    public Integer getOccurrencePeriod(IPeriodCounter periodCounter);
    public boolean occurrenceInCurrentPeriod(PeriodScope periodScope);
    public Integer getInceptionPeriod(IPeriodCounter periodCounter);
      /**
     * @return payout and reported pattern have the same period entries. True even if one of them is null
     */
    public boolean hasSynchronizedPatterns();
    public boolean hasTrivialPayout();
    public boolean hasIBNR();

    public IClaimRoot withScale(double scaleFactor);
}
