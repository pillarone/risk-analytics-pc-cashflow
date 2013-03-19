import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO23902ModelTests extends ModelTest {

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
        'TestORSAPMO23902Parameters'
    }

    String getParameterDisplayName() {
        'Ceded Premium with Cl Gen Cover 2'
    }

    @Override
    int getIterationCount() {
        10
    }

    protected boolean shouldCompareResults() {
        true
    }
}
