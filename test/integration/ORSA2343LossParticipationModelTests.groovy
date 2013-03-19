import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSA2343LossParticipationModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2343ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'PMO-2343-RT'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2343Parameters'
    }

    String getParameterDisplayName() {
        'Missing cession in case of loss participation'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
