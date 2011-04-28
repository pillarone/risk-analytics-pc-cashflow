

import models.nonLifeCashflow.NonLifeCashflowModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowIdxModelTests extends ModelTest {

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
        'NonLifeCashflowIdxParameters'
    }

    String getParameterDisplayName() {
        'Index and Pattern'
    }

    protected boolean shouldCompareResults() {
        true
    }
}