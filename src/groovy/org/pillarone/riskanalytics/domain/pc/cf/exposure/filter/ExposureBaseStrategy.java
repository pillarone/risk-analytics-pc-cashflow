package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureBaseStrategy extends RelativeBaseStrategy  {

    public IParameterObjectClassifier getType() {
        return ExposureBaseType.EXPOSURE;
    }

    public ExposureBase exposureBase() {
        return ExposureBase.NUMBER_OF_POLICIES;
    }
}
