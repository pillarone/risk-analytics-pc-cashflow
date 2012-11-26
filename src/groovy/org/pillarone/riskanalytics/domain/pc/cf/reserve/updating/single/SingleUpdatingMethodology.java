package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IUpdatingPatternMarker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SingleUpdatingMethodology extends AbstractParameterObject implements ISingleUpdatingMethodologyStrategy {

    ConstrainedString updatingPattern = new ConstrainedString(IUpdatingPatternMarker.class, "");
    SingleUpdatingMethod methodology = SingleUpdatingMethod.ORIGINAL_ULTIMATE;

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("updatingPattern", updatingPattern);
        parameters.put("methodology", methodology);
        return parameters;
    }

    public IParameterObjectClassifier getType() {
        return SingleUpdatingMethodologyStrategyType.SINGLE;
    }
}
