import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class GIRAReservesModelTests extends ModelTest {

    Class getModelClass() {
        GIRAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestGIRAAggregateNoIdxResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Aggregate Gross Claims without Index Collection'
    }

    @Override
    String getParameterFileName() {
        'TestGIRAReservesParameters'
    }

    String getParameterDisplayName() {
        'Reserves'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
