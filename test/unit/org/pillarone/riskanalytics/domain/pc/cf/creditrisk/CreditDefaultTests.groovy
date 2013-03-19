package org.pillarone.riskanalytics.domain.pc.cf.creditrisk

import org.pillarone.riskanalytics.domain.utils.constant.Rating

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CreditDefaultTests extends GroovyTestCase {

    void testUsage() {
        CreditDefault creditDefault = new CreditDefault()
        assertEquals 'AAA', 0.00002, creditDefault.probabilities().getDefaultProbability(Rating.AAA)
        assertEquals 'no default', 0.0, creditDefault.probabilities().getDefaultProbability(Rating.NO_DEFAULT)
    }
}
