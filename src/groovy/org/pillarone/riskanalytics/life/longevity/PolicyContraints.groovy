package org.pillarone.riskanalytics.life.longevity

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper
import org.pillarone.riskanalytics.life.Gender

/**
 *   author simon.parten @ art-allianz . com
 */
class PolicyContraints extends MDPConstraintsHelper {

        public static final String IDENTIFIER = "Longevity Policy Constraints"

        public static final Integer AGE_COLUMN = 0
        public static final Integer GENDER_COLUMN = 1
        public static final Integer RETIREMENT_YEAR_COLUMN = 2
        public static final Integer MEMBER_ANNUITY_COLUMN = 3
        public static final Integer SPOUSE_ANNUITY_COLUMN = 4
        public static final Integer SPOUSE_AGE_COLUMN = 5
        public static final Integer SPOUSE_GENDER_COLUMN = 6

        public static final Class AGE = Integer
        public static final Class GENDER = Gender
        public static final Class RETIREMENT_YEAR = Integer
        public static final Class MEMBER_ANNUITY_VALUE = Double
        public static final Class SPOUSE_ANNUITY = Double
        public static final Class SPOUSE_AGE = Integer
        public static final Class SPOUSE_GENDER = Gender

    public static final Map<Integer, Class> classMapper = [
        (PolicyContraints.AGE_COLUMN )              : AGE ,
        (PolicyContraints.GENDER_COLUMN )           : GENDER ,
        (PolicyContraints.RETIREMENT_YEAR_COLUMN )  : RETIREMENT_YEAR ,
        (PolicyContraints.MEMBER_ANNUITY_COLUMN )   : MEMBER_ANNUITY_VALUE ,
        (PolicyContraints.SPOUSE_ANNUITY_COLUMN )   : SPOUSE_ANNUITY ,
        (PolicyContraints.SPOUSE_AGE_COLUMN )       : SPOUSE_AGE ,
        (PolicyContraints.SPOUSE_GENDER_COLUMN )     : SPOUSE_GENDER ,
    ]

    public static List<String> columnnHeaders = ['Age', 'Gender','Retirement Year','Member Annuity','Spouse Annuity','Spouse Age','Spouse Gender', ]

}
