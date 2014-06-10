package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates an index value for the period start date using a distribution and the strategy parameters
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): allow indices to start at or after projection date
// todo(sku): handle same stddev if index series starts at 1.1. before project start
public class StochasticIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String START_DATE = "startDate";
    public static final String DISTRIBUTION = "distribution";
    public static final String SHIFT = "shift";

    private DateTime startDate;
    private DateTime lastDayOfPeriod;
    private RandomDistribution distribution;
    private IDistributionShiftStrategy shift;

    private IRandomNumberGenerator indexGenerator;
    private int preparatoryPeriods;

    private double previousPeriodFactor;

    public IParameterObjectClassifier getType() {
        return IndexStrategyType.STOCHASTIC;
    }

    private FactorsPacket factors;

    public Map getParameters() {
        Map params = new HashMap(3);
        params.put(START_DATE, startDate);
        params.put(DISTRIBUTION, distribution);
        params.put(SHIFT, shift);
        return params;
    }

    public FactorsPacket getFactors(PeriodScope periodScope, Component origin, List<EventDependenceStream> eventStreams) {
        lazyInitGenerator(periodScope);
        Double filteredSeverity = filterSeverity(origin, eventStreams);
        // note: indexGenerator.getDistribution() yields the unmodified distribution; hence this is only correct if there is no DistributionModifier
        double factor = filteredSeverity == null ? previousPeriodFactor * (1 + indexGenerator.nextValue().doubleValue() + shift.shift()) :
                previousPeriodFactor * (1 + indexGenerator.getDistribution().inverseF(filteredSeverity) + shift.shift());
        previousPeriodFactor = factor;
        factors.add(lastDayOfPeriod.plusYears(periodScope.getCurrentPeriod()), factor);
        factors.origin = origin;
        return factors;
    }

    private void lazyInitGenerator(PeriodScope periodScope) {
        if (indexGenerator == null) {
            indexGenerator = RandomNumberGeneratorFactory.getGenerator(distribution);
        }
        if (periodScope.isFirstPeriod()) {
            DateTime indexDate = new DateTime(startDate);
            previousPeriodFactor = 1d;
            factors = new FactorsPacket();
            while (indexDate.isBefore(periodScope.getCurrentPeriodStartDate())) {
                previousPeriodFactor *= 1 + indexGenerator.nextValue().doubleValue();
                factors.add(indexDate, previousPeriodFactor);
                indexDate = indexDate.plusYears(1);
            }
            lastDayOfPeriod = periodScope.getNextPeriodStartDate().minusDays(1);
            if (!(indexDate.equals(lastDayOfPeriod))) {
                factors.add(lastDayOfPeriod.minusYears(1), previousPeriodFactor * (1 + indexGenerator.nextValue().doubleValue()));
            }
        }
    }

    private static Double filterSeverity(Component filterCriteria, List<EventDependenceStream> eventStreams) {
        List<EventSeverity> filteredEventSeverities = new ArrayList<EventSeverity>();
        for (EventDependenceStream eventStream : eventStreams) {
            if (eventStream.getEventDependenceStream().containsKey(filterCriteria.getName())) {
                filteredEventSeverities.add(eventStream.getEventDependenceStream().get(filterCriteria.getName()));
            }
        }
        if (filteredEventSeverities.size() > 1) {
            throw new IllegalArgumentException("['StochasticIndexStrategy.invalidDependencies']");
        }
        return filteredEventSeverities.size() == 1 ? filteredEventSeverities.get(0).getValue() : null;
    }
}
