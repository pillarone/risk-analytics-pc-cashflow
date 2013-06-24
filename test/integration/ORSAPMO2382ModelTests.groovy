import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2382ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestPMO2382LegalEntityResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Legal Entity Ultimates'
    }

    @Override
    String getParameterFileName() {
        'TestPMO2382LegalDiamondParameters'
    }

    String getParameterDisplayName() {
        'PMO 2382-LegalDiamond'
    }

    protected boolean shouldCompareResults() {
        true
    }

}
