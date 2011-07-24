import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRAPtfModelTests extends ModelTest {

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
        'GIRAPtfParameters'
    }

    String getParameterDisplayName() {
        'Gross Portfolio'
    }

    protected boolean shouldCompareResults() {
        false
    }

    @Override
    int getIterationCount() {
        2
    }


}