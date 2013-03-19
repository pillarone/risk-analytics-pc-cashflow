import models.orsa.ORSAModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ORSAPMO2375ModelTests extends ModelTest {

    Class getModelClass() {
        ORSAModel
    }

    @Override
    String getResultConfigurationFileName() {
        'TestORSAPMO2359ResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Segments and Contracts'
    }

    @Override
    String getParameterFileName() {
        'TestPMO2375ImplausibleStopLossNetIBNRParameters'
    }

    String getParameterDisplayName() {
        'PMO 2375 Implausible StopLoss Net IBNR'
    }

    @Override
    int getIterationCount() {
        1
    }

    protected boolean shouldCompareResults() {
        true
    }
}
