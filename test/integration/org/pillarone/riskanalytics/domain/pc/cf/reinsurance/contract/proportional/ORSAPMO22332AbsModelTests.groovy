package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import models.gira.GIRAModel
import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO22332AbsModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO22332ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'PMO2233 Test 2'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO22332AbsParameters'
    }

    String getParameterDisplayName() {
        'PMO233 Test 2 - SL Abs Param'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
