package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumStructureConstraints extends MDPConstraintsHelper   {

    public static final String IDENTIFIER = "Premium Layer Constraints"

    public static final Integer CONTRACT_PERIOD_COLUMN_INDEX = 0
    public static final Integer LAYER_COLUMN_INDEX = 1
    public static final Integer SHARE_COLUMN_INDEX = 2
    public static final Integer CLAIM_LIMIT_COLUMN_INDEX = 3
    public static final Integer CLAIM_EXCESS_COLUMN_INDEX = 4
    public static final Integer PERIOD_LIMIT_COLUMN_INDEX = 5
    public static final Integer PERIOD_EXCESS_COLUMN_INDEX = 6
    public static final Integer INITIAL_PREMIUM = 7

    public static final Class  CONTRACT_PERIOD_CLASS = Integer
    public static final Class  LAYER_CLASS = Integer
    public static final Class  SHARE_CLASS = Double
    public static final Class  PERIOD_LIMIT_CLASS = Double
    public static final Class  PERIOD_EXCESS_CLASS = Double
    public static final Class  CLAIM_LIMIT_CLASS = Double
    public static final Class  CLAIM_EXCESS_CLASS = Double
    public static final Class  INITIAL_PREMIUM_CLASS = Double

    public static final Map<Integer, Class> classMapper = [
            (PremiumStructureConstraints.CONTRACT_PERIOD_COLUMN_INDEX) : CONTRACT_PERIOD_CLASS,
            (PremiumStructureConstraints.LAYER_COLUMN_INDEX)           : LAYER_CLASS,
            (PremiumStructureConstraints.SHARE_COLUMN_INDEX)           : SHARE_CLASS,
            (PremiumStructureConstraints.CLAIM_LIMIT_COLUMN_INDEX)     : CLAIM_LIMIT_CLASS,
            (PremiumStructureConstraints.CLAIM_EXCESS_COLUMN_INDEX)    : CLAIM_EXCESS_CLASS,
            (PremiumStructureConstraints.PERIOD_LIMIT_COLUMN_INDEX)    : PERIOD_LIMIT_CLASS,
            (PremiumStructureConstraints.PERIOD_EXCESS_COLUMN_INDEX)   : PERIOD_EXCESS_CLASS,
            (PremiumStructureConstraints.INITIAL_PREMIUM)               : INITIAL_PREMIUM_CLASS,

    ]

    public static List<String> columnHeaders = ['Period','Layer','Share','Vertical Limit',
            'Vertical Excess','Period Limit','Period Excess', 'Initial Premium']
}