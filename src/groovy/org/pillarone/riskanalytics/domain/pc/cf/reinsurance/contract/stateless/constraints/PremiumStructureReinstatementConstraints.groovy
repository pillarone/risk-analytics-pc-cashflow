package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumStructureReinstatementConstraints extends MDPConstraintsHelper   {

    public static final String IDENTIFIER = "Premium Layer Reinstatement Constraints"

    public static final Integer CONTRACT_PERIOD_COLUMN_INDEX = 0
    public static final Integer LAYER_COLUMN_INDEX = 1
//    public static final Integer NUMBER_REINSTATEMENTS_INDEX = 2
    public static final Integer REINSTATEMENT_PRECENTAGE_INDEX = 2

    public static final Class  CONTRACT_PERIOD_CLASS = Integer
    public static final Class  LAYER_CLASS = Integer
//    public static final Class  NUMBER_REINSTATEMENTS_CLASS = Double
    public static final Class  REINSTATEMENT_PRECENTAGE_CLASS = Double

    public static final Map<Integer, Class> classMapper = [
            (PremiumStructureReinstatementConstraints.CONTRACT_PERIOD_COLUMN_INDEX) : CONTRACT_PERIOD_CLASS,
            (PremiumStructureReinstatementConstraints.LAYER_COLUMN_INDEX)           : LAYER_CLASS,
//            (PremiumStructureReinstatementConstraints.NUMBER_REINSTATEMENTS_INDEX)   : NUMBER_REINSTATEMENTS_CLASS,
            (PremiumStructureReinstatementConstraints.REINSTATEMENT_PRECENTAGE_INDEX)   : REINSTATEMENT_PRECENTAGE_CLASS,

    ]

    public static List<String> columnHeaders = ['Period','Layer', 'Reinstatement Percentage']
}