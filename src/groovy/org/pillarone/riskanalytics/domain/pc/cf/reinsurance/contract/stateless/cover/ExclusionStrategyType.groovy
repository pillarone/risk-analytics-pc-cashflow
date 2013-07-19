package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

public class ExclusionStrategyType extends AbstractParameterObjectClassifier {

    public static final ExclusionStrategyType NONE = new ExclusionStrategyType('None', 'NONE', [:])
    public static final ExclusionStrategyType SELECTED = new ExclusionStrategyType('selected', 'SELECTED',
            ['grossClaims': new ComboBoxTableMultiDimensionalParameter([],
                    ['Covered gross claims'], IPerilMarker.class),
            ]
    )

    public static final all = [
            NONE,
            SELECTED,
    ]

    protected static Map types = [:]
    static {
        ExclusionStrategyType.all.each {
            ExclusionStrategyType.types[it.toString()] = it
        }
    }

    private ExclusionStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static ExclusionStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IExclusionCoverStrategy getDefault() {
        return new NoneExclusionStrategy()
    }

    static IExclusionCoverStrategy getStrategy(ExclusionStrategyType type, Map parameters) {
        switch (type) {
            case NONE:
                return new NoneExclusionStrategy();
            case SELECTED:
                return new ExclusionCoverStrategy(
                        grossClaims: (ComboBoxTableMultiDimensionalParameter) parameters['grossClaims'],
                )
            default :
                throw new IllegalArgumentException("Unknown cover strategy in " + this.toString());
        }
    }
}