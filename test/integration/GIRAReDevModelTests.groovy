import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRAReDevModelTests extends ModelTest {

    Class getModelClass() {
        GIRAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'GIRAAggregateResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Aggregate Gross Claims'
    }

    @Override
    String getParameterFileName() {
        'GIRAStochReDevParameters'
    }

    String getParameterDisplayName() {
        'Re (with dev)'
    }

    protected boolean shouldCompareResults() {
        true
    }
}