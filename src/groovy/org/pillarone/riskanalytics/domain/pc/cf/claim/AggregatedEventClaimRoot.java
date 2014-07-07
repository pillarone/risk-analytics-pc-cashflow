package org.pillarone.riskanalytics.domain.pc.cf.claim;

import edu.emory.mathcs.backport.java.util.Collections;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent;

import java.util.Collection;

public class AggregatedEventClaimRoot implements IClaimRoot {

    private final Collection<IClaimRoot> grossClaims;
    private final IEvent iEvent;

    public AggregatedEventClaimRoot(Collection<IClaimRoot> grossClaims, IEvent event) {
        iEvent = event;
        for (IClaimRoot grossClaim : grossClaims) {
            if(!grossClaim.getEvent().equals(event)) {
                throw new SimulationException("Events do not match on aggregated event root this is not allowed. Please contract development");
            }
        }
        this.grossClaims = Collections.unmodifiableCollection(grossClaims);
    }

    @Override
    public double getUltimate() {
        double ultimate = 0d;
        for (IClaimRoot grossClaim : grossClaims) {
            ultimate += grossClaim.getUltimate();
        }
        return ultimate;
    }

    @Override
    public boolean hasEvent() {
        return true;
    }

    @Override
    public IEvent getEvent() {
        return iEvent;
    }

    @Override
    public ClaimType getClaimType() {
        return ClaimType.AGGREGATED_EVENT;
    }

    @Override
    public DateTime getExposureStartDate() {
        return null;
    }

    @Override
    public DateTime getOccurrenceDate() {
        return iEvent.getDate();
    }

    @Override
    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        return periodCounter.belongsToPeriod(getOccurrenceDate());
    }

    @Override
    public boolean occurrenceInCurrentPeriod(PeriodScope periodScope) {
        return periodScope.getCurrentPeriod() == getOccurrencePeriod(periodScope.getPeriodCounter());
    }

    @Override
    public Integer getInceptionPeriod(IPeriodCounter periodCounter) {
        return periodCounter.belongsToPeriod(getOccurrenceDate());
    }

    @Override
    public boolean hasSynchronizedPatterns() {
        throw new SimulationException("");
    }

    @Override
    public boolean hasTrivialPayout() {
        throw new SimulationException("");
    }

    @Override
    public boolean hasIBNR() {
        throw new SimulationException("");
    }

    @Override
    public IClaimRoot withScale(double scaleFactor) {
        throw new SimulationException("");
    }

    @Override
    public String getPacketId() {
        return (grossClaims.size() > 0) ? grossClaims.iterator().next().getPacketId() : "";
    }

    @Override
    public String toString() {
        return "CededClaimRoot{" +
                "ultimate=" + getUltimate() +
                ", event=" + iEvent.toString() +
                '}';
    }
}
