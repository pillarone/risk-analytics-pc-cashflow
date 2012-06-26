import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-2072
class GIRAOverReservingModelTests extends ModelTest {

    Class getModelClass() {
        GIRAModel
    }

    String getResultConfigurationFileName() {
        'TestGIRASplitByInceptionResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Premium Reserve Risk Triangle'
    }

    String getParameterFileName() {
        'TestGIRAOverReservingParameters'
    }

    String getParameterDisplayName() {
        'PMO 2072 over reserving'
    }

    @Override
    protected boolean shouldCompareResults() {
        true
    }
}
