package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AdditionalPremiumConstraints extends MDPConstraintsHelper   {

    public static final String IDENTIFIER = "Additional Premium Constraints"

    public static final Integer LIMIT_COLUMN_INDEX = 0
    public static final Integer EXCESS_COLUMN_INDEX = 1
    public static final Integer RATE_COLUMN_INDEX = 2

    public static final Class  LIMIT_CLASS = Double
    public static final Class  EXCESS_CLASS = Double
    public static final Class  RATE_CLASS = Double

    public static final Map<Integer, Class> classMapper = [
            (AdditionalPremiumConstraints.LIMIT_COLUMN_INDEX)   : LIMIT_CLASS,
            (AdditionalPremiumConstraints.EXCESS_COLUMN_INDEX)  : EXCESS_CLASS,
            (AdditionalPremiumConstraints.RATE_COLUMN_INDEX)    : RATE_CLASS,
    ]

    public static List<String> columnHeaders = ['Limit','Excess','Rate']
}
