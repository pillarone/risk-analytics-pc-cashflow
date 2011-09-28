package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CoverAttributeStrategyType extends AbstractParameterObjectClassifier {

    public static final CoverAttributeStrategyType NONE = new CoverAttributeStrategyType("none", "NONE", [:])
    public static final CoverAttributeStrategyType ORIGINALCLAIMS = new CoverAttributeStrategyType("original claims",
        "ORIGINALCLAIMS", ['filter': FilterStrategyType.getDefault()])
    public static final CoverAttributeStrategyType CONTRACTS = new CoverAttributeStrategyType(
            'contracts', 'CONTRACTS', [
                    'contracts': new ConstrainedMultiDimensionalParameter([[], []],
                            [ReinsuranceContractBasedOn.CONTRACT, ReinsuranceContractBasedOn.BASED_ON],
                            ConstraintsFactory.getConstraints(ReinsuranceContractBasedOn.IDENTIFIER)),
                    'filter': FilterStrategyType.getDefault()])
    public static final CoverAttributeStrategyType INWARDLEGALENTITIES = new CoverAttributeStrategyType(
            'inward legal entities', 'INWARDLEGALENTITIES',
            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Covered Legal Entities'], ILegalEntityMarker),
             'activeReMode': ActiveReMode.INWARD])


    public static final all = [NONE, ORIGINALCLAIMS, CONTRACTS, INWARDLEGALENTITIES]


    protected static Map types = [:]
    static {
        CoverAttributeStrategyType.all.each {
            CoverAttributeStrategyType.types[it.toString()] = it
        }
    }

    private CoverAttributeStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static CoverAttributeStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static ICoverAttributeStrategy getDefault() {
        return new NoneCoverAttributeStrategy()
    }

    public static ICoverAttributeStrategy getStrategy(CoverAttributeStrategyType type, Map parameters) {
        ICoverAttributeStrategy coverStrategy ;
        switch (type) {
            case CoverAttributeStrategyType.NONE:
                coverStrategy = new NoneCoverAttributeStrategy()
                break
            case CoverAttributeStrategyType.ORIGINALCLAIMS:
                coverStrategy = new OriginalClaimsCoverAttributeStrategy(
                        filter: parameters['filter'])
                break
            case CoverAttributeStrategyType.CONTRACTS:
                coverStrategy = new ContractsCoverAttributeStrategy(
                        contracts: (ConstrainedMultiDimensionalParameter) parameters['contracts'],
                        filter: parameters['filter'])
                break
            case CoverAttributeStrategyType.INWARDLEGALENTITIES:
                coverStrategy = new InwardLegalEntitiesCoverAttributeStrategy(
                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'],
                        activeReMode: (ActiveReMode) parameters['activeReMode'])
                break
        }
        return coverStrategy;
    }
}
