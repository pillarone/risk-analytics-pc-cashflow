import models.nonLifeCashflow.NonLifeCashflowModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowDevModelTests extends ModelTest {

    Class getModelClass() {
        NonLifeCashflowModel
    }

    @Override
    String getResultConfigurationFileName() {
        'NonLifeCashflowAggregateResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Aggregate Gross Claims'
    }

    @Override
    String getParameterFileName() {
        'NonLifeCashflowDevParameters'
    }

    String getParameterDisplayName() {
        'Developed Claims'
    }

    protected boolean shouldCompareResults() {
        true
    }
}