package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class LossAndAP {

    private final double loss;
    private final Collection<LayerAndAP> aps;

    public LossAndAP(double loss, Collection<LayerAndAP> aps) {
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

    public Collection<APSingleValuePacket> getPackets(APBasis apBasis, IPeriodCounter counter) {
        Collection<APSingleValuePacket> apSingleValuePackets = new ArrayList<APSingleValuePacket>();
        for (LayerAndAP apLayer : aps) {
            for (APSingleValuePacket aPremium : apLayer.getPackets(counter)) {
                if(aPremium.getAdditionalPremium().getPremiumType().equals(apBasis) && aPremium.getAdditionalPremium().getAdditionalPremium() > 0) {
                    apSingleValuePackets.add(aPremium);
                }
            }
        }
        return apSingleValuePackets;
    }

    public double getLoss() {
        return loss;
    }

    public Collection<LayerAndAP> getAps() {
        return aps;
    }

    @Override
    public String toString() {
        return "LossAndAP{" +
                "loss=" + loss +
                ", aps=" + getAdditionalPremium() +
                '}';
    }
}
