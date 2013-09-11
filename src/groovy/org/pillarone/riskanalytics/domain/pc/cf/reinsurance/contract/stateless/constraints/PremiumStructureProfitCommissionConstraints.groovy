package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumStructureProfitCommissionConstraints extends MDPConstraintsHelper   {

    public static final String IDENTIFIER = "Premium Layer Comission Constraints"

    public static final Integer CONTRACT_PERIOD_COLUMN_INDEX = 0
    public static final Integer LAYER_COLUMN_INDEX = 1
    public static final Integer CLAIMS_PERCENTAGE_OF_TOTAL_PREMIUM_INDEX = 2
    public static final Integer PERCENTAGE_OF_PREMIUM_PC_INDEX = 3

    public static final Class  CONTRACT_PERIOD_CLASS = Integer
    public static final Class  LAYER_CLASS = Integer
    public static final Class  CLAIMS_PERCENTAGE_OF_TOTAL_PREMIUM_CLASS = Double
    public static final Class  PERCENTAGE_OF_PREMIUM_PC_CLASS = Double

    public static final Map<Integer, Class> classMapper = [
            (PremiumStructureProfitCommissionConstraints.CONTRACT_PERIOD_COLUMN_INDEX) : CONTRACT_PERIOD_CLASS,
            (PremiumStructureProfitCommissionConstraints.LAYER_COLUMN_INDEX)           : LAYER_CLASS,
            (PremiumStructureProfitCommissionConstraints.CLAIMS_PERCENTAGE_OF_TOTAL_PREMIUM_INDEX)   : CLAIMS_PERCENTAGE_OF_TOTAL_PREMIUM_CLASS,
            (PremiumStructureProfitCommissionConstraints.PERCENTAGE_OF_PREMIUM_PC_INDEX)   : PERCENTAGE_OF_PREMIUM_PC_CLASS,

    ]

    public static List<String> columnHeaders = ['Period','Layer', 'Claims as % of Total Premium (initial + reinstated)','% of premium as PC']
}