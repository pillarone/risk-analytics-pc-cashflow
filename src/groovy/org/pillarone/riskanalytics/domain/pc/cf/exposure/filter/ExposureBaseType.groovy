package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ExposureBaseType extends AbstractParameterObjectClassifier {


    public static final ExposureBaseType ABSOLUTE = new ExposureBaseType("absolute", "ABSOLUTE", [:])
    public static final ExposureBaseType PREMIUM = new ExposureBaseType("premium", "PREMIUM", [
            'underwritingInfo':new ComboBoxTableMultiDimensionalParameter([], ['Underwriting Info'], IUnderwritingInfoMarker)])
    public static final ExposureBaseType EXPOSURE = new ExposureBaseType("exposure", "EXPOSURE", [
            'underwritingInfo':new ComboBoxTableMultiDimensionalParameter([], ['Underwriting Info'], IUnderwritingInfoMarker)])

    public static final all = [ABSOLUTE, PREMIUM, EXPOSURE]

    protected static Map types = [:]
    static {
        ExposureBaseType.all.each {
            ExposureBaseType.types[it.toString()] = it
        }
    }

    private ExposureBaseType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ExposureBaseType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return ExposureBaseType.getStrategy(this, parameters)
    }

    static IExposureBaseStrategy getDefault() {
        return new AbsoluteBaseStrategy()
    }

    static IExposureBaseStrategy getStrategy(ExposureBaseType type, Map parameters) {
        switch (type) {
            case ExposureBaseType.ABSOLUTE:
                return new AbsoluteBaseStrategy()
            case ExposureBaseType.PREMIUM:
                return new PremiumBaseStrategy(
                        underwritingInfo: (ComboBoxTableMultiDimensionalParameter) parameters['underwritingInfo'])
            case ExposureBaseType.EXPOSURE:
                return new ExposureBaseStrategy(
                        underwritingInfo: (ComboBoxTableMultiDimensionalParameter) parameters['underwritingInfo'])
        }
    }
}

