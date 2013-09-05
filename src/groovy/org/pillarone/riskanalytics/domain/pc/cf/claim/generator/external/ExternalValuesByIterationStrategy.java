package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExternalValuesByIterationStrategy extends AbstractExternalValuesByIterationStrategy {

    static Log LOG = LogFactory.getLog(ExternalValuesByIterationStrategy.class);

    private ConstrainedMultiDimensionalParameter valueTable = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[], []]"), Arrays.asList("iteration", "value"),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));
    private PeriodApplication usage = PeriodApplication.FIRSTPERIOD;

    public IParameterObjectClassifier getType() {
        return ExternalValuesType.BY_ITERATION;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("valueTable", valueTable);
        parameters.put("usage", usage);
        return parameters;
    }

    @Override
    public ConstrainedMultiDimensionalParameter table() {
        return valueTable;
    }

    @Override
    public PeriodApplication usage() {
        return usage;
    }
}
