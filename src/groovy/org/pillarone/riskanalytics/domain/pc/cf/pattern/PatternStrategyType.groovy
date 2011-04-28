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
            cumulativePattern :  new ConstrainedMultiDimensionalParameter([[0],[1d]], [PatternTableConstraints.MONTHS,CUMULATED],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])

    public static final all = [NONE, INCREMENTAL, CUMULATIVE]

    public static final String INCREMENTS = "Increments";
    public static final String CUMULATED = "Cumulated";

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
                pattern = new TrivialPatternStrategy()
                break
            case PatternStrategyType.INCREMENTAL:
                pattern = new IncrementalPatternStrategy(
                        incrementalPattern : (ConstrainedMultiDimensionalParameter) parameters['incrementalPattern'])
                break;
            case PatternStrategyType.CUMULATIVE:
                pattern = new CumulativePatternStrategy(
                        cumulativePattern : (ConstrainedMultiDimensionalParameter) parameters['cumulativePattern'])
                break
        }
        return pattern
    }
}
