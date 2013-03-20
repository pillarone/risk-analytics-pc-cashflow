package org.pillarone.riskanalytics.domain.pc.cf.pattern.runOff;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * author simon.parten @ art-allianz . com
 */
public class RunOffPatternUtils {

    private RunOffPatternUtils() {
        throw new AssertionError("Don't instantiate this class");
    }

    /**
     * This method is a bit of a beastie, although I think it makes sense to keep it together.
     * It attempts to rescale a base pattern against an arbitrary date. Example pattern fed in through the two lists
     * (which must be of the same length and correctly ordered;
     * <p/>
     * 01.01.2010 - 0%
     * 01.06.2010 - 50%
     * 01.01.2011 - 80%
     * 01.01.2012 - 100%
     * <p/>
     * What answers do we want?
     * <p/>
     * If the update date is before or equal simulation start, (for example 31.12.2009 ) then we want the identity function.
     * Return the same pattern.
     * <p/>
     * If the update date falls inside the pattern, for example in on 01.09.2010 there are two possibilities;
     * 1) We want to ignore all entries before that date and add the prior vales to next available entry in the pattern. So return
     * <p/>
     * 01.01.2010 - 0%
     * 01.06.2010 - 0%
     * 01.01.2011 - 80%
     * 01.01.2012 - 1%
     * <p/>
     * 2) We want to rescale the remainder of the pattern to payout the pattern proportionally according to what's left in the tail.
     *
     * If we interpolate to this date, we would expect to have already paid out 60 % of the pattern. The rescaled cumulative pattern is;
     *
     * 01.01.2010 - 0%
     * 01.09.2010 - 60%
     * 01.01.2011 - 80%
     * 01.01.2012 - 100%
     *
     * we rescale (the incremental amounts, because we know nothing is actally paid to the interplolation date) to be ;
     *
     * <p/>
     * 01.09.2010 - 0%
     * 01.01.2011 - 50%
     * 01.01.2012 - 10%
     * <p/>
     *
     * The final possibility is that the update date is after the end of the pattern. Simply return 100% on the update date.
     *
     * @param simulationStart         the start date to apply the pattern from
     * @param updateDate              the date to rescale the pattern from
     * @param patternLength           number of entries in the pattern
     * @param months                  months in the pattern, must correspond to ratio list
     * @param ratios                  pattern values - must be the same length and correctly ordered as the months. Cumulative values.
     * @param rescaleFromUpdateDate Do we want to interpolate to the update date and rescale the pattern?
     * @return
     */
    public static TreeMap<DateTime, Double> rescaleRunOffPattern(
            DateTime simulationStart,
            DateTime updateDate,
            int patternLength,
            List<Number> months,
            List<Number> ratios,
            boolean rescaleFromUpdateDate
    ) {
        TreeMap<DateTime, Double> runOffPatternByDate;
        if (months.size() == 0 || ratios.size() == 0 || months.size() != ratios.size() || patternLength != ratios.size()) {
            throw new IllegalArgumentException("Run off pattern length must be greater than zero and must be of the same length.");
        }

//        Firstly create a source of truth. This will be the pattern in an absolute unmodified form for each entry in the pattern.
        double priorNumber = 0;
        double priorMonths = 0;
        TreeMap<DateTime, Double> tempMap = new TreeMap<DateTime, Double>();
        for (int j = 0; j < patternLength; j++) {
            Number month = months.get(j);
            Number ratio = ratios.get(j);
            if (!(month.doubleValue() >= priorMonths || ratio.doubleValue() >= priorNumber)) {
                throw new IllegalArgumentException("The runOff pattern does not appear to be in increasing month order. For technical reasons this is not currently allowed. ");
            }
            DateTime ratioDate = simulationStart.plusMonths(month.intValue());
            tempMap.put(ratioDate, ratio.doubleValue());
        }

//       First find the first entry after the update date. Ultimately we are not interested in the entries before it.

        DateTime firstDateAfterUpdateDate = tempMap.higherKey(updateDate);

        if (firstDateAfterUpdateDate == null) {
            runOffPatternByDate = new TreeMap<DateTime, Double>();
//            The pattern is exhausted before the update so assume value 1 on that day as we do want a complete pattern.
            runOffPatternByDate.put(updateDate, 1d);
            return runOffPatternByDate;
        } else if (firstDateAfterUpdateDate.equals(simulationStart)) {
            //        If the start of the interpolation is after the beginning of the pattern return the raw pattern.
            return tempMap;
        } else {
            TreeMap<DateTime, Double> allBeforeIncludingUpdateDate;
            allBeforeIncludingUpdateDate = new TreeMap<DateTime, Double>(tempMap.headMap(firstDateAfterUpdateDate, true));

            double valueAtUpdateDate = 0;
//            Get the value at the update date. If we want to simply payout everything which should have already been paid
//            this value is zero. If we want to rescale the entire pattern to recognise a payment delay leave as zero.
            if (rescaleFromUpdateDate) {
                valueAtUpdateDate = dateRatioInterpolation(simulationStart, null, updateDate.minusDays(1), allBeforeIncludingUpdateDate);
                tempMap.put(updateDate, valueAtUpdateDate);
            }

//            This map has all entries after the update date, with the value up to that date interpolated from the pattern.
            TreeMap<DateTime, Double> allEntriesAfterUpdateDate = new TreeMap<DateTime, Double>(tempMap.tailMap(updateDate, true));

//            Fill the rest of the map with the other entries. At this point the map values may not sum to 1.
            double priorRunOffValue = valueAtUpdateDate;
            TreeMap<DateTime, Double> incrementalValues = new TreeMap<DateTime, Double>();
            for (Map.Entry<DateTime, Double> entry : allEntriesAfterUpdateDate.entrySet()) {
                double incrementalValue = entry.getValue() - priorRunOffValue;
                priorRunOffValue += incrementalValue;
                incrementalValues.put(entry.getKey(), incrementalValue);
            }

//        Sum incremental values
            double normalisingValue = 0;
            for (double value : incrementalValues.values()) {
                normalisingValue += value;
            }
//            If there is no interpolation then there should be no need to normalise the remainder of the pattern.
            if (!rescaleFromUpdateDate && normalisingValue != 1) {
                throw new IllegalArgumentException("If we are not interpolating to the update date, then the normalised value of" +
                        "the adjusted pattern should already be 1... please report to development");
            }
            runOffPatternByDate = new TreeMap<DateTime, Double>();
//            Normalise the map.
            double priorValue = 0;
            for (Map.Entry<DateTime, Double> entry : incrementalValues.entrySet()) {
                double normalisedValue = entry.getValue() / normalisingValue;
                priorValue += normalisedValue;
                runOffPatternByDate.put(entry.getKey(), priorValue);
            }
        }
        return runOffPatternByDate;
    }

