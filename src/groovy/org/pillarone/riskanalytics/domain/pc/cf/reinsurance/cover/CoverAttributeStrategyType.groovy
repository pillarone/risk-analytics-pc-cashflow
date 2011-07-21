package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CoverAttributeStrategyType extends AbstractParameterObjectClassifier {

    public static final CoverAttributeStrategyType ALL = new CoverAttributeStrategyType("all", "ALL",
            ['reservesIndexed': IncludeType.NOTINCLUDED])
    public static final CoverAttributeStrategyType NONE = new CoverAttributeStrategyType("none", "NONE", [:])
    public static final CoverAttributeStrategyType GROSSPERILS = new CoverAttributeStrategyType(
            'gross perils', 'GROSSPERILS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], IPerilMarker)])
    public static final CoverAttributeStrategyType GROSSSEGMENTS = new CoverAttributeStrategyType(
            'gross segments', 'GROSSSEGMENTS',
            ['segments':new ComboBoxTableMultiDimensionalParameter([], ['Covered Segments'], ISegmentMarker)])
    public static final CoverAttributeStrategyType GROSSPERILSSEGMENTS = new CoverAttributeStrategyType(
            'gross perils, segments', 'GROSSPERILSSEGMENTS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], IPerilMarker),
             'segments':new ComboBoxTableMultiDimensionalParameter([], ['Covered Segments'], ISegmentMarker),
             'connection':LogicArguments.AND])
    public static final CoverAttributeStrategyType GROSSLEGALENTITIES = new CoverAttributeStrategyType(
            'gross legal entities', 'GROSSLEGALENTITIES',
            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Covered Legal Entities'], ILegalEntityMarker)])
    public static final CoverAttributeStrategyType CONTRACTS = new CoverAttributeStrategyType(
            'contracts', 'CONTRACTS', [
                    'contracts': new ConstrainedMultiDimensionalParameter([[], []],
                            [ReinsuranceContractBasedOn.CONTRACT, ReinsuranceContractBasedOn.BASED_ON],
                            ConstraintsFactory.getConstraints(ReinsuranceContractBasedOn.IDENTIFIER))])
    public static final CoverAttributeStrategyType CONTRACTSPERILS = new CoverAttributeStrategyType(
            'contracts and perils', 'CONTRACTSPERILS', [
                    'contracts': new ConstrainedMultiDimensionalParameter([[], []],
                            [ReinsuranceContractBasedOn.CONTRACT, ReinsuranceContractBasedOn.BASED_ON],
                            ConstraintsFactory.getConstraints(ReinsuranceContractBasedOn.IDENTIFIER)),
                    'perils': new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], IPerilMarker.class)])
    public static final CoverAttributeStrategyType CONTRACTSSEGMENTS = new CoverAttributeStrategyType(
            'contracts and segments', 'CONTRACTSSEGMENTS', [
                    'contracts': new ConstrainedMultiDimensionalParameter([[], []],
                            [ReinsuranceContractBasedOn.CONTRACT, ReinsuranceContractBasedOn.BASED_ON],
                            ConstraintsFactory.getConstraints(ReinsuranceContractBasedOn.IDENTIFIER)),
                    'segments': new ComboBoxTableMultiDimensionalParameter([], ['Covered Segments'], ISegmentMarker.class)])
    public static final CoverAttributeStrategyType INWARDLEGALENTITIES = new CoverAttributeStrategyType(
            'inward legal entities', 'INWARDLEGALENTITIES',
            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Covered Legal Entities'], ILegalEntityMarker)])
    public static final CoverAttributeStrategyType GROSSINWARDLEGALENTITIES = new CoverAttributeStrategyType(
            'gross, inward legal entities', 'GROSSINWARDLEGALENTITIES',
            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Covered Legal Entities'], ILegalEntityMarker)])



    public static final all = [ALL, NONE, GROSSPERILS, GROSSSEGMENTS, GROSSPERILSSEGMENTS, GROSSLEGALENTITIES,
                                CONTRACTS, CONTRACTSPERILS, CONTRACTSSEGMENTS, INWARDLEGALENTITIES, GROSSINWARDLEGALENTITIES]


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
        return new AllCoverAttributeStrategy(reserves : IncludeType.NOTINCLUDED)
    }

    public static ICoverAttributeStrategy getStrategy(CoverAttributeStrategyType type, Map parameters) {
        ICoverAttributeStrategy coverStrategy ;
        switch (type) {
            case CoverAttributeStrategyType.ALL:
                coverStrategy = new AllCoverAttributeStrategy(reserves : (IncludeType) parameters['reserves'])
                break
            case CoverAttributeStrategyType.NONE:
                coverStrategy = new NoneCoverAttributeStrategy()
                break
            case CoverAttributeStrategyType.GROSSPERILS:
                coverStrategy = new GrossPerilsCoverAttributeStrategy(perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'])
                break
            case CoverAttributeStrategyType.GROSSSEGMENTS:
                coverStrategy = new GrossSegmentsCoverAttributeStrategy(segments: (ComboBoxTableMultiDimensionalParameter) parameters['segments'])
                break
            case CoverAttributeStrategyType.GROSSPERILSSEGMENTS:
                coverStrategy = new GrossPerilsSegmentsCoverAttributeStrategy(
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'],
                        segments: (ComboBoxTableMultiDimensionalParameter) parameters['segments'],
                        connection: (LogicArguments) parameters['connection'])
                break
            case CoverAttributeStrategyType.GROSSLEGALENTITIES:
                coverStrategy = new GrossLegalEntitiesCoverAttributeStrategy(
                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'])
                break
            case CoverAttributeStrategyType.CONTRACTS:
                coverStrategy = new ContractsCoverAttributeStrategy(
                        contracts: (ConstrainedMultiDimensionalParameter) parameters['contracts'])
                break
            case CoverAttributeStrategyType.CONTRACTSPERILS:
                coverStrategy = new ContractsPerilsCoverAttributeStrategy(
                        contracts: (ConstrainedMultiDimensionalParameter) parameters['contracts'],
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'])
                break
            case CoverAttributeStrategyType.CONTRACTSSEGMENTS:
                coverStrategy = new ContractsSegmentsCoverAttributeStrategy(
                        contracts: (ConstrainedMultiDimensionalParameter) parameters['contracts'],
                        segments: (ComboBoxTableMultiDimensionalParameter) parameters['segments'])
                break
            case CoverAttributeStrategyType.INWARDLEGALENTITIES:
                coverStrategy = new InwardLegalEntitiesCoverAttributeStrategy(
                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'])
                break
            case CoverAttributeStrategyType.GROSSINWARDLEGALENTITIES:
                coverStrategy = new GrossInwardLegalEntitiesCoverAttributeStrategy(
                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'])
                break
        }
        return coverStrategy;
    }
}
