import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2481ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2481ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Segments and Contracts (first splitted)'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2481Parameters'
    }

    String getParameterDisplayName() {
        'PMO-2481 Generator Cover'
    }

    @Override
    int getIterationCount() {
        1
    }

    protected boolean shouldCompareResults() {
        true
    }
}
