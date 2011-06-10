package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.utils.math.copula.IPerilMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilsCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter perils;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.PERILS;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("perils", perils);
        return parameters;
    }

    List<IPerilMarker> getCoveredPerils() {
        return (List<IPerilMarker>) perils.getValuesAsObjects();
    }
}
