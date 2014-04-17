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
public class ResourceExternalValuesByIterationAndDateStrategy extends AbstractExternalValuesByIterationAndDateStrategy {

    static Log LOG = LogFactory.getLog(ResourceExternalValuesByIterationAndDateStrategy.class);

    private ResourceHolder<ExternalValuesExtendedResource> referencedIterationDateData = new ResourceHolder<ExternalValuesExtendedResource>(ExternalValuesExtendedResource.class, "externalValuesExtendedResource", null);

    public IParameterObjectClassifier getType() {
        return ExternalValuesType.BY_ITERATION_AND_DATE_RESOURCE;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("referencedIterationDateData", referencedIterationDateData);
        return parameters;
    }

    @Override
    public ConstrainedMultiDimensionalParameter table() {
        return referencedIterationDateData.getResource().getParmValueTable();
    }
}
