package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoUpdatingMethodology extends AbstractParameterObject implements ISingleUpdatingMethodologyStrategy {

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public IParameterObjectClassifier getType() {
        return SingleUpdatingMethodologyStrategyType.NONE;
    }
}
