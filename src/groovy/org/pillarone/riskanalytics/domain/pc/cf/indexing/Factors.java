package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): implement baseDate usage
public class Factors {

    private FactorsPacket packet;
    private BaseDateMode baseDate;
    private IndexMode indexMode;


    public Factors(FactorsPacket packet, BaseDateMode baseDate, IndexMode indexMode) {
        this.packet = packet;
        this.baseDate = baseDate;
        this.indexMode = indexMode;
    }

    public Double getFactor(DateTime date) {
        switch (indexMode) {
            case CONTINUOUS:
                return packet.getFactorInterpolated(date);
            case STEPWISE_NEXT:
                return packet.getFactorCeiling(date);
            case STEPWISE_PREVIOUS:
                return packet.getFactorFloor(date);
        }
        return null;
    }

    public IndexPacket getIndices(DateTime date) {
        return new IndexPacket(
                packet.getFactorInterpolated(date),
                packet.getFactorFloor(date),
                packet.getFactorCeiling(date)
        );
    }
}
