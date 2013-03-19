package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies

/**
*   author simon.parten @ art-allianz . com
 */
class TermLayerTest extends GroovyTestCase {

    void testIncurred(){
        TermLayer layer = new TermLayer(10, 5, 0.5)
        assert layer.getLossFromThisLayer(7) == (7 - 5) * 0.5
        assert layer.getLossFromThisLayer(16) == (10) * 0.5
        assert layer.getLossFromThisLayer(4) == (0) * 0.5
    }
}
