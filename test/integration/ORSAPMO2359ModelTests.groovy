import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2359ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2359ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Segments and Contracts'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2359Parameters'
    }

    String getParameterDisplayName() {
        'PMO-2359 Five Contracts'
    }

    @Override
    int getIterationCount() {
        10
    }

    protected boolean shouldCompareResults() {
        true
    }
}
