package models.nonLifeCashflow

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.DynamicClaimsGenerator
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Patterns

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowModel extends StochasticModel {

    Patterns patterns
    DynamicClaimsGenerator claimsGenerators

    @Override
    void initComponents() {
        patterns = new Patterns()
        claimsGenerators = new DynamicClaimsGenerator()

        addStartComponent patterns
    }

    @Override
    void wireComponents() {
    }

    @Override
    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        return new LimitedContinuousPeriodCounter(new DateTime(2011,1,1,0,0,0,0), Period.years(1), 1)
    }


}
