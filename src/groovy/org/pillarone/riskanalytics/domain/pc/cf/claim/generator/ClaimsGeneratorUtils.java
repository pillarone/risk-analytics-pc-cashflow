package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.DefaultContractBase;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.LossesOccurringContractBase;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionUtils;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;
import org.pillarone.riskanalytics.domain.utils.math.randomnumber.UniformIntList;
import umontreal.iro.lecuyer.probdist.Distribution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorUtils {

    /**
     * @param severityScaleFactor
     * @param claimSizeGenerator
     * @param dateGenerator
     * @param claimNumber
     * @param claimType
     * @param periodScope
     * @return
     * @deprecated currently kept to ensure reproducible results for the GIRA model
     */
    @Deprecated
    public static List<ClaimRoot> generateClaims(double severityScaleFactor, IRandomNumberGenerator claimSizeGenerator,
                                                 IRandomNumberGenerator dateGenerator, int claimNumber,
                                                 ClaimType claimType, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        List<EventPacket> events = generateEventsOrNull(claimType, claimNumber, periodScope, dateGenerator);
        for (int i = 0; i < claimNumber; i++) {
            DateTime occurrenceDate = events == null ?
                    DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue()) : events.get(i).getDate();
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = periodScope.getPeriodCounter().getCurrentPeriodStart();
            EventPacket event = events == null ? null : events.get(i);
            baseClaims.add(new ClaimRoot((Double) claimSizeGenerator.nextValue() * -severityScaleFactor, claimType,
                    exposureStartDate, occurrenceDate, event));
        }
        return baseClaims;
    }

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, List<Factors> severityFactors,
                                                 IRandomNumberGenerator claimSizeGenerator,
                                                 IRandomNumberGenerator dateGenerator, int claimNumber,
                                                 ClaimType claimType, PeriodScope periodScope,
                                                 IReinsuranceContractBaseStrategy contractBase) {
        if (contractBase instanceof DefaultContractBase) {
            // temporarily added to keep reproducibility of results
            return generateClaims(severityScaleFactor, claimSizeGenerator, dateGenerator, claimNumber, claimType, periodScope);
        }
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        List<EventPacket> events = generateEventsOrNull(claimType, claimNumber, periodScope, dateGenerator);
        for (int i = 0; i < claimNumber; i++) {
            EventPacket event = events == null ? null : events.get(i);
            // todo(sku): replace with information from underwriting
            DateTime inceptionDate = contractBase.inceptionDate(periodScope, dateGenerator);
            int splittedClaimNumber = contractBase.splittedClaimsNumber();
            double ultimate = (Double) claimSizeGenerator.nextValue() * -severityScaleFactor;
            double splittedUltimate = ultimate / (double) splittedClaimNumber;
            for (int j = 0; j < splittedClaimNumber; j++) {
                DateTime occurrenceDate = contractBase.occurrenceDate(inceptionDate, dateGenerator, periodScope, event);
                double scaleFactor = IndexUtils.aggregateFactor(severityFactors, occurrenceDate, periodScope.getPeriodCounter(), inceptionDate);
                baseClaims.add(new ClaimRoot(splittedUltimate * scaleFactor, claimType,
                        inceptionDate, occurrenceDate, event));
            }
        }
        return baseClaims;
    }

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, List<Factors> severityFactors,
                                                 RandomDistribution distribution, DistributionModified modifier,
                                                 ClaimType claimType, PeriodScope periodScope) {
        return generateClaims(severityScaleFactor, severityFactors, distribution, modifier, claimType, periodScope,
                new LossesOccurringContractBase());
    }

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, List<Factors> severityFactors,
                                                 RandomDistribution distribution, DistributionModified modifier, ClaimType claimType,
                                                 PeriodScope periodScope, IReinsuranceContractBaseStrategy contractBase) {
        IRandomNumberGenerator claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
        IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
        return generateClaims(severityScaleFactor, severityFactors, claimSizeGenerator, dateGenerator, 1, claimType,
                periodScope, contractBase);
    }

    public static List<ClaimRoot> calculateClaims(double severityScaleFactor, Distribution claimsSizeDistribution, ClaimType claimType,
                                                  PeriodScope periodScope, List<EventSeverity> eventSeverities,
                                                  double shift) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        for (EventSeverity eventSeverity : eventSeverities) {
            DateTime occurrenceDate = eventSeverity.getEvent().getDate();
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = periodScope.getPeriodCounter().getCurrentPeriodStart();
            EventPacket event = claimType.equals(ClaimType.EVENT) || claimType.equals(ClaimType.AGGREGATED_EVENT) ? eventSeverity.getEvent() : null;
            baseClaims.add(new ClaimRoot((claimsSizeDistribution.inverseF(eventSeverity.getValue()) + shift) * -severityScaleFactor, claimType,
                    exposureStartDate, occurrenceDate, event));
        }
        return baseClaims;
    }

    public static List<EventSeverity> filterEventSeverities(List<EventDependenceStream> eventStreams, IPerilMarker filterCriteria) {
        List<EventSeverity> filteredEventSeverities = new ArrayList<EventSeverity>();
        for (EventDependenceStream eventStream : eventStreams) {
            if (eventStream.getEventDependenceStream().containsKey(filterCriteria.getNormalizedName())) {
                filteredEventSeverities.add(eventStream.getEventDependenceStream().get(filterCriteria.getNormalizedName()));
            }
        }
        return filteredEventSeverities;
    }

    public static List<EventPacket> extractEvents(List<EventSeverity> eventSeverities) {
        List<EventPacket> events = new ArrayList<EventPacket>();
        for (EventSeverity eventSeverity : eventSeverities) {
            events.add(eventSeverity.getEvent());
        }
        return events;
    }

    public static List<Double> extractSeverities(List<EventSeverity> eventSeverities) {
        List<Double> severities = new ArrayList<Double>();
        for (EventSeverity eventSeverity : eventSeverities) {
            severities.add(eventSeverity.getValue());
        }
        return severities;
    }

    public static List<EventPacket> generateEvents(int number, PeriodScope periodScope) {
        int numberOfDaysInPeriod = Days.daysBetween(periodScope.getPeriodCounter().getCurrentPeriodStart(),
                periodScope.getPeriodCounter().getCurrentPeriodEnd()).getDays();
        List<Integer> dates = UniformIntList.getIntegers(number, 0, numberOfDaysInPeriod - 1);
        List<EventPacket> events = new ArrayList<EventPacket>(number);
        for (int i = 0; i < number; i++) {
            events.add(new EventPacket(periodScope.getPeriodCounter().getCurrentPeriodStart().plusDays(dates.get(i))));
        }
        return events;
    }

    /**
     *
     * @param claimType triggering behaviour, events are required for event claim types only
     * @param number of events to generate
     * @param periodScope used for date generation
     * @param dateGenerator
     * @return for ClaimType.EVENT or AGGREGATED_EVENT a list of length number in all other cases with event dates
     *          generated according to dateGenerator and value = 0, null for all other claim types
     */
    public static List<EventPacket> generateEventsOrNull(ClaimType claimType, int number, PeriodScope periodScope, IRandomNumberGenerator dateGenerator) {
        // dateGenerator uses fraction of period, i.e., must have states in unity interval
        if (!(claimType.equals(ClaimType.EVENT) || claimType.equals(ClaimType.AGGREGATED_EVENT))) {
            return null;
        }
        List<EventPacket> events = new ArrayList<EventPacket>(number);
        for (int i = 0; i < number; i++) {
            DateTime date = DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue());
            events.add(new EventPacket(date));
        }
        return events;
    }

    public static RandomFrequencyDistribution extractFrequencyDistribution(List<SystematicFrequencyPacket> distributions, IPerilMarker filterCriteria) {
        List<RandomFrequencyDistribution> filteredDistributions = new ArrayList<RandomFrequencyDistribution>();
        for (SystematicFrequencyPacket distribution : distributions) {
            if (distribution.getTargets().contains(filterCriteria.getNormalizedName())) {
                filteredDistributions.add(distribution.getFrequencyDistribution());
            }
        }
        if (filteredDistributions.size() == 0) return null;
        RandomFrequencyDistribution distribution = filteredDistributions.get(0);
        for (int i = 1; i < filteredDistributions.size(); i++) {
            distribution = FrequencyDistributionUtils.getSumOfDistributions(distribution, filteredDistributions.get(i));
        }
        return distribution;
    }

}
