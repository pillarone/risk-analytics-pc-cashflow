package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter;

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PremiumBaseStrategy extends RelativeBaseStrategy  {

    public PremiumBaseStrategy(final ComboBoxTableMultiDimensionalParameter underwritingInfo) {
        super(underwritingInfo);
    }

    public IParameterObjectClassifier getType() {
        return ExposureBaseType.PREMIUM;
    }

    public ExposureBase exposureBase() {
        return ExposureBase.PREMIUM_WRITTEN;
    }
}
