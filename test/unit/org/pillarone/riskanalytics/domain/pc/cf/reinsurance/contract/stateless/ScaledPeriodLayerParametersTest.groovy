package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis

/**
 * Created with IntelliJ IDEA.
 * User: sparten
 * Date: 22.09.12
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
class ScaledPeriodLayerParametersTest extends GroovyTestCase {

    private DateTime time = new DateTime(2010, 1, 1, 1,0 ,0,0)
    private IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(time, 2)

    private ScaledPeriodLayerParameters layerParameters = new ScaledPeriodLayerParameters()

    @Override
    protected void setUp() {
        super.setUp()    //To change body of overridden methods use File | Settings | File Templates.
        layerParameters.add(0, 1, 1, 0.10, 0.5, 0.20, 0.25, 0, APBasis.NCB)
        layerParameters.add(0, 2, 1, 0.6, 0, 0, 0, 0, APBasis.NCB)
        layerParameters.add(1, 1, 1, 0.15, 0, 0, 0, 0, APBasis.NCB)
    }

    void testGetLayersAbsScale() {

        AllPeriodUnderwritingInfoPacket allUWInfor = new AllPeriodUnderwritingInfoPacket()
        List<LayerParameters> firstPeriod = layerParameters.getLayers(0, counter, ExposureBase.ABSOLUTE, allUWInfor  )
        assert firstPeriod.get(0).getClaimExcess() == 0.1
        assert firstPeriod.get(0).getClaimLimit() == 0.5
        assert firstPeriod.get(0).getLayerPeriodExcess() == 0.2
        assert firstPeriod.get(0).getLayerPeriodLimit() == 0.25
        assert  firstPeriod.get(1).getClaimExcess() == 0.6

        List<LayerParameters> secondPeriod = layerParameters.getLayers(1, counter, ExposureBase.ABSOLUTE, allUWInfor  )
        assert secondPeriod.get(0).getClaimExcess() == 0.15
    }

    void testPremiumScale() {
        AllPeriodUnderwritingInfoPacket allUWInfor = new AllPeriodUnderwritingInfoPacket()
        UnderwritingInfoPacket infoPacket1 = new UnderwritingInfoPacket()
        infoPacket1.setPremiumWritten(10)
        infoPacket1.setDate(time)

        UnderwritingInfoPacket infoPacket2 = new UnderwritingInfoPacket()
        infoPacket2.setPremiumWritten(20)
        infoPacket2.setDate(time.plusYears(1))

        TreeMap<DateTime, UnderwritingInfoPacket> map = new TreeMap<DateTime, UnderwritingInfoPacket>()
        map.put(time, infoPacket1)
        map.put(time.plusYears(1), infoPacket2)

        allUWInfor.setUnderwritingInfoPerPeriod(map)

        List<LayerParameters> firstPeriod = layerParameters.getLayers(0, counter, ExposureBase.PREMIUM_WRITTEN, allUWInfor  )
        assert firstPeriod.get(0).getClaimExcess() == 1
        assert firstPeriod.get(0).getClaimLimit() == 5
        assert firstPeriod.get(0).getLayerPeriodExcess() == 2
        assert firstPeriod.get(0).getLayerPeriodLimit() == 2.5

        List<LayerParameters> secondPeriod = layerParameters.getLayers(1, counter, ExposureBase.PREMIUM_WRITTEN, allUWInfor  )
        assert  secondPeriod.get(0).getClaimExcess() == 3

    }



}
