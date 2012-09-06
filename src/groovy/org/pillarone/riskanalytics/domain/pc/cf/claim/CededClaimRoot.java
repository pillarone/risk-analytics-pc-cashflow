package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

public class CededClaimRoot implements ICededRoot {

    private static Log LOG = LogFactory.getLog(CededClaimRoot.class);

    private double ultimate;
    private IClaimRoot grossClaim;

    public CededClaimRoot(double ultimate, IClaimRoot grossClaim) {
        this.ultimate = ultimate;
        this.grossClaim = grossClaim;
    }

    public double getUltimate() {
        return ultimate;
    }

    public boolean hasEvent() {
        return grossClaim.hasEvent();
    }

    public EventPacket getEvent() {
        return grossClaim.getEvent();
    }

    public ClaimType getClaimType() {
        return ClaimType.CEDED;
    }

    public DateTime getExposureStartDate() {
        return grossClaim.getExposureStartDate();
    }

    public DateTime getOccurrenceDate() {
        return grossClaim.getOccurrenceDate();
    }

    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        return grossClaim.getOccurrencePeriod(periodCounter);
    }

    public boolean occurrenceInCurrentPeriod(PeriodScope periodScope) {
        return grossClaim.occurrenceInCurrentPeriod(periodScope);
    }

    public Integer getInceptionPeriod(IPeriodCounter periodCounter) {
        return grossClaim.getInceptionPeriod(periodCounter);
    }

    public boolean hasSynchronizedPatterns() {
        return grossClaim.hasSynchronizedPatterns();
    }

    public boolean hasTrivialPayout() {
        return false;
    }

    public boolean hasIBNR() {
        return grossClaim.hasIBNR();
    }

    public IClaimRoot getGrossClaim() {
        return grossClaim;
    }

    public IClaimRoot withScale(double scaleFactor) {
        throw new SimulationException("A ceded claim should never be scaled");
    }

    @Override
    public String toString() {
        return "CededClaimRoot{" +
                "ultimate=" + ultimate +
                ", grossClaim=" + grossClaim.toString() +
                '}';
    }
}
