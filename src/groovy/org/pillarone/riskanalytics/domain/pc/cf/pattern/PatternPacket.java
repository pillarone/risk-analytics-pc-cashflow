package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.DateFactors;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternPacket extends Packet {

    private static Log LOG = LogFactory.getLog(PatternPacket.class);

    protected List<Double> cumulativeValues;
    protected List<Period> cumulativePeriods;


    public static final PatternPacket PATTERN_TRIVIAL = new TrivialPattern();

    public PatternPacket() {
    }


    public PatternPacket(List<Double> cumulativeValues, List<Period> cumulativePeriods) {
        this.cumulativeValues = cumulativeValues;
        this.cumulativePeriods = cumulativePeriods;
    }

    /**
     * @param elapsedMonths
     * @return outstanding share after elapsedMonths using a linear interpolation if elapsedMonths is not part of the cumulativePeriods
     */
    public double outstandingShare(double elapsedMonths) {
        int indexAboveElapsedMonths = 0;
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths < cumulativePeriods.get(i).getMonths()) {
                indexAboveElapsedMonths = i;
                int numberOfMonthsBelowElapsedMonths = cumulativePeriods.get(indexAboveElapsedMonths - 1).getMonths();
                int numberOfMonthsAboveElapsedMonths = cumulativePeriods.get(indexAboveElapsedMonths).getMonths();
                double valueBelowElapsedMonths = cumulativeValues.get(indexAboveElapsedMonths - 1);
                double valueAboveElapsedMonths = cumulativeValues.get(indexAboveElapsedMonths);
                double periodRatio = (elapsedMonths - numberOfMonthsBelowElapsedMonths)
                        / (double) (numberOfMonthsAboveElapsedMonths - numberOfMonthsBelowElapsedMonths);
                double paidPortion = (valueAboveElapsedMonths - valueBelowElapsedMonths) * periodRatio;
                return 1 - valueBelowElapsedMonths - paidPortion;
            }
            else if (elapsedMonths == cumulativePeriods.get(i).getMonths()) {
                return 1 - cumulativeValues.get(i);
            }
        }
        // elapseMonths is after latest period
        return 0d;
    }

    // todo(sku): cache result as function is used quite often
    public boolean hasSameCumulativePeriods(PatternPacket other) {
        boolean synchronous = true;
        synchronous = size() == other.size();
        for (int developmentPeriod = 0; synchronous && developmentPeriod < size(); developmentPeriod++) {
            synchronous = incrementMonths(developmentPeriod).equals(other.incrementMonths(developmentPeriod));
        }
        return synchronous;
    }

    public List<DateFactors> getDateFactorsForCurrentPeriod(DateTime occurrenceDate, IPeriodCounter periodCounter) {
            List<DateFactors> dateFactors = new ArrayList<DateFactors>();
    //      todo(sku): avoid looping through complete pattern
            for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
                DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod));
    //                todo(sku): extend core plugin
    //                if (periodCounter.belongsToCurrentPeriod(date)) {
                if (!date.isBefore(periodCounter.getCurrentPeriodStart())
                        && date.isBefore(periodCounter.getNextPeriodStart())) {
                    dateFactors.add(new DateFactors(date, incrementFactor(devPeriod), cumulativeValues.get(devPeriod)));
                }
            }
            return dateFactors;
    }

    /**
     * @param elapsedMonths
     * @return nearest pattern index with month value lower elapsedMonths or null if elapsedMonths is after last period
     */
    public Integer nextPayoutIndex(double elapsedMonths) {
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths < cumulativePeriods.get(i).getMonths()) {
                return i;
            }
        }
        // elapseMonths is after latest period
        return null;
    }

    /**
     * @param elapsedMonths
     * @return nearest pattern index with month value lower or equal elapsedMonths or null if elapsedMonths is after last period
     */
    public Integer thisOrNextPayoutIndex(double elapsedMonths) {
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths <= cumulativePeriods.get(i).getMonths()) {
                return i;
            }
        }
        // elapseMonths is after latest period
        return null;
    }

    public int size() {
        return cumulativeValues.size();
    }

    public boolean isTrivial() {
        return size() == 0 || (size() == 1 && cumulativeValues.get(0) == 1d);
    }

    public double incrementFactor(int developmentPeriod) {
        if (developmentPeriod == 0) {
            return cumulativeValues.get(0);
        }
        return cumulativeValues.get(developmentPeriod) - cumulativeValues.get(developmentPeriod - 1);
    }

    public double incrementFactor(int developmentPeriod, double outstandingShare) {
        return incrementFactor(developmentPeriod) / outstandingShare;
    }

    public Integer incrementMonths(int developmentPeriod) {
        if (developmentPeriod >= size()) return null;
        if (developmentPeriod == 0) {
            return cumulativePeriods.get(0).getMonths();
        }
        else {
            return cumulativePeriods.get(developmentPeriod).getMonths() - cumulativePeriods.get(developmentPeriod - 1).getMonths();
        }
    }

    public List<Double> getCumulativeValues() {
        return cumulativeValues;
    }

    public List<Period> getCumulativePeriods() {
        return cumulativePeriods;
    }

    public Period getCumulativePeriod(int developmentPeriod) {
        return cumulativePeriods.get(developmentPeriod);
    }

    private static final class TrivialPattern extends PatternPacket {

        private TrivialPattern() {
            // todo(sku): use immutable lists
            cumulativePeriods = Arrays.asList(Period.days(0));
            cumulativeValues = Arrays.asList(1d);
        }
    }
}
