package org.pillarone.riskanalytics.domain.pc.cf.exceptionUtils

import org.pillarone.riskanalytics.domain.pc.cf.global.SimulationConstants

/**
*   author simon.parten @ art-allianz . com
 */
class ExceptionUtilsTest extends GroovyTestCase {


    void testCheckValue() {
        assert ExceptionUtils.getCheckValue(0d) == SimulationConstants.EPSILON
        assert ExceptionUtils.getCheckValue(10d) == SimulationConstants.EPSILON + 1/1000
        assert ExceptionUtils.getCheckValue(-10d) == SimulationConstants.EPSILON + 1/1000
        assert ExceptionUtils.getCheckValue(1000d) == SimulationConstants.EPSILON + 3*3/1000
    }


}

