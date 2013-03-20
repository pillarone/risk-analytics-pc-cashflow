package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuoteCapConstraints extends MDPConstraintsHelper   {

    public static final String IDENTIFIER = "Quote CAP Constraints"
    public static final String CONTRACT_PERIOD = 'Contract Period'
    public static final String QUOTA_SHARE = 'QS%'
    public static final String CAP = 'Cap'

    public static final Integer PERIOD_COLUMN_INDEX = 0
    public static final Integer QUOTE_COLUMN_INDEX = 1
    public static final Integer CAP_COLUMN_INDEX = 2

    public static final Class  PERIOD_CLASS = Integer
    public static final Class  QUOTE_CLASS = Double
    public static final Class  CAP_CLASS = Double

    public static final Map<Integer, Class> classMapper = [
            (QuoteCapConstraints.PERIOD_COLUMN_INDEX) : PERIOD_CLASS,
            (QuoteCapConstraints.QUOTE_COLUMN_INDEX)  : QUOTE_CLASS,
            (QuoteCapConstraints.CAP_COLUMN_INDEX)    : CAP_CLASS,
    ]

    public static List<String> columnHeaders = [CONTRACT_PERIOD, QUOTA_SHARE, CAP]
}
