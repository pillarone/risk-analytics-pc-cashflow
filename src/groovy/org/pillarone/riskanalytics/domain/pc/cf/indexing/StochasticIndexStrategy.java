package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType;
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

    private double previousPeriodFactor = 1d;

    public IParameterObjectClassifier getType() {
        return IndexStrategyType.STOCHASTIC;
    }

    public Map getParameters() {
        Map params = new HashMap(2);
        params.put(START_DATE, startDate);
        params.put(DISTRIBUTION, distribution);
        return params;
    }

    public List<FactorsPacket> getFactors(PeriodScope periodScope, Index origin) {
        lazyInitGenerator();
        double factor = 1 + indexGenerator.nextValue().doubleValue();
        List<FactorsPacket> packets = new ArrayList<FactorsPacket>(1);
        FactorsPacket packet = new FactorsPacket(periodScope.getCurrentPeriodStartDate(), previousPeriodFactor);
        packet.origin = origin;
        packets.add(packet);
        previousPeriodFactor *= factor;
        return packets;
    }

    private void lazyInitGenerator() {
        if (indexGenerator == null) {
            indexGenerator = RandomNumberGeneratorFactory.getGenerator(distribution);
        }
    }
}
