package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Factors {

    private FactorsPacket packet;
    private BaseDateMode baseDate;
    private IndexMode indexMode;
    private DateTime fixedDate;


    public Factors(FactorsPacket packet, BaseDateMode baseDate, IndexMode indexMode, DateTime fixedDate) {
        this.packet = packet;
        this.baseDate = baseDate;
        this.indexMode = indexMode;
        this.fixedDate = fixedDate;
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

    /**
     * Evaluates the baseDate property to define the evaluation date of getFactor(date)
     * @param periodCounter
     * @param dateOfLoss
     * @return
     */
    public Double getFactor(IPeriodCounter periodCounter, DateTime dateOfLoss) {
        return getFactor(evaluateBaseDate(periodCounter, dateOfLoss));
    }

    public IndexPacket getIndices(DateTime date) {
        return new IndexPacket(
                packet.getFactorInterpolated(date),
                packet.getFactorFloor(date),
                packet.getFactorCeiling(date)
        );
    }

    /**
     * Evaluates the baseDate property to define the evaluation date of getIndices(date)
     * @param periodCounter
     * @param dateOfLoss
     * @return
     */
    public IndexPacket getIndices(IPeriodCounter periodCounter, DateTime dateOfLoss) {
        return getIndices(evaluateBaseDate(periodCounter, dateOfLoss));
    }

    /**
     * @param periodCounter
     * @param dateOfLoss
     * @return evaluation date according to baseDate property
     */
    private DateTime evaluateBaseDate(IPeriodCounter periodCounter, DateTime dateOfLoss) {
        switch (baseDate) {
            case DATE_OF_LOSS:
                return dateOfLoss;
            case DAY_BEFORE_FIRST_PERIOD:
                return periodCounter.startOfFirstPeriod().minusDays(1);
            case START_OF_PROJECTION:
                return periodCounter.startOfFirstPeriod();
            case FIXED_DATE:
                return fixedDate;
        }
        return null;
    }
}
