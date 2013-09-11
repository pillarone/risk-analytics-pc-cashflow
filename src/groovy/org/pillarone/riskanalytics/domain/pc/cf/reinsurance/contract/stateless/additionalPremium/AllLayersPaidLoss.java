package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.Collection;
import java.util.Collections;

/**
 * author simon.parten @ art-allianz . com
 */
public class AllLayersPaidLoss {

    private final Collection<LayerAndPaidLoss> paidLossesByLayer;

    public AllLayersPaidLoss(Collection<LayerAndPaidLoss> paidLossesByLayer) {
        this.paidLossesByLayer = paidLossesByLayer;
    }

    public double paidLossAllLayers() {
        double paid = 0d;
        for (LayerAndPaidLoss layerAndPaidLoss : paidLossesByLayer) {
            paid += layerAndPaidLoss.getPaidLossAfterAnnualStructureWithShare();
        }
        return paid;
    }

    public Collection<LayerAndPaidLoss> getPaidLossesByLayer() {
        return Collections.unmodifiableCollection(paidLossesByLayer) ;
    }

    public LayerAndPaidLoss getLayerOrNull(LayerIdentifier layerIdentifier) {
        for (LayerAndPaidLoss layerAndPaidLoss : paidLossesByLayer) {
            if(layerIdentifier.equals(layerAndPaidLoss.getLayerParameters().getLayerIdentifier())) {
                return layerAndPaidLoss;
            }
        }
        return null;
    }
}
