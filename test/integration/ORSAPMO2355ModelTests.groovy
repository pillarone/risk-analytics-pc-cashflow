import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2355ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2355ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Segments and Structures'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2355Parameters'
    }

    String getParameterDisplayName() {
        'Alternative Aggregate Reserve Risk'
    }

    @Override
    int getIterationCount() {
        1
    }

    protected boolean shouldCompareResults() {
        true
    }
}
