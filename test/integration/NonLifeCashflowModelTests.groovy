import models.nonLifeCashflow.NonLifeCashflowModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowModelTests extends ModelTest {

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
        'NonLifeCashflowParameters'
    }

    String getParameterDisplayName() {
        'Claims'
    }

    protected boolean shouldCompareResults() {
        true
    }
}