package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExternalValuesByIterationAndDateStrategy extends AbstractExternalValuesByIterationAndDateStrategy {

    static Log LOG = LogFactory.getLog(ExternalValuesByIterationAndDateStrategy.class);

    private ConstrainedMultiDimensionalParameter valueDateTable = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[], [], []]"), Arrays.asList("iteration", "date", "value"),
            ConstraintsFactory.getConstraints(IntDateTimeDoubleConstraints.IDENTIFIER));



    public IParameterObjectClassifier getType() {
        return ExternalValuesType.BY_ITERATION_AND_DATE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("valueDateTable", valueDateTable);
        return parameters;
    }

    @Override
    public ConstrainedMultiDimensionalParameter table() {
        return valueDateTable;
    }
}
