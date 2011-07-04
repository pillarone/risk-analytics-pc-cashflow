package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.BeforeSimulationStartException;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.domain.pc.cf.claim.DateFactors;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternPacket extends Packet {

    private static Log LOG = LogFactory.getLog(PatternPacket.class);

    protected List<Double> cumulativeValues;
    protected List<Period> cumulativePeriods;

    /**
     * this field is required to enable different kinds of pattern within one mdp @see PayoutReportingCombinedPattern
     */
    private Class<? extends IPatternMarker> patternMarker;

    public PatternPacket() {
    }

    public PatternPacket(Class<? extends IPatternMarker> patternMarker, List<Double> cumulativeValues, List<Period> cumulativePeriods) {
        this.patternMarker = patternMarker;
        this.cumulativeValues = cumulativeValues;
        this.cumulativePeriods = cumulativePeriods;
    }

    /**
     * @param elapsedMonths
     * @return outstanding share after elapsedMonths using a linear interpolation if elapsedMonths is not part of the cumulativePeriods
     */
    public double outstandingShare(double elapsedMonths) {
        int indexAboveElapsedMonths = 0;
        if (elapsedMonths < 0){
            throw new IllegalArgumentException("elapsed months are negative!");
        }
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

    public boolean hasSameCumulativePeriods(PatternPacket other) {
        boolean synchronous = size() == other.size();
        for (int developmentPeriod = 0; synchronous && developmentPeriod < size(); developmentPeriod++) {
            synchronous = incrementMonths(developmentPeriod).equals(other.incrementMonths(developmentPeriod));
        }
        return synchronous;
    }

    public static boolean hasSameCumulativePeriods(PatternPacket payout, PatternPacket reporting, boolean payoutPatternMaybeLonger) {
        boolean sameSizeOrPayoutLonger = payout.size() >= reporting.size();
        boolean samePeriods = sameSizeOrPayoutLonger;
        if (sameSizeOrPayoutLonger) {
            for (int developmentPeriod = 0; samePeriods && developmentPeriod < reporting.size(); developmentPeriod++) {
                samePeriods = payout.incrementMonths(developmentPeriod).equals(reporting.incrementMonths(developmentPeriod));
            }
        }
        return samePeriods;
    }

    /**
     * @param occurrenceDate
     * @param periodCounter
     * @param returnPrevious if nothing is found in current period return value of last period containing information
     * @return
     */
    public List<DateFactors> getDateFactorsForCurrentPeriod(DateTime occurrenceDate, IPeriodCounter periodCounter,
                                                            boolean returnPrevious) {
        List<DateFactors> dateFactors = new ArrayList<DateFactors>();       //      todo(sku): avoid looping through complete pattern
        double previousCumulativeValue = 0;
        boolean previousBeforeLastElement = false;
        DateTime previousDate = null;
        for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
            DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod));
            if (!date.isBefore(periodCounter.startOfFirstPeriod()) && periodCounter.belongsToCurrentPeriod(date)) {
                dateFactors.add(new DateFactors(date, incrementFactor(devPeriod), cumulativeValues.get(devPeriod)));
            }
            else if (date.isBefore(periodCounter.getCurrentPeriodStart())) {
                previousDate = date;
                previousCumulativeValue = cumulativeValues.get(devPeriod);
            }
            else if (date.isAfter(periodCounter.getCurrentPeriodEnd())) {
                previousBeforeLastElement = true;
                break;
            }
        }
        if (returnPrevious && previousBeforeLastElement && dateFactors.isEmpty() && previousDate != null) {
            dateFactors.add(new DateFactors(previousDate, 0, previousCumulativeValue));
        }
        return dateFactors;
    }

    public List<DateFactors> getDateFactorsTillStartOfCurrentPeriod(DateTime occurrenceDate, IPeriodCounter periodCounter) {
        List<DateFactors> dateFactors = new ArrayList<DateFactors>();       //      todo(sku): avoid looping through complete pattern
        for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
            DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod));
            if (date.isBefore(periodCounter.getCurrentPeriodStart())) {
                dateFactors.add(new DateFactors(date, incrementFactor(devPeriod), cumulativeValues.get(devPeriod)));
            }
        }
        return dateFactors;
    }

    public List<DateFactors> getDateFactorsForCurrentPeriod(IPeriodCounter periodCounter) {
        return getDateFactorsForCurrentPeriod(periodCounter.getCurrentPeriodStart(), periodCounter, false);
    }

    /**
     * @param elapsedMonths
     * @return nearest pattern index with month value greater elapsedMonths or null if elapsedMonths is after last period
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
     * @return nearest pattern index with month value greater or equal elapsedMonths or null if elapsedMonths is after last period
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

    /**
     * @param elapsedMonths
     * @return nearest pattern index with month value lower or equal elapsedMonths or null if elapsedMonths is after last period
     */
    public Integer thisOrPreviousPayoutIndex(double elapsedMonths) {
        int index = -1;  // elapseMonths is before first period
        for (int i = 0; i < cumulativePeriods.size(); i++) {
            if (elapsedMonths >= cumulativePeriods.get(i).getMonths()) {
                index +=1;
            }
            else {
                break;
            }
        }
        return index > -1 ? index : null;
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

    public Period getLastCumulativePeriod() {
        return cumulativePeriods.get(size() - 1);
    }

    public Period getCumulativePeriod(int developmentPeriod) {
        return cumulativePeriods.get(developmentPeriod);
    }

    public boolean isPayoutPattern() {
        return patternMarker == IPayoutPatternMarker.class;
    }

    public boolean isReportingPattern() {
        return patternMarker == IReportingPatternMarker.class;
    }

    public boolean isRecoveryPattern() {
        return patternMarker == IRecoveryPatternMarker.class;
    }

    public boolean isPremiumPattern() {
        return patternMarker == IPremiumPatternMarker.class;
    }

    public boolean samePatternType(Class<? extends IPatternMarker> other) {
        return patternMarker.equals(other);
    }

    public static final class TrivialPattern extends PatternPacket {

        public TrivialPattern(Class<? extends IPatternMarker> patternMarker) {
            // todo(sku): use immutable lists
            super(patternMarker, Arrays.asList(1d), Arrays.asList(Period.days(0)));
        }
    }
}
