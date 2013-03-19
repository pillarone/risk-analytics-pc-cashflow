import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2344ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2344ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'PMO-2344-RT'
    }

    @Override
    String getParameterFileName() {
        'TestORSAPMO2344QuotaChainParameters'
    }

    String getParameterDisplayName() {
        'Chain of Quota Shares'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
