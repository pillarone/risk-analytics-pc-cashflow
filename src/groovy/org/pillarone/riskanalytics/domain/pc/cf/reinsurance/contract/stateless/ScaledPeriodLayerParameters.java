package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;


import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;

import java.util.*;

/**
 * Helper class used for layers for NonPropTemplateContractStrategy. Missing gaps in the period sequence are filled
 * with values of nearest previous period (floorEntry).
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ScaledPeriodLayerParameters extends PeriodLayerParameters {

    private ExposureBase exposureBase = null;
    private IPeriodCounter counter = null;
    private AllPeriodUnderwritingInfoPacket uwInfo = null;

    public ScaledPeriodLayerParameters(PeriodLayerParameters layerParams) {
        super(layerParams);
    }

    public ScaledPeriodLayerParameters() {
    }

    @Override
    public List<LayerParameters> getLayers(int period) {

        if(exposureBase == null){
            throw new SimulationException("Scaled parameters incorrectly setup. Check exposure base ");
        }
        if( counter == null){
            throw new SimulationException("Scaled parameters incorrectly setup. Check counter ");
        }
        if( uwInfo == null){
            throw new SimulationException("Scaled parameters incorrectly setup. Check uwInfo ");
        }
        return getLayers(period, counter, exposureBase, uwInfo);
    }

    public List<LayerParameters> getLayers(int period, IPeriodCounter iPeriodCounter, ExposureBase exposureBase, AllPeriodUnderwritingInfoPacket infoPacket) {
        List<UnderwritingInfoPacket> relevantPackets = new ArrayList<UnderwritingInfoPacket>();
        for (Map.Entry<DateTime, UnderwritingInfoPacket> entry : infoPacket.getUnderwritingInfoPerPeriod().entrySet()) {
            if (iPeriodCounter.belongsToPeriod(entry.getKey()) == period) {
                relevantPackets.add(entry.getValue());
            }
        }
        double scaleFactor = 0;
        switch (exposureBase) {
            case ABSOLUTE:
                scaleFactor = 1;
                break;
            case PREMIUM_WRITTEN:
                scaleFactor = UnderwritingInfoUtils.sumPremiumWritten(relevantPackets);
                break;
            case NUMBER_OF_POLICIES:
                scaleFactor = UnderwritingInfoUtils.sumNumberOfPolicies(relevantPackets);
                break;
            case SUM_INSURED:
                throw new NotImplementedException("Sum insured not implemented");
            default:
                throw new SimulationException("");
        }

        List<LayerParameters> originalParams = super.getLayers(period);
        List<LayerParameters> scaledParams = new ArrayList<LayerParameters>();
        for (LayerParameters originalParam : originalParams) {
            LayerParameters tempParam = new LayerParameters(
                    originalParam.getShare(),
                    originalParam.getClaimExcess() * scaleFactor,
                    originalParam.getClaimLimit() * scaleFactor);

            List<AdditionalPremiumPerLayer> premiums = originalParam.getAdditionalPremiums();
            if (premiums.size() == 0) {
                tempParam.addAdditionalPremium(
                        originalParam.getLayerPeriodExcess() * scaleFactor,
                        originalParam.getLayerPeriodLimit() * scaleFactor,
                        0d,
                        APBasis.LOSS
                );
            }
            for (AdditionalPremiumPerLayer premium : premiums) {
                tempParam.addAdditionalPremium(
                        premium.getPeriodExcess() * scaleFactor,
                        premium.getPeriodLimit() * scaleFactor,
                        premium.getAdditionalPremium(),
                        premium.getBasis()
                );
            }
            scaledParams.add(tempParam);
        }
        return scaledParams;
    }

    public ExposureBase getExposureBase() {
        return exposureBase;
    }

    public void setExposureBase(ExposureBase exposureBase) {
        this.exposureBase = exposureBase;
    }

    public IPeriodCounter getCounter() {
        return counter;
    }

    public void setCounter(IPeriodCounter counter) {
        this.counter = counter;
    }

    public AllPeriodUnderwritingInfoPacket getUwInfo() {
        return uwInfo;
    }

    public void setUwInfo(AllPeriodUnderwritingInfoPacket uwInfo) {
        this.uwInfo = uwInfo;
    }
}
