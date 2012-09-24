package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.QuoteCapConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ProportionalContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.NonPropTemplateContractStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TemplateContractType extends AbstractParameterObjectClassifier {

    public static final TemplateContractType NONPROPORTIONAL = new TemplateContractType("non proportional", "NONPROPORTIONAL",
            ['termLimit': 0d,
                    'termExcess': 0d,
                    'termAP' : new ConstrainedMultiDimensionalParameter([[0d],[0d],[0d]],
                            AdditionalPremiumConstraints.columnHeaders, ConstraintsFactory.getConstraints(AdditionalPremiumConstraints.IDENTIFIER)),
                    'structure' : new ConstrainedMultiDimensionalParameter(
                            [[1],[1],[1d],[0d],[0d],[0d],[0d],[0d],['PREMIUM']],
                            LayerConstraints.columnHeaders, ConstraintsFactory.getConstraints(LayerConstraints.IDENTIFIER))
            ])
    public static final TemplateContractType PROPORTIONAL = new TemplateContractType('proportional', 'PROPORTIONAL',
            ['proportionalStructure': new ConstrainedMultiDimensionalParameter([[1],[0d],[0d]],
                    QuoteCapConstraints.columnHeaders, ConstraintsFactory.getConstraints(QuoteCapConstraints.IDENTIFIER)),
//                    'cedingCommissionType': CommissionType.getDefault()
            ]
    )

    public static final all = [NONPROPORTIONAL, PROPORTIONAL]

    protected static Map types = [:]
    static {
        TemplateContractType.all.each {
            TemplateContractType.types[it.toString()] = it
        }
    }

    private TemplateContractType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static TemplateContractType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IReinsuranceContractStrategy getDefault() {
        return new NonPropTemplateContractStrategy()
    }

    static IReinsuranceContractStrategy getStrategy(TemplateContractType type, Map parameters) {
        IReinsuranceContractStrategy contract
        switch (type) {
            case TemplateContractType.NONPROPORTIONAL:
                return new NonPropTemplateContractStrategy(
                        'termLimit': (double) parameters[NonPropTemplateContractStrategy.TERM_LIMIT],
                        'termExcess': (double) parameters[NonPropTemplateContractStrategy.TERM_EXCESS],
                        'termAP': (ConstrainedMultiDimensionalParameter) parameters[NonPropTemplateContractStrategy.TERM_AP],
                        'structure': (ConstrainedMultiDimensionalParameter) parameters[NonPropTemplateContractStrategy.STRUCTURE])
            case TemplateContractType.PROPORTIONAL:
                return new ProportionalContractStrategy(
                        'proportionalStructure': (ConstrainedMultiDimensionalParameter) parameters[ProportionalContractStrategy.PROPORTIONAL_STRUCTURE],
//                        'cedingCommissionType': (ICommissionParameterStrategy) parameters[ProportionalContractStrategy.CEDING_COMMISSION_TYPE]
                        )

            default:
                throw new IllegalArgumentException("Unknown contract strategy in " + this.toString());
        }
    }
}
