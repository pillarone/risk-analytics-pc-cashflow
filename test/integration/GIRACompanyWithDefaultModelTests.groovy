import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRACompanyWithDefaultModelTests extends ModelTest {

    Class getModelClass() {
        GIRAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestGIRALegalEntityDrillDownResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Legal Entity, Drill Down'
    }

    @Override
    String getParameterFileName() {
        'TestGIRAMultiCompanyWithDefaultParameters'
    }

    String getParameterDisplayName() {
        'Multi Company with Default'
    }

    @Override
    int getIterationCount() {
        1
    }

    //todo(sku): re-enable net financials currently wrong
//    protected boolean shouldCompareResults() {
//        true
//    }
}