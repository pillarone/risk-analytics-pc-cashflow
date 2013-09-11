package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IRiLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.PeriodLayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PeriodLayerParametersTests extends GroovyTestCase {

    

    void testUsage() {
        PeriodLayerParameters params = new PeriodLayerParameters()
        params.add(0, 1, 0.5, 25000000, 50000000, 0, 50000000, 1d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 50000000, 50000000, -1d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 100000000, 50000000, -0.01d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 150000000, 50000000, 0d, APBasis.PREMIUM)
        params.add(1, 1, 1, 75000000, 75000000, 0, 75000000, 1d, APBasis.PREMIUM)
        params.add(1, 1, 1, 75000000, 75000000, 75000000, 75000000, 0.75d, APBasis.PREMIUM)
        params.add(1, 1, 1, 75000000, 750000000, 150000000, 75000000, 0d, APBasis.PREMIUM)
        params.add(2, 1, 1, 0, 10000000, 0, 100000000, 0d, APBasis.PREMIUM)

        List<LayerParameters> layersPeriod0 = params.getLayers(0)
        assertEquals 'one layer in P0', 1, layersPeriod0.size()
        assertEquals 'period excess layer 1 in P0', 0, layersPeriod0[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P0', 200000000, layersPeriod0[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 3, layersPeriod0[0].additionalPremiums.size()
        assertEquals 'additional premium percentages', [1, -1, -0.01], layersPeriod0[0].additionalPremiums.additionalPremium
        assertEquals 'additional premium period excess', [0, 50000000, 100000000], layersPeriod0[0].additionalPremiums.periodExcess

        List<LayerParameters> layersPeriod1 = params.getLayers(1)
        assertEquals 'one layer in P1', 1, layersPeriod1.size()
        assertEquals 'period excess layer 1 in P1', 0, layersPeriod1[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P1', 225000000, layersPeriod1[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 2, layersPeriod1[0].additionalPremiums.size()
        assertEquals 'additional premium percentages', [1, 0.75], layersPeriod1[0].additionalPremiums.additionalPremium
        assertEquals 'additional premium period excess', [0, 75000000], layersPeriod1[0].additionalPremiums.periodExcess

        List<LayerParameters> layersPeriod2 = params.getLayers(2)
        assertEquals 'one layer in P2', 1, layersPeriod2.size()
        assertEquals 'period excess layer 1 in P2', 0, layersPeriod2[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P2', 100000000, layersPeriod2[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 0, layersPeriod2[0].additionalPremiums.size()
    }

    void testPeriodGap() {
        PeriodLayerParameters params = new PeriodLayerParameters()
        params.add(0, 1, 0.5, 25000000, 50000000, 0, 50000000, 1d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 50000000, 50000000, -1d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 100000000, 50000000, -0.01d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 150000000, 50000000, 0d, APBasis.PREMIUM)
        params.add(2, 1, 1, 0, 10000000, 0, 100000000, 0d, APBasis.PREMIUM)

        List<LayerParameters> layersPeriod0 = params.getLayers(0)
        assertEquals 'one layer in P0', 1, layersPeriod0.size()
        assertEquals 'period excess layer 1 in P0', 0, layersPeriod0[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P0', 200000000, layersPeriod0[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 3, layersPeriod0[0].additionalPremiums.size()
        assertEquals 'additional premium percentages', [1, -1, -0.01], layersPeriod0[0].additionalPremiums.additionalPremium
        assertEquals 'additional premium period excess', [0, 50000000, 100000000], layersPeriod0[0].additionalPremiums.periodExcess

        List<LayerParameters> layersPeriod1 = params.getLayers(1)
        assertEquals 'one layer in P1', 1, layersPeriod1.size()
        assertEquals 'period excess layer 1 in P1', 0, layersPeriod1[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P1', 200000000, layersPeriod1[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 3, layersPeriod1[0].additionalPremiums.size()
        assertEquals 'additional premium percentages', [1, -1, -0.01], layersPeriod1[0].additionalPremiums.additionalPremium
        assertEquals 'additional premium period excess', [0, 50000000, 100000000], layersPeriod1[0].additionalPremiums.periodExcess

        List<LayerParameters> layersPeriod2 = params.getLayers(2)
        assertEquals 'one layer in P2', 1, layersPeriod2.size()
        assertEquals 'period excess layer 1 in P2', 0, layersPeriod2[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P2', 100000000, layersPeriod2[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 0, layersPeriod2[0].additionalPremiums.size()

        List<LayerParameters> layersPeriod3 = params.getLayers(3)
        assertEquals 'one layer in P3', 1, layersPeriod3.size()
        assertEquals 'period excess layer 1 in P3', 0, layersPeriod3[0].getLayerPeriodExcess()
        assertEquals 'period limit layer 1 in P3', 100000000, layersPeriod3[0].getLayerPeriodLimit()
        assertEquals 'additional premium bands', 0, layersPeriod3[0].additionalPremiums.size()
    }

    void testLayerParameterLargeLimits() {
        LayerParameters params = new LayerParameters(1, 0, 0, 1,1)
        assert params.getClaimLimit() == Double.MAX_VALUE

        params.addAdditionalPremium(30, 0, 0.5, APBasis.LOSS)
        assert params.getLayerPeriodLimit() == Double.MAX_VALUE

//        Once we have set the limit infinite, it must stay there
        params.addAdditionalPremium( 50 , 0, 0.5, APBasis.LOSS )
        assert  params.getLayerPeriodLimit() == Double.MAX_VALUE

//        Or throw an excpetion if we think the user input is non-sensical.
        shouldFail {
            params.addAdditionalPremium( 50 , 50, 0.5, APBasis.LOSS )
        }
    }

    void testMappingToNewDataStructure() {
        PeriodLayerParameters params = new PeriodLayerParameters()
        params.add(0, 1, 0.5, 25000000, 50000000, 0, 50000000, 1d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 50000000, 50000000, -1d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 100000000, 50000000, -0.01d, APBasis.PREMIUM)
        params.add(0, 1, 0.5, 25000000, 50000000, 150000000, 50000000, 0d, APBasis.PREMIUM)
        params.add(2, 1, 1, 0, 10000000, 0, 100000000, 0d, APBasis.PREMIUM)

        List<IRiLayer> someLayers = params.getContractLayers(0)
        assert someLayers.size() == 2

        assert someLayers.findAll {  it -> it.getYearLayerIdentifier().equals(new YearLayerIdentifier(0, 1)) } . size() == 1
        assert someLayers.findAll {  it -> it.getYearLayerIdentifier().equals(new YearLayerIdentifier(0, 1)) } . get(0).getLegacyAdditionalPremiums().size() == 3
    }

}