package models.nonLifeCashflow

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.DynamicClaimsGenerator
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.joda.time.Period

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowModel extends StochasticModel {

    DynamicClaimsGenerator claimsGenerators
    @Override
    void initComponents() {
        claimsGenerators = new DynamicClaimsGenerator()

        addStartComponent claimsGenerators
    }

    @Override
    void wireComponents() {
    }

    @Override
    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        return new LimitedContinuousPeriodCounter(new DateTime(2011,1,1,0,0,0,0), Period.years(1), 1)
    }


}
