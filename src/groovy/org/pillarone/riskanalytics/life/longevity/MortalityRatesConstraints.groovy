package org.pillarone.riskanalytics.life.longevity

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper
import org.pillarone.riskanalytics.life.Gender

/**
 *   author simon.parten @ art-allianz . com
 */
class MortalityRatesConstraints extends MDPConstraintsHelper {

        public static final String IDENTIFIER = "Mortality  Constraints"

        public static final Integer AGE_COLUMN = 0
        public static final Integer RATE_COLUMN = 1

        public static final Class AGE = Double
        public static final Class RATE = Double

    public static final Map<Integer, Class> classMapper = [
        (MortalityRatesConstraints.AGE_COLUMN )              : AGE ,
        (MortalityRatesConstraints.RATE_COLUMN )           : RATE ,
    ]

    public static List<String> columnnHeaders = ['Age', 'Rate']

    public static List<List<Double>> initialList = [  [0.5], [0.5] ]

}
