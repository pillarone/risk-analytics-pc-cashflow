package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexStrategyType extends AbstractParameterObjectClassifier {

    public static final IndexStrategyType NONE = new IndexStrategyType(
            'trivial', 'NONE', [:])
    public static final IndexStrategyType DETERMINISTICANNUALCHANGE = new IndexStrategyType(
            'deterministic annual changes', 'DETERMINISTICANNUALCHANGE',
            [changes: new ConstrainedMultiDimensionalParameter([[],[]],
                    AnnualIndexTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(AnnualIndexTableConstraints.IDENTIFIER))])
    public static final IndexStrategyType DETERMINISTICINDEXSERIES = new IndexStrategyType(
            'deterministic index series', 'DETERMINISTICINDEXSERIES',
            [indices: new ConstrainedMultiDimensionalParameter([[],[]],
                    DeterministicIndexTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(DeterministicIndexTableConstraints.IDENTIFIER))])
    public static final IndexStrategyType AGE_TO_AGE = new IndexStrategyType(
            'age-to-age', 'AGE_TO_AGE',
            [ratios: new ConstrainedMultiDimensionalParameter([[],[]],
                    LinkRatioIndexTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(LinkRatioIndexTableConstraints.IDENTIFIER))])
    public static final IndexStrategyType STOCHASTIC = new IndexStrategyType("stochastic", "STOCHASTIC", [
            startDate: new DateTime(2011,1,1,0,0,0,0),
            distribution: DistributionType.getStrategy(DistributionType.NORMAL, [mean: 0d, stDev: 1d])])
    public static final IndexStrategyType ORNSTEINUHLENBECK = new IndexStrategyType("Ornstein-Uhlenbeck", "ORNSTEINUHLENBECK", [
            startValue: 0d, a: 0d, b: 0d, sigma: 1d])

    public static final all = [NONE, DETERMINISTICANNUALCHANGE, DETERMINISTICINDEXSERIES, AGE_TO_AGE, STOCHASTIC, ORNSTEINUHLENBECK]

    protected static Map types = [:]
    static {
        IndexStrategyType.all.each {
            IndexStrategyType.types[it.toString()] = it
        }
    }

    private IndexStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static IndexStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IIndexStrategy getDefault() {
        return new TrivialIndexStrategy();
    }

    static IIndexStrategy getStrategy(IndexStrategyType type, Map parameters) {
        IIndexStrategy indexStrategy;
        switch (type) {
            case IndexStrategyType.NONE:
                return new TrivialIndexStrategy()
            case IndexStrategyType.DETERMINISTICANNUALCHANGE:
                return new DeterministicAnnualChangeIndexStrategy(
                        changes: (ConstrainedMultiDimensionalParameter) parameters['changes'])
            case IndexStrategyType.DETERMINISTICINDEXSERIES:
                return new DeterministicIndexStrategy(
                        indices : (ConstrainedMultiDimensionalParameter) parameters['indices'])
            case IndexStrategyType.AGE_TO_AGE:
                return new AgeToAgeIndexStrategy(
                        ratios : (ConstrainedMultiDimensionalParameter) parameters['ratios'])
            case IndexStrategyType.STOCHASTIC:
                return new StochasticIndexStrategy(
                        startDate : (DateTime) parameters[StochasticIndexStrategy.START_DATE],
                        distribution : (RandomDistribution) parameters[StochasticIndexStrategy.DISTRIBUTION])
            case IndexStrategyType.ORNSTEINUHLENBECK:
                return new OrnsteinUhlenbeckIndexStrategy(
                        startValue: parameters[OrnsteinUhlenbeckIndexStrategy.START_VALUE],
                        a:  parameters[OrnsteinUhlenbeckIndexStrategy.A],
                        b:  parameters[OrnsteinUhlenbeckIndexStrategy.B],
                        sigma: parameters[OrnsteinUhlenbeckIndexStrategy.SIGMA])
            default:
                throw new IllegalArgumentException("Unknown strategy " + type)
        }
    }
}
