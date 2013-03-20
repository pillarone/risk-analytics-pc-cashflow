package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
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
 * Generates an index value according to the Ornstein-Uhlenbeck process X_t = a + b * X_(t-1) + epsilon_t, with epsilon_t ~ N(0, sigma^2)
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class OrnsteinUhlenbeckIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String START_VALUE = "startValue";
    public static final String A = "a";
    public static final String B = "b";
    public static final String SIGMA = "sigma";

    private double startValue;
    private double a;
    private double b;
    private double sigma;

    private IRandomNumberGenerator whiteNoiseGenerator;

    private double perviousX;
    private double previousIndex;
    private double previousExpX;

    public IParameterObjectClassifier getType() {
        return IndexStrategyType.ORNSTEINUHLENBECK;
    }

    private FactorsPacket factors;

    public Map getParameters() {
        Map params = new HashMap(4);
        params.put(START_VALUE, startValue);
        params.put(A, a);
        params.put(B, b);
        params.put(SIGMA, sigma);
        return params;
    }

    public FactorsPacket getFactors(PeriodScope periodScope, Component origin, List<EventDependenceStream> eventStreams) {
        lazyInitWhiteNoiseGenerator(periodScope);

        double index = previousExpX * previousIndex;
        factors.add(periodScope.getCurrentPeriodStartDate(), index);
        factors.origin = origin;

        Double filteredSeverity = filterSeverity(origin, eventStreams);
        // note: whiteNoiseGenerator.getDistribution() yields the unmodified distribution; hence this is only correct if there is no DistributionModifier
        double x = filteredSeverity == null ? a + b * perviousX + whiteNoiseGenerator.nextValue().doubleValue() :
                a + b * perviousX + whiteNoiseGenerator.getDistribution().inverseF(filteredSeverity);

        previousExpX = Math.exp(x);
        previousIndex = index;

        return factors;
    }

    private void lazyInitWhiteNoiseGenerator(PeriodScope periodScope) {
        if (whiteNoiseGenerator == null) {
            RandomDistribution distribution = DistributionType.getStrategy(DistributionType.NORMAL,
                    new HashMap<DistributionParams, Double>() {{
                        put(DistributionParams.MEAN, 0d);
                        put(DistributionParams.STDEV, sigma);}});
            whiteNoiseGenerator = RandomNumberGeneratorFactory.getGenerator(distribution);
        }
        if (periodScope.isFirstPeriod()) {
            perviousX = startValue;
            previousIndex = 100;
            previousExpX = Math.exp(previousExpX);
            factors = new FactorsPacket(periodScope.getCurrentPeriodStartDate().minusYears(1), previousIndex);
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
            throw new IllegalArgumentException("['OrnsteinUhlenbeckIndexStrategy.invalidDependencies']");
        }
        return filteredEventSeverities.size() == 1 ? filteredEventSeverities.get(0).getValue() : null;
    }
}
