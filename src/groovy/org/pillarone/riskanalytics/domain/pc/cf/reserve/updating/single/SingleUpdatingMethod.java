package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionParams;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Filters generated IBNR claims being processed further
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum SingleUpdatingMethod {
    /**
     * all generated IBNR claims are kept
     */
    ORIGINAL_ULTIMATE {
        @Override
        public ClaimAndRandomDraws filterIBNRClaims(List<ClaimRoot> baseClaims, DateTime updateDate, DateTime lastReportedDate,
                                                    PatternPacket updatingPattern, IPeriodCounter periodCounter) {
            return new ClaimAndRandomDraws(baseClaims);
        }
    },
    /**
     * The number kept is depending on the updating pattern using a binomial distribution and the outstanding reported share
     */
    REPORTED_BF {
        @Override
        public ClaimAndRandomDraws filterIBNRClaims(List<ClaimRoot> baseClaims, DateTime updateDate, DateTime lastReportedDate,
                                                    PatternPacket updatingPattern, IPeriodCounter periodCounter) {
            // adjusted implementation taken form BornhuetterFergusonMethodReportedFrequency.requiredSimulatedClaims()
            if (baseClaims == null || baseClaims.isEmpty()) {
                return new ClaimAndRandomDraws(new ArrayList<ClaimRoot>());
            }
            double monthsDeveloped = Days.daysBetween(periodCounter.getCurrentPeriodStart(), lastReportedDate).dividedBy(30).getDays();
            double elapsedMonths = DateTimeUtilities.deriveNumberOfMonths(periodCounter.getCurrentPeriodStart().withDayOfMonth(1), lastReportedDate);
            Integer nextReportedIndex = updatingPattern.thisOrNextPayoutIndex(elapsedMonths);
            if (nextReportedIndex == null){
                // we are after the end of the reporting pattern. There should be no further claims.
                return new ClaimAndRandomDraws(new ArrayList<ClaimRoot>());
            }
            if (nextReportedIndex == 0) {
                return new ClaimAndRandomDraws(baseClaims);
            }
            Double outstandingShare = updatingPattern.outstandingShare(monthsDeveloped);

            Double claimsToKeep = ((Integer) baseClaims.size()).doubleValue() * outstandingShare;
            Integer claimsToKeepInt = claimsToKeep.intValue();
            Double fractionalPart = claimsToKeep - claimsToKeepInt;
            IRandomNumberGenerator uniform = RandomNumberGeneratorFactory.getUniformGenerator();
            double uniformDraw = uniform.nextValue().doubleValue();
            if(uniformDraw < fractionalPart) {
                claimsToKeepInt++;
            }
            return new ClaimAndRandomDraws(baseClaims.subList(0, claimsToKeepInt), uniformDraw );
        }
    },
    /**
     * "IBNR claims" occurring after the updateDate are kept
     */
    FILTER_ON_CLAIMS_OCCURRENCE_DATE {
        @Override
        public ClaimAndRandomDraws filterIBNRClaims(List<ClaimRoot> baseClaims, DateTime updateDate, DateTime lastReportedDate,
                                                    PatternPacket updatingPattern, IPeriodCounter periodCounter) {
            List<ClaimRoot> filteredClaims = new ArrayList<ClaimRoot>();
            for (ClaimRoot claim : baseClaims) {
                if (!claim.getOccurrenceDate().isBefore(updateDate)) {
                    filteredClaims.add(claim);
                }
            }
            return new ClaimAndRandomDraws(filteredClaims);
        }
    };

    abstract public ClaimAndRandomDraws filterIBNRClaims(List<ClaimRoot> baseClaims, DateTime updateDate, DateTime lastReportedDate,
                                                         PatternPacket updatingPattern, IPeriodCounter periodCounter);

    public static class ClaimAndRandomDraws {
        private final List<ClaimRoot> updatedClaims;
        private final double uniformDraw;

        public ClaimAndRandomDraws(List<ClaimRoot> updatedClaims, double uniformDraw) {
            this.updatedClaims = updatedClaims;
            this.uniformDraw = uniformDraw;
        }

        public ClaimAndRandomDraws(List<ClaimRoot> updatedClaims) {
            this.updatedClaims = updatedClaims;
            uniformDraw = 0d;
        }

        public List<ClaimRoot> getUpdatedClaims() {
            return updatedClaims;
        }

        public double getUniformDraw() {
            return uniformDraw;
        }
    }
}
