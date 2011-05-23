package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

import java.util.List;
import java.util.Map;

/**
 * Contains the different index mode values. Should be filled with values from period start date
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class IndexPacket extends MultiValuePacket {

    private double continuous = 1;
    private double stepwisePrevious = 1;
    private double stepwiseNext = 1;

    public IndexPacket() {
    }

    public IndexPacket(double continuous, double stepwisePrevious, double stepwiseNext) {
        this.continuous = continuous;
        this.stepwisePrevious = stepwisePrevious;
        this.stepwiseNext = stepwiseNext;
    }

    /**
     * @param factorsPackets
     * @param evaluationDate typically begin of period
     */
    public IndexPacket(FactorsPacket factorsPackets, DateTime evaluationDate) {
        continuous *= factorsPackets.getFactorInterpolated(evaluationDate);
        stepwisePrevious *= factorsPackets.getFactorFloor(evaluationDate);
        stepwiseNext *= factorsPackets.getFactorCeiling(evaluationDate);
    }

    public void multiply(IndexPacket other) {
        continuous = other.continuous;
        stepwisePrevious *= other.stepwisePrevious;
        stepwiseNext *= other.stepwiseNext;
    }
}
