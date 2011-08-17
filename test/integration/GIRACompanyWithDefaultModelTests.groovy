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
        'GIRAAggregateNoIdxResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Aggregate Gross Claims without Index Collection'
    }

    @Override
    String getParameterFileName() {
        'GIRAMultiCompanyWithDefaultParameters'
    }

    String getParameterDisplayName() {
        'Multi Company with Default'
    }

    protected boolean shouldCompareResults() {
        false   // todo(sku): reactivate compare
    }
}