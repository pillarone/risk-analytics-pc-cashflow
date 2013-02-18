package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLossAndAP {

    private final Collection<IncurredLossAndLayer> loss;
    private final Collection<LayerAndAP> aps;

    public IncurredLossAndAP(Collection<IncurredLossAndLayer> loss, Collection<LayerAndAP> aps) {
        this.loss = loss;
        this.aps = aps;
    }

    public double getAdditionalPremium() {
        double ap = 0d;
        for ( LayerAndAP aLayer : aps) {
            for (AdditionalPremium aPremium : aLayer.getAdditionalPremiums()) {
                ap += aPremium.getAdditionalPremium();
            }
        }
        return ap;
    }

    public Collection<APSingleValuePacket> getPackets(APBasis apBasis, IPeriodCounter counter, IReinsuranceContractMarker contractMarker) {
        Collection<APSingleValuePacket> apSingleValuePackets = new ArrayList<APSingleValuePacket>();
        for (LayerAndAP apLayer : aps) {
            for (APSingleValuePacket aPremium : apLayer.getPackets(counter)) {
                if(aPremium.getAdditionalPremium().getPremiumType().equals(apBasis) && aPremium.getAdditionalPremium().getAdditionalPremium() > 0) {
                    aPremium.addMarker(IReinsuranceContractMarker.class, contractMarker );
                    apSingleValuePackets.add(aPremium);
                }
            }
        }
        return apSingleValuePackets;
    }

    public Collection<AdditionalPremium> getAddtionalPremiums(IPeriodCounter counter, IReinsuranceContractMarker contractMarker) {
        Collection<AdditionalPremium> apSingleValuePackets = Lists.newArrayList();
        for (LayerAndAP apLayer : aps) {
            apSingleValuePackets.addAll(apLayer.getAdditionalPremiums());
        }
        return apSingleValuePackets;

    }

    public double getLoss() {
        double totalLoss = 0d;
        for (IncurredLossAndLayer los : loss) {
            totalLoss += los.getLoss();
        }
        return totalLoss;
    }

    public Collection<LayerAndAP> getAps() {
        return Collections.unmodifiableCollection( aps );
    }

    public IncurredLossAndLayer getLayerAndIncurredLoss(LayerParameters.LayerIdentifier layerIdentifier) {
        for (IncurredLossAndLayer los : loss) {
            if(los.getLayerParameters().getLayerIdentifier().equals(layerIdentifier) ) {
                return los;
            }
        }
        throw new SimulationException("Didn't find incurred layer for " + layerIdentifier.toString());

    }

    @Override
    public String toString() {
        return "IncurredLossAndAP{" +
                "loss=" + getLoss() +
                ", aps=" + getAdditionalPremium() +
                '}';
    }
}
