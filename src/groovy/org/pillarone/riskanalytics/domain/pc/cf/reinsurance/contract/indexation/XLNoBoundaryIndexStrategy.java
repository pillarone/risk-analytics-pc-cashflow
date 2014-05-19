package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public class XLNoBoundaryIndexStrategy extends AbstractParameterObject implements IBoundaryIndexStrategy  {
    @Override
    public IParameterObjectClassifier getType() {
        return XLBoundaryIndexType.NONE;
    }

    @Override
    public Map getParameters() {
        return Collections.emptyMap();
    }

    public XLBoundaryIndexApplication getIndexedValues() {
        return XLBoundaryIndexApplication.NONE;
    }

    @Override
    public ConstrainedMultiDimensionalParameter getIndex() {
        return null;
    }
}
