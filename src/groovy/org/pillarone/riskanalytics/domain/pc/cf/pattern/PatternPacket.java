package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.DateFactors;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternPacket extends Packet implements Cloneable {

    private static Log LOG = LogFactory.getLog(PatternPacket.class);

    protected List<Double> cumulativeValues;
    protected List<Period> cumulativePeriods;
    private boolean stochasticHitPattern = false;

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
        checkIncreasingPeriodLengths();
    }

    public PatternPacket(Class<? extends IPatternMarker> patternMarker, List<Double> cumulativeValues, 
                         List<Period> cumulativePeriods, boolean stochasticHitPattern) {
        this(patternMarker, cumulativeValues, cumulativePeriods);
        this.stochasticHitPattern = stochasticHitPattern;
    }

    /**
     * @param originalPattern used for the patternMarker and stochasticHitPattern property
     * @param cumulativeValues
     * @param cumulativePeriods
     */
    public PatternPacket(PatternPacket originalPattern, List<Double> cumulativeValues, List<Period> cumulativePeriods) {
        this(originalPattern.patternMarker, cumulativeValues, cumulativePeriods, originalPattern.stochasticHitPattern);
    }

    /**
     * throws an IllegalArgumentException if cumulativePeriods are not of increasing duration.
     */
    private void checkIncreasingPeriodLengths() {
        DateTime reference = new DateTime(2010,1,1,0,0,0,0);
        Period previousPeriod = Period.days(0);
        for (Period period : cumulativePeriods) {
            if (reference.plus(previousPeriod).isAfter(reference.plus(period))) {
                throw new PeriodsNotIncreasingException();
            }
            previousPeriod = period;
        }
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

    /**
     * @return same instance for non stochastic patterns, new instance for stochastic patterns
     */
    public PatternPacket get() {
        if (stochasticHitPattern) {
            // create a new instance with 0 for cumulative values below the hit probability and 1 if above
            IRandomNumberGenerator randomNumberGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
            Double hitProbability = (Double) randomNumberGenerator.nextValue();
            List<Double> adjustedCumulativeValues = new ArrayList<Double>();
            for (Double cumulativeValue : cumulativeValues) {
                if (cumulativeValue <= hitProbability) {
                    adjustedCumulativeValues.add(0d);
                } else {
                    adjustedCumulativeValues.add(1d);
                }
            }
            return new PatternPacket(patternMarker, adjustedCumulativeValues, cumulativePeriods, false);
        }
        else {
            return this;
        }
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
            else if (!date.isBefore(periodCounter.getCurrentPeriodEnd())) {
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

    public void insertTrivialPeriod(Period period, int index) {
        cumulativeValues.add(index, cumulativeValues.get(index - 1));
        cumulativePeriods.add(index, period);
    }

    public static final class TrivialPattern extends PatternPacket {

        public TrivialPattern(Class<? extends IPatternMarker> patternMarker) {
            // todo(sku): use immutable lists
            super(patternMarker, Arrays.asList(1d), Arrays.asList(Period.days(0)));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PatternPacket) {
            boolean difference = true;           //samePatternType((Class<? extends IPatternMarker>) obj);
            for (int idx = 0; idx < size() && difference; idx++) {
                difference = cumulativePeriods.get(idx).equals(((PatternPacket) obj).getCumulativePeriod(idx));
                difference &= cumulativeValues.get(idx).equals(((PatternPacket) obj).getCumulativeValues().get(idx));
                difference &= stochasticHitPattern == ((PatternPacket) obj).stochasticHitPattern;
            }
            return difference;
        }
        return false;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(patternMarker);
        for (Period period : cumulativePeriods) {
            builder.append(period.hashCode());
        }
        for (Double value : cumulativeValues) {
            builder.append(value);
        }
        builder.append(stochasticHitPattern);
        return builder.toHashCode();
    }

    @Override
    public PatternPacket clone() {
        List<Double>  clonedCumulativeValues = new ArrayList<Double>(cumulativeValues.size());
        for (Double value : cumulativeValues) {
            clonedCumulativeValues.add(new Double(value));
        }
        List<Period> clonedCumulativePeriods = new ArrayList<Period>(cumulativePeriods.size());
        for (Period period : cumulativePeriods) {
            clonedCumulativePeriods.add(new Period(period));
        }
        return new PatternPacket(patternMarker, clonedCumulativeValues, clonedCumulativePeriods, stochasticHitPattern);
    }
}
