package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import sun.misc.PerfCounter
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

/**
*   author simon.parten @ art-allianz . com
 */
class AggregateActualClaimsStrategyTest extends GroovyTestCase {

    void testReadParametersChecks(){

        IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(new DateTime(2012, 1, 1, 0, 0, 0, 0), 3)
        ConstrainedMultiDimensionalParameter history = new ConstrainedMultiDimensionalParameter(
                [[],[],[],[]],
                AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER));;
        AggregateActualClaimsStrategy strategy = new AggregateActualClaimsStrategy(history: history )
        strategy.lazyInitHistoricClaimsPerContractPeriod(counter, new DateTime(2013, 1, 1, 1, 0, 0, 0), PayoutPatternBase.PERIOD_START_DATE)


//        Fail on reporting period
        shouldFail {
            ConstrainedMultiDimensionalParameter aHistory = new ConstrainedMultiDimensionalParameter(
                    [[1],[1],[1],[new DateTime(2011, 1, 1, 0, 0, 0, 0)]],
                    AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                    ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER));;
            AggregateActualClaimsStrategy aStrategy = new AggregateActualClaimsStrategy(history: aHistory )
            aStrategy.lazyInitHistoricClaimsPerContractPeriod(counter, new DateTime(2013, 1, 1, 1, 0, 0, 0), PayoutPatternBase.PERIOD_START_DATE)
        }

//        Fail on -ve reported
        shouldFail {
            ConstrainedMultiDimensionalParameter aHistory = new ConstrainedMultiDimensionalParameter(
                    [[1],[1],[-1],[new DateTime(2012, 1, 1, 0, 0, 0, 0)]],
                    AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                    ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER));;
            AggregateActualClaimsStrategy aStrategy = new AggregateActualClaimsStrategy(history: aHistory )
            aStrategy.lazyInitHistoricClaimsPerContractPeriod(counter, new DateTime(2013, 1, 1, 1, 0, 0, 0), PayoutPatternBase.PERIOD_START_DATE)
        }

//        -ve paid
        shouldFail {
            ConstrainedMultiDimensionalParameter aHistory = new ConstrainedMultiDimensionalParameter(
                    [[1],[-1],[1],[new DateTime(2012, 1, 1, 0, 0, 0, 0)]],
                    AggregateHistoricClaimsConstraints.COLUMN_HEADERS,
                    ConstraintsFactory.getConstraints(AggregateHistoricClaimsConstraints.IDENTIFIER));;
            AggregateActualClaimsStrategy aStrategy = new AggregateActualClaimsStrategy(history: aHistory )
            aStrategy.lazyInitHistoricClaimsPerContractPeriod(counter, new DateTime(2013, 1, 1, 1, 0, 0, 0), PayoutPatternBase.PERIOD_START_DATE)
        }
    }


}
