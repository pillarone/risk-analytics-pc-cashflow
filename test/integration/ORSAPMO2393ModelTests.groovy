import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2393ModelTests extends ModelTest {

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
        'TestORSAPMO2393Parameters'
    }

    String getParameterDisplayName() {
        'Segment Net Premium with Ceded Cover'
    }

    @Override
    int getIterationCount() {
        10
    }

    protected boolean shouldCompareResults() {
        true
    }
}
