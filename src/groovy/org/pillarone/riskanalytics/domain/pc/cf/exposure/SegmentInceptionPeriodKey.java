package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SegmentInceptionPeriodKey {

    ISegmentMarker segment;
    Integer inceptionPeriod;

    public SegmentInceptionPeriodKey(ISegmentMarker segment, Integer inceptionPeriod) {
        this.segment = segment;
        this.inceptionPeriod = inceptionPeriod;
        if (segment == null || inceptionPeriod == null) {
            throw new SimulationException("Neither segment nor inception period may be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SegmentInceptionPeriodKey)) return false;

        SegmentInceptionPeriodKey that = (SegmentInceptionPeriodKey) o;

        if (!inceptionPeriod.equals(that.inceptionPeriod)) return false;
        if (!segment.equals(that.segment)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = segment.hashCode();
        result = 31 * result + inceptionPeriod.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SegmentInceptionPeriodKey{" +
                "segment=" + segment +
                ", inceptionPeriod=" + inceptionPeriod +
                '}';
    }
}
