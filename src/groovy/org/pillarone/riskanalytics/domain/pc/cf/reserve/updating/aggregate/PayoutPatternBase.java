package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum PayoutPatternBase {

    ARTISAN_1_PROXY {
        @Override
        public PatternPacket patternAccordingToPayoutBaseNoUpdates(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            return RESERVE_DEVELOPMENT.patternAccordingToPayoutBaseNoUpdates(originalPattern, startDateForPayouts,updateDate);
        }

        @Override
        public PatternPacket patternAccordingToPayoutBaseWithUpdates(PatternPacket payoutPattern, ClaimRoot claimRoot,
                                                                     TreeMap<DateTime, Double> claimPaidUpdates,
                                                                     DateTime updateDate, DateTimeUtilities.Days360 days360,
                                                                     boolean sanityChecks, DateTime contractPeriodStartDate,
                                                                     DateTime firstActualPaidDateOrNull, DateTime lastReportedDateOrNull) {

            DateTime startDateForPatterns = startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull);
            PatternPacket futurePattern = patternAccordingToPayoutBaseNoUpdates(payoutPattern, startDateForPatterns, updateDate.plusYears(1));
            double ultimate = claimRoot.getUltimate();
            NavigableMap<DateTime, Double> newPattern = Maps.newTreeMap();
            List<Period> periods = Lists.newArrayList();
            List<Double> payments = Lists.newArrayList();
            for (Map.Entry<DateTime, Double> entry : claimPaidUpdates.entrySet()) {
                double payment = entry.getValue() / ultimate;
                newPattern.put(entry.getKey(), payment);
                periods.add(new Period(startDateForPatterns, entry.getKey()));
                payments.add(payment);
            }
            double usedPattern = newPattern.floorEntry(updateDate).getValue();
            double futureMultiplier = 1 - usedPattern;
            Map<DateTime, Double> mapFuturePattern  = futurePattern.absolutePattern(startDateForPatterns, true);

            for (Map.Entry<DateTime, Double> entry : mapFuturePattern.entrySet()) {
                if(entry.getValue() == 0) continue;
                usedPattern += entry.getValue() * futureMultiplier;
                newPattern.put(entry.getKey(), usedPattern);
                periods.add(new Period(startDateForPatterns, entry.getKey().minusDays(1)));
                payments.add(usedPattern);

            }
            return new PatternPacket(payoutPattern, payments, periods);
        }

        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            return PERIOD_START_DATE.startDateForPayouts(claimRoot, contractPeriodStart, firstActualPaidDate);
        }
    },

    RESERVE_DEVELOPMENT {
        /**
         * If the payout pattern ends before the update date, the returned pattern will produce a single payout at the
         * update date. If the payout pattern
         * @param originalPattern
         * @param startDateForPayouts
         * @param updateDate
         * @return
         */
        @Override
        public PatternPacket patternAccordingToPayoutBaseNoUpdates(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            final NavigableMap<DateTime, Double> theMap = originalPattern.absolutePattern(startDateForPayouts, true);
            final SortedMap<DateTime, Double> theMapAfterUpdate = theMap.tailMap(updateDate);
            double normalisingFactor = DateTimeUtilities.sumDateTimeDoubleMapByDateRange(theMapAfterUpdate, updateDate.minusDays(1), theMap.lastKey().plusDays(1));

            final List<Double> cumulativeValues = new ArrayList<Double>();
            final List<Period> cumulativePeriods = new ArrayList<Period>();

            cumulativeValues.add(0d);
            cumulativePeriods.add(new Period(0));

            // payout everything at update date
            if (normalisingFactor == 0) {
                cumulativeValues.add(1d);
                cumulativePeriods.add(new Period(startDateForPayouts, updateDate));
            }
            else {
                double cumulativePattern = 0d;
                for (Map.Entry<DateTime, Double> entry : theMapAfterUpdate.entrySet()) {
                    double incrementalPatternEntry = entry.getValue() / normalisingFactor;
                    cumulativePattern += incrementalPatternEntry;
                    cumulativeValues.add(cumulativePattern);
                    cumulativePeriods.add(new Period(startDateForPayouts, entry.getKey()));
                }
            }

            return new PatternPacket(originalPattern, cumulativeValues, cumulativePeriods);
        }

        @Override
        public PatternPacket patternAccordingToPayoutBaseWithUpdates(PatternPacket payoutPattern, ClaimRoot claimRoot, TreeMap<DateTime, Double> claimPaidUpdates, DateTime updateDate, DateTimeUtilities.Days360 days360, boolean sanityChecks, DateTime contractPeriodStartDate, DateTime firstActualPaidDateOrNull, DateTime lastReportedDateOrNull) {
            throw new SimulationException("Reserve pattern cannot have claim updates");
        }

        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            if (firstActualPaidDate != null) {
                throw new SimulationException(" The reserve pattern has been provided with an actual payment date. This is not allowed.");
            }
            return PERIOD_START_DATE.startDateForPayouts(claimRoot, contractPeriodStart, firstActualPaidDate);
        }
    },

    CLAIM_OCCURANCE_DATE {
        @Override
        public PatternPacket patternAccordingToPayoutBaseNoUpdates(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            return PatternUtils.adjustForNoClaimUpdates(originalPattern, startDateForPayouts, updateDate);
        }

        @Override
        public PatternPacket patternAccordingToPayoutBaseWithUpdates(PatternPacket payoutPattern, ClaimRoot claimRoot,
                                                                     TreeMap<DateTime, Double> claimPaidUpdates, DateTime updateDate,
                                                                     DateTimeUtilities.Days360 days360, boolean sanityChecks,
                                                                     DateTime contractPeriodStartDate, DateTime firstActualPaidDateOrNull,
                                                                     DateTime lastReportedDateOrNull) {
            DateTime startDateForPatterns = startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull);
            return PatternUtils.adjustedPattern(payoutPattern, claimPaidUpdates, claimRoot.getUltimate(), startDateForPatterns,
                    claimRoot.getOccurrenceDate(), updateDate, lastReportedDateOrNull, days360);
        }

        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            if (firstActualPaidDate == null) {
                return claimRoot.getOccurrenceDate();
            }
            if (firstActualPaidDate.isBefore(claimRoot.getOccurrenceDate())) {
//                LOG.info("Claim generated with occurance date after first actual payment. " +
//                        "Overriding claim payments base date to be the date of the first paid claim, which is: " + DateTimeUtilities.formatDate.print(firstActualPaidDate));
                return firstActualPaidDate;
            }
            return claimRoot.getOccurrenceDate();
        }
    },

    PERIOD_START_DATE {
        @Override
        public PatternPacket patternAccordingToPayoutBaseNoUpdates(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            return PatternUtils.adjustForNoClaimUpdates(originalPattern, startDateForPayouts, updateDate);
        }

        @Override
        public PatternPacket patternAccordingToPayoutBaseWithUpdates(PatternPacket payoutPattern, ClaimRoot claimRoot,
                                                                     TreeMap<DateTime, Double> claimPaidUpdates, DateTime updateDate,
                                                                     DateTimeUtilities.Days360 days360, boolean sanityChecks,
                                                                     DateTime contractPeriodStartDate, DateTime firstActualPaidDateOrNull,
                                                                     DateTime lastReportedDateOrNull) {
            DateTime startDateForPatterns = startDateForPayouts(claimRoot, contractPeriodStartDate, firstActualPaidDateOrNull);
            return PatternUtils.adjustedPattern(payoutPattern, claimPaidUpdates, claimRoot.getUltimate(), startDateForPatterns,
                    claimRoot.getOccurrenceDate(), updateDate, lastReportedDateOrNull, days360);
        }

        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            return contractPeriodStart;
        }
    };

    //
    public abstract PatternPacket patternAccordingToPayoutBaseNoUpdates(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate);

    public abstract PatternPacket patternAccordingToPayoutBaseWithUpdates(PatternPacket payoutPattern, ClaimRoot claimRoot, TreeMap<DateTime, Double> claimPaidUpdates, DateTime updateDate,
                                                                          DateTimeUtilities.Days360 days360, boolean sanityChecks, DateTime contractPeriodStartDate, DateTime firstActualPaidDateOrNull, DateTime lastReportedDateOrNull);

    public abstract DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate);

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
