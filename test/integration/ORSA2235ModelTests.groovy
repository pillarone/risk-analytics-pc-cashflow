import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ORSA2235ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2235ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Retro by Period'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2235AbsParameters'
    }

    String getParameterDisplayName() {
        'PMO-2235'
    }

    protected boolean shouldCompareResults() {
        true
    }
}