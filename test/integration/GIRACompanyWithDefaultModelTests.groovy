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
        'GIRALegalEntityDrillDownResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Legal Entity Drill Down'
    }

    @Override
    String getParameterFileName() {
        'GIRAMultiCompanyWithDefaultParameters'
    }

    String getParameterDisplayName() {
        'Multi Company with Default'
    }

    @Override
    int getIterationCount() {
        1
    }

    protected boolean shouldCompareResults() {
        true
    }
}