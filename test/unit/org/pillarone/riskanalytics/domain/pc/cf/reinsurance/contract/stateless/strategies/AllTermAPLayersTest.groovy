package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies

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

        assert allTermAPLayers.incrementalTermLoss(losses, 25d, 4d, 0).getAdditionalPremium().additionalPremium == 0
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, 1).getAdditionalPremium().additionalPremium == 0
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, 2).getAdditionalPremium().additionalPremium == 1 * 0.5
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, 3).getAdditionalPremium().additionalPremium == 4 * 0.5 - 0.5
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, 4).getAdditionalPremium().additionalPremium == (10 * 0.5 + 7) - 2
        assert allTermAPLayers.incrementalTermLoss(losses, 25, 4, 5).getAdditionalPremium().additionalPremium == (10 * 0.5 + 10) - 12


    }

}
