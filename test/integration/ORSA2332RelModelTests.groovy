import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSA2332RelModelTests extends ModelTest {

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
        'TestORSAPMO22332RelParameters'
    }

    String getParameterDisplayName() {
        'PMO233 Test 2 - SL Rel Param'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
