package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationBasedOn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public class XLIndexedBoundaryIndexStrategy extends AbstractParameterObject implements IBoundaryIndexStrategy  {

    public XLBoundaryIndexApplication getIndexedValues() {
        return indexedValues;
    }

    public ConstrainedMultiDimensionalParameter getIndex() {
        return index;
    }

    private ConstrainedMultiDimensionalParameter index;
    private XLBoundaryIndexApplication indexedValues;

    @Override
    public IParameterObjectClassifier getType() {
        return XLBoundaryIndexType.INDEXED;
    }

    @Override
    public Map getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("index", index);
        params.put("indexedValues", indexedValues);
        return params;
    }
}
