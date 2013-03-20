package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CoverStrategyType extends AbstractParameterObjectClassifier {

    public static final CoverStrategyType NONE = new CoverStrategyType('None', 'None', [:])
    public static final CoverStrategyType ALLGROSSCLAIMS = new CoverStrategyType('all gross claims', 'ALLGROSSCLAIMS', [:])
//    public static final CoverStrategyType ALLCLAIMFILTERS = new CoverStrategyType('all claim filters', 'ALLCLAIMFILTERS', [:])
    public static final CoverStrategyType SELECTED = new CoverStrategyType('selected', 'SELECTED',
            ['grossClaims': new ComboBoxTableMultiDimensionalParameter([],
                    ['Covered gross claims'], IPerilMarker.class),
//                    'claimFilters': new ComboBoxTableMultiDimensionalParameter([],
//                            ['Covered gross claims'], IClaimFilterMarker.class),
                    'structures': new ConstrainedMultiDimensionalParameter([[], []],
                            [ContractBasedOn.CONTRACT, ContractBasedOn.BASED_ON],
                            ConstraintsFactory.getConstraints(ContractBasedOn.IDENTIFIER))])

    public static final all = [
            NONE,
            ALLGROSSCLAIMS,
//            ALLCLAIMFILTERS,
            SELECTED,
    ]

    protected static Map types = [:]
    static {
        CoverStrategyType.all.each {
            CoverStrategyType.types[it.toString()] = it
        }
    }

    private CoverStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static CoverStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static ICoverStrategy getDefault() {
        return new NoneCoverStrategy()
    }

    static ICoverStrategy getStrategy(CoverStrategyType type, Map parameters) {
        switch (type) {
            case CoverStrategyType.NONE:
                return new NoneCoverStrategy();
            case CoverStrategyType.ALLGROSSCLAIMS:
                return new AllGrossClaimsCoverStrategy()
//            case CoverStrategyType.ALLCLAIMFILTERS:
//                return new AllClaimFiltersCoverStrategy()
            case CoverStrategyType.SELECTED:
                return new SelectedCoverStrategy(
                        grossClaims: (ComboBoxTableMultiDimensionalParameter) parameters['grossClaims'],
//                        claimFilters: (ComboBoxTableMultiDimensionalParameter) parameters['claimFilters'],
                        structures: (ConstrainedMultiDimensionalParameter) parameters['structures'])
            default :
                throw new IllegalArgumentException("Unknown cover strategy in " + this.toString());
        }
    }
}