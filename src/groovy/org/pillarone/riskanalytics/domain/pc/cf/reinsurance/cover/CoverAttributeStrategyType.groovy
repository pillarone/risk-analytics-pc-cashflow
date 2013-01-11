package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.apache.commons.lang.NotImplementedException
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
    public static final CoverAttributeStrategyType LEGALENTITIES = new CoverAttributeStrategyType(
            'legal entities', 'LEGALENTITIES',
            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Legal Entities'], ILegalEntityMarker),
             'legalEntityCoverMode': LegalEntityCoverMode.INWARD])
    public static final CoverAttributeStrategyType MATRIX = new CoverAttributeStrategyType(
            'matrix', 'MATRIX',
            ['flexibleCover': new ConstrainedMultiDimensionalParameter([[], [], [], [], [], []],
                    [CoverMap.CONTRACT_NET_OF, CoverMap.CONTRACT_CEDED_OF, CoverMap.LEGAL_ENTITY,
                     CoverMap.SEGMENTS, CoverMap.GENERATORS, CoverMap.LOSS_KIND_OF],
                ConstraintsFactory.getConstraints(CoverMap.IDENTIFIER)),
            'benefitContracts': new ConstrainedMultiDimensionalParameter([[]],
                    [ReinsuranceContractBasedOn.CONTRACT],
                    ConstraintsFactory.getConstraints(ReinsuranceContractBasedOn.IDENTIFIER))]

    )


    public static final all = [NONE, ORIGINALCLAIMS, CONTRACTS, LEGALENTITIES, MATRIX]


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
            case CoverAttributeStrategyType.LEGALENTITIES:
                coverStrategy = new InwardLegalEntitiesCoverAttributeStrategy(
                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'],
                        legalEntityCoverMode: (LegalEntityCoverMode) parameters['legalEntityCoverMode'])
                break
            case CoverAttributeStrategyType.MATRIX:
                coverStrategy = new MatrixCoverAttributeStrategy(
                        flexibleCover: (ConstrainedMultiDimensionalParameter) parameters['flexibleCover'],
                        benefitContracts: (ConstrainedMultiDimensionalParameter) parameters['benefitContracts'])
                break
            default: throw new NotImplementedException("$type not implemented.")
        }
        return coverStrategy;
    }
}
