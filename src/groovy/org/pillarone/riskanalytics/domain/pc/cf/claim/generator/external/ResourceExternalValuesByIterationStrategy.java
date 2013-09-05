package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.ResourceHolder;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ResourceExternalValuesByIterationStrategy extends AbstractExternalValuesByIterationStrategy {

    static Log LOG = LogFactory.getLog(ResourceExternalValuesByIterationStrategy.class);


    private ResourceHolder<ExternalValuesResource> referencedIterationData = new ResourceHolder<ExternalValuesResource>(ExternalValuesResource.class);


    public IParameterObjectClassifier getType() {
        return ExternalValuesType.BY_ITERATION_RESOURCE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("referencedIterationData", referencedIterationData);
        return parameters;
    }

    @Override
    public ConstrainedMultiDimensionalParameter table() {
        return referencedIterationData.getResource().getParmValueTable();
    }

    @Override
    public PeriodApplication usage() {
        return referencedIterationData.getResource().getParmUsage();
    }
}
