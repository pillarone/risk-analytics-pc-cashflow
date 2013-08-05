package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumStructureProfitCommissionConstraints extends MDPConstraintsHelper   {

    public static final String IDENTIFIER = "Premium Layer Comission Constraints"

    public static final Integer CONTRACT_PERIOD_COLUMN_INDEX = 0
    public static final Integer LAYER_COLUMN_INDEX = 1
    public static final Integer LIMIT_START_INDEX = 2
    public static final Integer LIMIT_TOP_BAND_INDEX = 3
    public static final Integer AP_PERC_OF_LIMIT_INDEX = 4

    public static final Class  CONTRACT_PERIOD_CLASS = Integer
    public static final Class  LAYER_CLASS = Integer
    public static final Class  LIMIT_START_CLASS = Double
    public static final Class  LIMIT_TOP_BAND_CLASS = Double
    public static final Class  AP_PERC_OF_LIMIT_CLASS = Double

    public static final Map<Integer, Class> classMapper = [
            (PremiumStructureProfitCommissionConstraints.CONTRACT_PERIOD_COLUMN_INDEX) : CONTRACT_PERIOD_CLASS,
            (PremiumStructureProfitCommissionConstraints.LAYER_COLUMN_INDEX)           : LAYER_CLASS,
            (PremiumStructureProfitCommissionConstraints.LIMIT_START_INDEX)   : LIMIT_START_CLASS,
            (PremiumStructureProfitCommissionConstraints.LIMIT_TOP_BAND_INDEX)   : LIMIT_TOP_BAND_CLASS,
            (PremiumStructureProfitCommissionConstraints.AP_PERC_OF_LIMIT_INDEX)   : AP_PERC_OF_LIMIT_CLASS,

    ]

    public static List<String> columnHeaders = ['Period','Layer', 'Claims as % of Total Premium (initial + reinstated)','% of premium as PC']
}