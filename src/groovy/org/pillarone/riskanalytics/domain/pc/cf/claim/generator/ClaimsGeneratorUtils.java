package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IPerilMarker;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.DependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
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

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, IRandomNumberGenerator claimSizeGenerator,
                                                 IRandomNumberGenerator dateGenerator, int claimNumber,
                                                 ClaimType claimType, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        List<EventPacket> events = new ArrayList<EventPacket>();
        if (claimType.equals(ClaimType.EVENT) || claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            events = generateEvents(claimNumber, periodScope);
        }
        for (int i = 0; i < claimNumber; i++) {
            double fractionOfPeriod = (Double) dateGenerator.nextValue();
            DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, fractionOfPeriod);
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = occurrenceDate;
            baseClaims.add(new ClaimRoot((Double) claimSizeGenerator.nextValue() * -severityScaleFactor, claimType,
                    exposureStartDate, occurrenceDate, events.size() == 0 ? null : events.get(i)));
        }
        return baseClaims;
    }

    public static List<ClaimRoot> generateClaims(double severityScaleFactor, RandomDistribution distribution, DistributionModified modifier,
                                                 ClaimType claimType, PeriodScope periodScope) {
        IRandomNumberGenerator claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
        IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
        return generateClaims(severityScaleFactor, claimSizeGenerator, dateGenerator, 1, claimType, periodScope);
    }

    public static List<ClaimRoot> calculateClaims(double severityScaleFactor, Distribution distribution,
                                                  IRandomNumberGenerator dateGenerator, ClaimType claimType,
                                                  PeriodScope periodScope, List<Double> probabilities,
                                                  List<EventPacket> events, double shift) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        if (!(claimType.equals(ClaimType.EVENT) || claimType.equals(ClaimType.AGGREGATED_EVENT))) {
            events = new ArrayList<EventPacket>();
        }
        for (int i = 0; i < probabilities.size(); i++) {
            double fractionOfPeriod = (Double) dateGenerator.nextValue();
            DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, fractionOfPeriod);
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = occurrenceDate;
            baseClaims.add(new ClaimRoot((distribution.inverseF(probabilities.get(i)) + shift) * -severityScaleFactor, claimType,
                    exposureStartDate, occurrenceDate, events.size() == 0 ? null : events.get(i)));
        }
        return baseClaims;
    }

    public static List<Double> filterProbabilities(List<DependenceStream> streams, IPerilMarker filterCriteria) {
        List<Double> probabilities = new ArrayList<Double>();
        for (DependenceStream stream : streams) {
            if (stream.getDependenceStream().containsKey(filterCriteria.getNormalizedName())) {
                probabilities.add((Double) stream.getDependenceStream().get(filterCriteria.getNormalizedName()));
            }
        }
        if (probabilities.size() > 1) {
            throw new IllegalArgumentException("['ClaimsGenerator.attritionalClaims','" + filterCriteria.getNormalizedName() + "']");
        }
        return probabilities;
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

    public static RandomDistribution extractDistribution(List<SystematicFrequencyPacket> distributions, IPerilMarker filterCriteria) {
        RandomDistribution filteredDistribution = new RandomDistribution();
        int count = 0;
        for (SystematicFrequencyPacket distribution : distributions) {
            if (distribution.getTargets().contains(filterCriteria.getNormalizedName())) {
                filteredDistribution = distribution.getFrequencyDistribution();
                count++;
            }
        }
        if (count > 1) {
            throw new IllegalArgumentException("['ClaimsGenerator.IllegalDependencies','"+ filterCriteria.getNormalizedName() + "']");
        }
        return filteredDistribution;
    }

}
