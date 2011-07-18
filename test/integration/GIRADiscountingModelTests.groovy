import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class GIRADiscountingModelTests extends ModelTest {

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
        'GIRADiscountingParameters'
    }

    String getParameterDisplayName() {
        'Discounting'
    }

    protected boolean shouldCompareResults() {
        true
    }
}
