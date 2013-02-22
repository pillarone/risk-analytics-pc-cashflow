package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies

import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
*   author simon.parten @ art-allianz . com
 */
class AllTermAPLayersTest extends GroovyTestCase {

    void testAllTermLayers() {
        TermLayer layer = new TermLayer(10, 5 , 0.5)
        TermLayer layer1 = new TermLayer(10, 15 , 1)

        Collection<TermLayer> collection = new ArrayList<TermLayer>()
        collection << layer
        collection << layer1

        AllTermAPLayers allTermAPLayers = new AllTermAPLayers(collection)
        Map<Integer, Double> losses = [ (0I): 3d, (1I): 3d, (2I): 4d, (3I): 3d, (4I): 13d,(5I): 13d, ]

        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope (new DateTime(2012, 1, 1, 1, 0, 0, 0), 5)

        assert allTermAPLayers.incrementalTermLoss(losses, 25d, 4d, periodScope).getAdditionalPremium().additionalPremium == 0
        periodScope.prepareNextPeriod()
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, periodScope).getAdditionalPremium().additionalPremium == 0
        periodScope.prepareNextPeriod()
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, periodScope).getAdditionalPremium().additionalPremium == 1 * 0.5
        periodScope.prepareNextPeriod()
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, periodScope).getAdditionalPremium().additionalPremium == 4 * 0.5 - 0.5
        periodScope.prepareNextPeriod()
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, periodScope).getAdditionalPremium().additionalPremium == (10 * 0.5 + 7) - 2
        periodScope.prepareNextPeriod()
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, periodScope).getAdditionalPremium().additionalPremium == (10 * 0.5 + 10) - 12


    }

}
