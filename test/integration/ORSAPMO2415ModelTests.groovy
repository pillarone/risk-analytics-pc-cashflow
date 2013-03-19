import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2415ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2415ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Retro Claims by Period'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2415Parameters'
    }

    String getParameterDisplayName() {
        'Z Para PMO 2415 SpecADCLPT'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
