package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.runOff.RunOffPatternUtils;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum PayoutPatternBase {


    RESERVE_DEVELOPMENT {
        @Override
        public PatternPacket patternAccordingToPayoutBase(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            final NavigableMap<DateTime, Double> theMap = originalPattern.absolutePattern(startDateForPayouts, true);
            final SortedMap<DateTime, Double> theMapAfterUpdate = theMap.tailMap(updateDate);
            double normalisingFactor = DateTimeUtilities.sumDateTimeDoubleMapByDateRange(theMapAfterUpdate, updateDate.minusDays(1), theMap.lastKey().plusDays(1));

            final List<Double> cumulativeValues = new ArrayList<Double>();
            final List<Period> cumulativePeriods = new ArrayList<Period>();

            cumulativeValues.add(0d);
            cumulativePeriods.add(new Period(0));

            if (normalisingFactor == 0) {
                cumulativeValues.add(1d);
                cumulativePeriods.add(new Period(startDateForPayouts, updateDate));
            } else {
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
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            if (firstActualPaidDate != null) {
                throw new SimulationException(" The reserve pattern has been provided with an actual payment date. This is not allowed.");
            }
            return PERIOD_START_DATE.startDateForPayouts(claimRoot, contractPeriodStart, firstActualPaidDate);
        }
    },

    CLAIM_OCCURANCE_DATE {
        @Override
        public PatternPacket patternAccordingToPayoutBase(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            return PatternUtils.adjustForNoClaimUpdates(originalPattern, startDateForPayouts, updateDate);
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
    }, PERIOD_START_DATE {
        @Override
        public PatternPacket patternAccordingToPayoutBase(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate) {
            return PatternUtils.adjustForNoClaimUpdates(originalPattern, startDateForPayouts, updateDate);
        }

        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            return contractPeriodStart;
        }
    };

    //
    public abstract PatternPacket patternAccordingToPayoutBase(PatternPacket originalPattern, DateTime startDateForPayouts, DateTime updateDate);

    public abstract DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate);

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
