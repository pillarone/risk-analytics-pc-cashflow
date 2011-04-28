package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexStrategyType extends AbstractParameterObjectClassifier {

    public static final IndexStrategyType TRIVIAL = new IndexStrategyType(
            'trivial', 'TRIVIAL', [:])
    public static final IndexStrategyType DETERMINISTICANNUALCHANGE = new IndexStrategyType(
            'deterministic annual change', 'DETERMINISTICANNUALCHANGE',
            [indices: new ConstrainedMultiDimensionalParameter([[],[]],
                    [AnnualIndexTableConstraints.DATE, AnnualIndexTableConstraints.CHANGE],
                    ConstraintsFactory.getConstraints(AnnualIndexTableConstraints.IDENTIFIER))])
    public static final IndexStrategyType STOCHASTIC = new IndexStrategyType("stochastic", "STOCHASTIC", [
            startDate: new DateTime(2011,1,1,0,0,0,0), mean: 0.0, stDev: 0.2])

    public static final all = [TRIVIAL, DETERMINISTICANNUALCHANGE, STOCHASTIC]

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
            case IndexStrategyType.TRIVIAL:
                indexStrategy = new TrivialIndexStrategy()
                break
            case IndexStrategyType.DETERMINISTICANNUALCHANGE:
                indexStrategy = new DeterministicAnnualChangeIndexStrategy(
                        indices : (ConstrainedMultiDimensionalParameter) parameters['indices'])
                break;
            case IndexStrategyType.STOCHASTIC:
                indexStrategy = new StochasticIndexStrategy(
                        startDate : (DateTime) parameters[StochasticIndexStrategy.START_DATE],
                        mean : (Double) parameters[StochasticIndexStrategy.MEAN],
                        stDev : (Double) parameters[StochasticIndexStrategy.STDEV],
                )
        }
        return indexStrategy
    }
}
