import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2401NetModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2401ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'PMO-2401 RT'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2401NetParameters'
    }

    String getParameterDisplayName() {
        'RI Program with last contract based on net'
    }

    @Override
    int getIterationCount() {
        1
    }

    protected boolean shouldCompareResults() {
        true
    }
}
