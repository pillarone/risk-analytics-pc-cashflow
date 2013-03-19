import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2391ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2336ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Aggregate Claims, Segments, R/I'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2391Parameters'
    }

    String getParameterDisplayName() {
        'Premium with Benefit Contracts'
    }

    @Override
    int getIterationCount() {
        10
    }

    protected boolean shouldCompareResults() {
        true
    }
}
