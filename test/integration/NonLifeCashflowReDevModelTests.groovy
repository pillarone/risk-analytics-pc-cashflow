import models.nonLifeCashflow.NonLifeCashflowModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowReDevModelTests extends ModelTest {

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
        'NonLifeCashflowStochReDevParameters'
    }

    String getParameterDisplayName() {
        'Re (with dev)'
    }

    protected boolean shouldCompareResults() {
        true
    }
}