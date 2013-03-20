package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class LayerAndAP {

    private final LayerParameters layerParameters;
    private final Collection<AdditionalPremium> additionalPremiums;

    public LayerAndAP(LayerParameters layerParameters, Collection<AdditionalPremium> additionalPremiums) {
        this.layerParameters = layerParameters;
        this.additionalPremiums = additionalPremiums;
    }

    public LayerParameters getLayerParameters() {
        return layerParameters;
    }

    public Collection<AdditionalPremium> getAdditionalPremiums() {
        return additionalPremiums;
    }

    @Override
    public String toString() {
        return "LayerAndAP{" +
                "layerParameters=" + layerParameters.toString() +
                ", additionalPremiums=" + additionalPremiums +
                '}';
    }
}
