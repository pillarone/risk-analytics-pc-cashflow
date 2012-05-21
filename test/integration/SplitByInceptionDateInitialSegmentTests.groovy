import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-2075
class SplitByInceptionDateInitialSegmentTests extends ModelTest {

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
        'TestGIRAInitialSegmentParameters'
    }

    String getParameterDisplayName() {
        'Initial Segments'
    }

    @Override
    protected boolean shouldCompareResults() {
        true
    }
}
