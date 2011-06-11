

import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRAIdxModelTests extends ModelTest {

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
        'GIRAIdxParameters'
    }

    String getParameterDisplayName() {
        'Index and Pattern'
    }

    protected boolean shouldCompareResults() {
        true
    }
}