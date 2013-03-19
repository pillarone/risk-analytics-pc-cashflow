import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2347ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2347ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'PMO-2347-RT'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2347Parameters'
    }

    String getParameterDisplayName() {
        'Quote and Retro Contract combined'
    }

    protected boolean shouldCompareResults() {
        false
    }
}
