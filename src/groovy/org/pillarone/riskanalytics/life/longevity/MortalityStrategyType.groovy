package org.pillarone.riskanalytics.life.longevity

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * author simon.parten @ art-allianz . com
 */
public class MortalityStrategyType extends AbstractParameterObjectClassifier {

    public static final String MORTALITY_RATES = "mortalityRates"
    public static final String HISTORIC_MORTALITY_RATES = "historicMortalityRates"
    public static final String MORTALITY_RATE2010 = "mortalityRate2010"
    public static final String ACTUAL_MORTALITY_RATE = "actualMortalityRate"

    public static final MortalityStrategyType D_LONGEVITY = new MortalityStrategyType("D Mortality", "D_MORTALITY",
            [
                    MORTALITY_RATES : new    ConstrainedMultiDimensionalParameter(
                    MortalityRatesConstraints.initialList, MortalityRatesConstraints.columnnHeaders, new MortalityRatesConstraints()),

                    HISTORIC_MORTALITY_RATES  : new    ConstrainedMultiDimensionalParameter(
                    HistoricMortalityRatesConstraints.initialList()  , HistoricMortalityRatesConstraints.columnnHeaders, new HistoricMortalityRatesConstraints()),

                    MORTALITY_RATE2010  : new    ConstrainedMultiDimensionalParameter(
                    MortalityRatesConstraints.initialList , MortalityRatesConstraints.columnnHeaders, new MortalityRatesConstraints()),

                    ACTUAL_MORTALITY_RATE  : new    ConstrainedMultiDimensionalParameter(
                    MortalityRatesConstraints.initialList , MortalityRatesConstraints.columnnHeaders, new MortalityRatesConstraints()),
            ]
    );

    public static final all=[
            D_LONGEVITY,
    ]

    protected static Map types = [:]
    static {
        MortalityStrategyType.all.each {
            MortalityStrategyType.types[it.toString()] = it
        }
    }

    private MortalityStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static MortalityStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IMortalityStrategy getDefault() {
        return new DLongevityMortality()
    }

    static IMortalityStrategy getStrategy(MortalityStrategyType type, Map parameters) {
        switch (type) {
            case MortalityStrategyType.D_LONGEVITY:
                return new DLongevityMortality(
                        (ConstrainedMultiDimensionalParameter) parameters.get(MORTALITY_RATES),
                                (ConstrainedMultiDimensionalParameter)   parameters.get(HISTORIC_MORTALITY_RATES),
                                (ConstrainedMultiDimensionalParameter)   parameters.get(MORTALITY_RATE2010),
                                (ConstrainedMultiDimensionalParameter)   parameters.get(ACTUAL_MORTALITY_RATE),
                )
            default: throw new NotImplementedException(type.toString() + " is not implemented")
        }
    }


}
