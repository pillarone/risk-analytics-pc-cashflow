package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
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
// todo(sku): use startDate param in the factor calculation, currently its ignored and time serie starts with projection start date
public class StochasticIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String START_DATE = "startDate";
    public static final String DISTRIBUTION = "distribution";

    private DateTime startDate;
    private RandomDistribution distribution;

    private IRandomNumberGenerator indexGenerator;

    private double previousPeriodFactor;

    public IParameterObjectClassifier getType() {
        return IndexStrategyType.STOCHASTIC;
    }

    private FactorsPacket factors;

    public Map getParameters() {
        Map params = new HashMap(2);
        params.put(START_DATE, startDate);
        params.put(DISTRIBUTION, distribution);
        return params;
    }

    public FactorsPacket getFactors(PeriodScope periodScope, Index origin) {
        lazyInitGenerator(periodScope);
        double factor = previousPeriodFactor * (1 + indexGenerator.nextValue().doubleValue());
        previousPeriodFactor = factor;
        factors.add(periodScope.getNextPeriodStartDate(), factor);
        factors.origin = origin;
        return factors;
    }

    private void lazyInitGenerator(PeriodScope periodScope) {
        if (periodScope.isFirstPeriod()) {
            previousPeriodFactor = 1d;
            factors = new FactorsPacket(periodScope.getCurrentPeriodStartDate(), 1);
        }
        if (indexGenerator == null) {
            indexGenerator = RandomNumberGeneratorFactory.getGenerator(distribution);
        }
    }
}