    @Deprecated
    public static double dateRatioInterpolation(DateTime simulationStartDate,
                                                DateTime interpolationStartDate,
                                                DateTime interpolationReportDate,
                                                TreeMap<DateTime, Double> dateRatioMap
                                                ) {

        return dateRatioInterpolation(
                simulationStartDate,
                interpolationStartDate,
                interpolationReportDate,
                dateRatioMap,
                DateTimeUtilities.Days360.EU);
    }


        /**
        * This takes a pattern and interpolates from the start date to the update date, returning what the interpolated value of
        * the pattern supplied on that date. Should deal tail cases gracefully.
        *
        * @param simulationStartDate     - the base date of the pattern.
        * @param interpolationStartDate  - ignored, originall intended for a different function so left in at the moment.
        * @param interpolationReportDate - the date to interpolate to, commonly the so called, ' update date '.
        * @param dateRatioMap            - the pattern of interest
        * @return a double which is the interpolated value of the pattern on the interpolationReportDate.
        */
    public static double dateRatioInterpolation(DateTime simulationStartDate,
                                                DateTime interpolationStartDate,
                                                DateTime interpolationReportDate,
                                                TreeMap<DateTime, Double> dateRatioMap,
                                                DateTimeUtilities.Days360 days360) {
        if(interpolationStartDate == null ) {
            interpolationStartDate = simulationStartDate;
        }

        Map.Entry<DateTime, Double> floorPatternEntry = dateRatioMap.floorEntry(interpolationReportDate);
        Map.Entry<DateTime, Double> ceilingPatternEntry = dateRatioMap.ceilingEntry(interpolationReportDate);
        if (dateRatioMap.containsKey(interpolationReportDate.plusMillis(1))) {
            return dateRatioMap.get(interpolationReportDate.plusMillis(1));
        }
        if (dateRatioMap.containsKey(interpolationReportDate)) {
            return dateRatioMap.get(interpolationReportDate);
        }
        if (ceilingPatternEntry == null) {
            return 1d;
        }
        if (floorPatternEntry == null) {
//          Return incremental ratio in period... we have no floor entry so it must be simulation start.

            DateTime patternDate = dateRatioMap.ceilingEntry(interpolationReportDate).getKey();
            if (patternDate == null) {
                throw new IllegalArgumentException("No data in pattern map!!!");
            }

            double daysInReportingPeriod = days360.days360(interpolationStartDate, interpolationReportDate.plusMillis(1));
            double daysInPatternPeriod = days360.days360(interpolationStartDate, patternDate);

            double ratio = daysInReportingPeriod / daysInPatternPeriod;
            return ceilingPatternEntry.getValue() * ratio;
        }

        DateTime ceilingPatternDate = dateRatioMap.ceilingEntry(interpolationReportDate).getKey();
        DateTime floorPatternDate = dateRatioMap.floorEntry(interpolationReportDate).getKey();
        int daysBetweenReportAndPattern = days360.days360(floorPatternDate, interpolationReportDate.plusMillis(1));
        int daysBetweenPatternDates = days360.days360(floorPatternDate, ceilingPatternDate);
//        Cast to double so that the divison below returns a double.
        double doubleDaysToReport = daysBetweenReportAndPattern;
        double doubleDaysBetweenEntries = daysBetweenPatternDates;
        double ceilingPatternValue = ceilingPatternEntry.getValue();
        double floorPatternValue = floorPatternEntry.getValue();

        return floorPatternValue + ((ceilingPatternValue - floorPatternValue) * (doubleDaysToReport / doubleDaysBetweenEntries));
    }
}
