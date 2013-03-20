package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RetrospectiveCoverAttributeStrategyType extends AbstractParameterObjectClassifier {

    public static final RetrospectiveCoverAttributeStrategyType NONE = new RetrospectiveCoverAttributeStrategyType("none", "NONE", [:])
    public static final RetrospectiveCoverAttributeStrategyType ORIGINALRESERVES = new RetrospectiveCoverAttributeStrategyType("original reserves",
        "ORIGINALRESERVES", ['reserves': new ComboBoxTableMultiDimensionalParameter([],  ['Reserve Generators'], IReserveMarker)])
    public static final RetrospectiveCoverAttributeStrategyType LEGALENTITIES = new RetrospectiveCoverAttributeStrategyType(
            'legal entities', 'LEGALENTITIES',
            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Legal Entities'], ILegalEntityMarker),
             'legalEntityCoverMode': LegalEntityCoverMode.INWARD])


    public static final all = [NONE, ORIGINALRESERVES, LEGALENTITIES]


    protected static Map types = [:]
    static {
        RetrospectiveCoverAttributeStrategyType.all.each {
            RetrospectiveCoverAttributeStrategyType.types[it.toString()] = it
        }
    }

    private RetrospectiveCoverAttributeStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static RetrospectiveCoverAttributeStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static ICoverAttributeStrategy getDefault() {
        return new NoneRetroCoverAttributeStrategy()
    }

    public static ICoverAttributeStrategy getStrategy(RetrospectiveCoverAttributeStrategyType type, Map parameters) {
        ICoverAttributeStrategy coverStrategy ;
        switch (type) {
            case RetrospectiveCoverAttributeStrategyType.NONE:
                coverStrategy = new NoneRetroCoverAttributeStrategy()
                break
            case RetrospectiveCoverAttributeStrategyType.ORIGINALRESERVES:
                coverStrategy = new ReservesFilterStrategy(
                        reserves: (ComboBoxTableMultiDimensionalParameter) parameters['reserves'])
                break
            case RetrospectiveCoverAttributeStrategyType.LEGALENTITIES:
                coverStrategy = new LegalEntityFilterStrategy(
                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'])
                break
        }
        return coverStrategy;
    }
}
