import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRASegmentsModelTests extends ModelTest {

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
        'GIRASegmentsParameters'
    }

    String getParameterDisplayName() {
        'Segment'
    }

    protected boolean shouldCompareResults() {
        true
    }
}