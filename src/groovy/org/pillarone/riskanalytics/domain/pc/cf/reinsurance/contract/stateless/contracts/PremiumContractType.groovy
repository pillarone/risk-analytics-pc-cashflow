package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts

import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IPremiumContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureAPConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureProfitCommissionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureReinstatementConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.QuoteCapConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.NonPropTemplateContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.PremiumContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ProportionalContractStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumContractType extends AbstractParameterObjectClassifier {

    public static final PremiumContractType XOL = new PremiumContractType("non proportional", "NONPROPORTIONAL",
            [
                    (PremiumContractStrategy.STRUCTURE) : new ConstrainedMultiDimensionalParameter(
                            [[1],[1],[1d],[0d],[0d],[0d],[0d],[0d]],
                            PremiumStructureConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureConstraints.IDENTIFIER)),
                    (PremiumContractStrategy.REINSTATEMENTS) : new ConstrainedMultiDimensionalParameter(
                            [[1],[1],[0d],[0d]],
                            PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER)),
                    (PremiumContractStrategy.ADDITIONALPREMIUMS) : new ConstrainedMultiDimensionalParameter(
                            [[1],[1],[0d],[0d],[0d]],
                            PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER)),
                    (PremiumContractStrategy.PROFITCOMMISSION) : new ConstrainedMultiDimensionalParameter(
                            [[1],[1],[0d],[0d],],
                            PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER)),
                    (PremiumContractStrategy.TERM_LIMIT) : 0d,
                    (PremiumContractStrategy.TERM_EXCESS) : 0d,
                    (PremiumContractStrategy.ORDERING_METHOD) : ContractOrderingMethod.PAID,

            ])
//    public static final PremiumContractType PROPORTIONAL = new PremiumContractType('proportional', 'PROPORTIONAL',
//            ['proportionalStructure': new ConstrainedMultiDimensionalParameter([[1],[0d],[0d]],
//                    QuoteCapConstraints.columnHeaders, ConstraintsFactory.getConstraints(QuoteCapConstraints.IDENTIFIER)),
//                    'cedingCommissionType': CommissionType.getDefault()
//            ]
//    )

    public static final all = [
            XOL,
//            PROPORTIONAL
    ]

    protected static Map types = [:]
    static {
        PremiumContractType.all.each {
            PremiumContractType.types[it.toString()] = it
        }
    }

    private PremiumContractType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static PremiumContractType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IPremiumContractStrategy getDefault() {
        return new PremiumContractStrategy(
                new ConstrainedMultiDimensionalParameter(
                        [[1],[1],[1d],[0d],[0d],[0d],[0d],[0d]],
                        PremiumStructureConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureConstraints.IDENTIFIER)),
                new ConstrainedMultiDimensionalParameter(
                        [[1],[1],[0d],[0d]],
                        PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER)),
                new ConstrainedMultiDimensionalParameter(
                        [[1],[1],[0d],[0d],[0d]],
                        PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER)),
                new ConstrainedMultiDimensionalParameter(
                        [[1],[1],[0d],[0d],],
                        PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER)),
                        0d,
                        0d,
                        ContractOrderingMethod.PAID,
                )

    }

    static IPremiumContractStrategy getStrategy(PremiumContractType type, Map parameters) {
        switch (type) {
            case PremiumContractType.XOL:
                return new PremiumContractStrategy(
                        (ConstrainedMultiDimensionalParameter) parameters.get(PremiumContractStrategy.STRUCTURE) ,
                        (ConstrainedMultiDimensionalParameter) parameters.get(PremiumContractStrategy.REINSTATEMENTS) ,
                        (ConstrainedMultiDimensionalParameter) parameters.get(PremiumContractStrategy.ADDITIONALPREMIUMS) ,
                        (ConstrainedMultiDimensionalParameter) parameters.get(PremiumContractStrategy.PROFITCOMMISSION) ,
                        (Double) parameters.get(PremiumContractStrategy.TERM_LIMIT) ,
                        (Double) parameters.get(PremiumContractStrategy.TERM_EXCESS) ,
                        (ContractOrderingMethod) parameters.get(PremiumContractStrategy.ORDERING_METHOD) ,
                )
//            case PremiumContractType.PROPORTIONAL:
//                return new ProportionalContractStrategy(
//                        'proportionalStructure': (ConstrainedMultiDimensionalParameter) parameters[ProportionalContractStrategy.PROPORTIONAL_STRUCTURE],
//                        'cedingCommissionType': (ICommissionParameterStrategy) parameters[ProportionalContractStrategy.CEDING_COMMISSION_TYPE]
//                        )

            default:
                throw new IllegalArgumentException("Unknown contract strategy in " + this.toString());
        }
    }
}
