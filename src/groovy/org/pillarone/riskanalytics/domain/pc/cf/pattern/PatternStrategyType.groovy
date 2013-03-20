package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PatternStrategyType extends AbstractParameterObjectClassifier {

    public static final PatternStrategyType NONE = new PatternStrategyType('none', 'NONE', [:])
    public static final PatternStrategyType INCREMENTAL = new PatternStrategyType("incremental", "INCREMENTAL", [
            incrementalPattern :  new ConstrainedMultiDimensionalParameter([[0],[1d]], [PatternTableConstraints.MONTHS,INCREMENTS],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])
    public static final PatternStrategyType CUMULATIVE = new PatternStrategyType("cumulative", "CUMULATIVE", [
            cumulativePattern :  new ConstrainedMultiDimensionalParameter([[0],[1d]], [PatternTableConstraints.MONTHS,CUMULATIVE2],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])
    public static final PatternStrategyType AGE_TO_AGE = new PatternStrategyType("age-to-age", "AGE_TO_AGE", [
            ageToAgePattern :  new ConstrainedMultiDimensionalParameter([[0],[1d]], [PatternTableConstraints.MONTHS,LINK_RATIOS],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])
    public static final PatternStrategyType HIT_PROBABILITY = new PatternStrategyType("hit probability", "HIT_PROBABILITY", [
            stochasticHitPattern  :  new ConstrainedMultiDimensionalParameter([[0],[1d]], [PatternTableConstraints.MONTHS,INCREMENTS],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])

    public static final all = [NONE, INCREMENTAL, CUMULATIVE, AGE_TO_AGE, HIT_PROBABILITY]

    public static final String INCREMENTS = "Increments";
    public static final String CUMULATIVE2 = "Cumulative";
    public static final String STOCHASTIC_HIT_PROBABILITY = "Stochastic Hit Probability";
    public static final String LINK_RATIOS = "Link ratios";

    protected static Map types = [:]
    static {
        PatternStrategyType.all.each {
            PatternStrategyType.types[it.toString()] = it
        }
    }

    private PatternStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static PatternStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IPatternStrategy getDefault() {
        return new TrivialPatternStrategy();
    }

    static IPatternStrategy getStrategy(PatternStrategyType type, Map parameters) {
        IPatternStrategy pattern;
        switch (type) {
            case PatternStrategyType.NONE:
                return new TrivialPatternStrategy()
                break
            case PatternStrategyType.INCREMENTAL:
                return new IncrementalPatternStrategy(
                        incrementalPattern : (ConstrainedMultiDimensionalParameter) parameters['incrementalPattern'])
                break;
            case PatternStrategyType.CUMULATIVE:
                return new CumulativePatternStrategy(
                        cumulativePattern : (ConstrainedMultiDimensionalParameter) parameters['cumulativePattern'])
                break
            case PatternStrategyType.AGE_TO_AGE:
                return new AgeToAgePatternStrategy(
                        ageToAgePattern : (ConstrainedMultiDimensionalParameter) parameters['ageToAgePattern'])
                break
            case PatternStrategyType.HIT_PROBABILITY:
                return new StochasticHitPatternStrategy(
                        stochasticHitPattern : (ConstrainedMultiDimensionalParameter) parameters['stochasticHitPattern'])

        }
    }
}
