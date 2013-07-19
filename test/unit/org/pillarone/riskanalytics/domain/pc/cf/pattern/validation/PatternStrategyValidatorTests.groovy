package org.pillarone.riskanalytics.domain.pc.cf.pattern.validation

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPatternStrategy
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints

class PatternStrategyValidatorTests extends GroovyTestCase {

    @Override
    protected void setUp() throws Exception {
        ConstraintsFactory.registerConstraint(new PatternTableConstraints())
    }

    void testIncremental_ZeroRows() {
        def params = new ConstrainedMultiDimensionalParameter([[],[]], [PatternTableConstraints.MONTHS, PatternStrategyType.INCREMENTS],
                ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER))
        IPatternStrategy strategy = PatternStrategyType.getStrategy(PatternStrategyType.INCREMENTAL, [incrementalPattern:params])
        List<ParameterValidation> validationResult = new PatternStrategyValidator().validate([new ParameterObjectParameterHolder('path', 0, strategy)])
        assert 1 == validationResult.size()
        assert 'incremental.pattern.error.incremental.values.empty' == validationResult[0].msg
    }
}
